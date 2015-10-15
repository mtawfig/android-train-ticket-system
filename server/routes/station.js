'use strict';

var Station = require('../models/Station'),
  Connection = require('../models/Connection'),
  Timetable = require('../models/Timetable'),
  Joi = require('joi'),
  schema = require('../schema'),
  Boom = require('boom'),
  Promise = require('bluebird'),
  _ = require('lodash'),
  moment = require('moment');


var getDuration = function(step) {
  var parseStart = (step.hoursStart < 10 ? 'H' : 'HH') + ' ' + (step.minutesStart < 10 ? 'm' : 'mm');
  var start = step.hoursStart + ' ' + step.minutesStart;

  var parseEnd = (step.hoursEnd < 10 ? 'H' : 'HH') + ' ' + (step.minutesEnd < 10 ? 'm' : 'mm');
  var end = step.hoursEnd + ' ' + step.minutesEnd;

  return moment(end, parseEnd).diff(moment(start, parseStart), 'minutes');
};

var buildItinerary = function(timetables) {

  function createStep(timetable) {
    return {
      startStationId: timetable.fromStationId,
      endStationId: timetable.toStationId,
      hoursStart: timetable.hoursStart,
      minutesStart: timetable.minutesStart,
      hoursEnd: timetable.hoursEnd,
      minutesEnd: timetable.minutesEnd,
      line: timetable.line,
      numberOfStops: 1
    }
  }

  var currentStep = createStep(timetables[0]);

  var itinerary = [currentStep];

  _.rest(timetables).forEach(function(timetable) {
    if (timetable.line === currentStep.line) {
      currentStep.numberOfStops += 1;
      currentStep.endStationId = timetable.toStationId;
      currentStep.hoursEnd = timetable.hoursEnd;
      currentStep.minutesEnd = timetable.minutesEnd;
    } else {
      currentStep.wait = getDuration({
        hoursStart: currentStep.hoursEnd, minutesStart: currentStep.minutesEnd,
        hoursEnd: timetable.hoursStart, minutesEnd: timetable.minutesStart
      });
      currentStep.duration = getDuration(currentStep);

      currentStep = createStep(timetable);
      itinerary.push(currentStep);
    }
  });

  var lastStep = _.last(itinerary);
  lastStep.duration = getDuration(lastStep);

  return Promise.map(itinerary, function(step) {
    var stationIds = [step.startStationId, step.endStationId];
    return Station.query()
      .whereIn('stationId', stationIds)
      .then(function(stations) {
        if (stations[0].stationId === step.startStationId) {
          step.startStation = stations[0];
          step.endStation = stations[1];
        } else {
          step.endStation = stations[0];
          step.startStation = stations[1];
        }
        delete step.startStationId;
        delete step.endStationId;
        return step;
      })
  });
};

var calculateCost = function(itinerary) {
  return itinerary.reduce(function(cost, step) {
    return cost + 100 + step.numberOfStops * 50;
  }, 0);
};

module.exports = function (server) {
  server.route({
    method: 'GET',
    path: '/stations',
    config: {},
    handler: function (request, reply) {
      Station.query().eager('connections')
        .then(function (stations) {
          reply(stations);
        })
        .catch(function (reason) {
          reply(Boom.notFound(reason));
        });
    }
  });

  server.route({
    method: 'GET',
    path: '/stations/{fromStationId}/to/{toStationId}',
    config: {
      validate: {
        params: {
          fromStationId: schema.station.id,
          toStationId: schema.station.id
        },
        query: {
          startDate: schema.station.date.optional()
        }
      }
    },
    handler: function (request, reply) {
      var fromStationId = request.params.fromStationId;
      var toStationId = request.params.toStationId;
      var startDate = request.query.startDate ? new Date(request.query.startDate) : new Date();

      Connection.getPath(fromStationId, toStationId)
        .then(function(path) {
          return Timetable.getTimetablesForPath(path, startDate);
        })
        .then(function(timetables) {
          return buildItinerary(timetables)
        })
        .then(function(itinerary) {
          var firstStep = _.first(itinerary);
          var lastStep = _.last(itinerary);
          var startAndEndTime = {
            hoursStart: firstStep.hoursStart,
            minutesStart: firstStep.minutesStart,
            hoursEnd: lastStep.hoursEnd,
            minutesEnd: lastStep.minutesEnd
          };
          reply(
            _.extend(startAndEndTime, {
              duration: getDuration(startAndEndTime),
              cost: calculateCost(itinerary),
              steps: itinerary
            })
          );
        })
        .catch(function (reason) {
          reply(Boom.notFound(reason));
        });
    }
  });
};
