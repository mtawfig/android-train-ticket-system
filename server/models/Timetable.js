'use strict';

var Model = require('objection').Model;
var Promise = require('bluebird');
var _ = require('lodash');
var moment = require('moment');
var Connection = require('./Connection.js');
var Station = require('./Station.js');
var Ticket = require('./Ticket.js');

var Knex = require('knex');
var knexConfig = require('../knexfile');
var knex = Knex(knexConfig.development);

/**
 * @extends Model
 * @constructor
 */
function Timetable() {
  Model.apply(this, arguments);
}

Model.extend(Timetable);
module.exports = Timetable;

Timetable.tableName = 'Timetable';
Timetable.idColumn = 'timetableId';

Timetable.relationMappings = {
  fromStation: {
    relation: Model.OneToOneRelation,
    modelClass: __dirname + '/Station',
    join: {
      from: 'Timetable.fromStationId',
      to: 'Station.stationId'
    }
  },
  toStation: {
    relation: Model.OneToOneRelation,
    modelClass: __dirname + '/Station',
    join: {
      from: 'Timetable.toStationId',
      to: 'Station.stationId'
    }
  }
};


Timetable.getTimetablesForPath = function (path, startTime) {
  var hours = startTime.getHours();
  var minutes = startTime.getMinutes();

  var bestTimetable = [];

  var getBest = function(path, hours, minutes) {
    if (path.length === 1) {
      return bestTimetable;
    }

    var fromStationId = path[0].stationId;
    var toStationId = path[1].stationId;

    return knex('Timetable')
      .where('Timetable.fromStationId', '=', fromStationId)
      .andWhere('Timetable.toStationId', '=', toStationId)
      .andWhere(function () {
        this
          .where(function () {
            this.where('hoursStart', '=', hours)
              .andWhere('minutesStart', '>=', minutes)
          })
          .orWhere('hoursStart', '>', hours)
      })
      .orderByRaw('hoursStart ASC, minutesStart ASC')
      .first()
      .innerJoin('Connection', function() {
        this.on('Timetable.fromStationId', 'Connection.fromStationId')
          .andOn('Timetable.toStationId', 'Connection.toStationId')
      })
      .then(function (best) {
        if (!best) {
          // skips to next day
          return getBest(path, 0, 0);
        }
        bestTimetable.push(best);
        return getBest(_.rest(path), best.hoursEnd, best.minutesEnd)
      })
  };

  return getBest(path, hours, minutes);
};

var buildSubObjectsAndOmitKeys = function(omitList, result) {
  return _.reduce(result, function(newResult, n, key) {
    var split = key.split(':');
    var subKey;
    if (split.length === 2) {
      key = split[0];
      subKey = split[1];
      if (!newResult[key]) {
        newResult[key] = {};
      }
      newResult[key][subKey] = n;
    } else if (omitList.indexOf(key) === -1) {
      newResult[key] = n;
    }
    return newResult;
  }, {});
};

Timetable.getTimetable = function (departingStationId) {
  return knex('Timetable')
    .distinct('tripId', 'tripStepNumber')
    .where('Timetable.fromStationId', departingStationId)
    .then(function(distinctDepartures) {
      var query = knex.select([
          "Timetable.fromStationId",
          "Timetable.toStationId",
          "hoursStart",
          "minutesStart",
          "hoursEnd",
          "minutesEnd",
          "tripId",
          "tripStepNumber",
          "direction",
          "line",
          "FromStation.stationId as fromStation:stationId",
          "FromStation.name as fromStation:name",
          "FromStation.labelRotationDegrees as fromStation:labelRotationDegrees",
          "FromStation.latitude as fromStation:latitude",
          "FromStation.longitude as fromStation:longitude",
          "ToStation.stationId as toStation:stationId",
          "ToStation.name as toStation:name",
          "ToStation.labelRotationDegrees as toStation:labelRotationDegrees",
          "ToStation.latitude as toStation:latitude",
          "ToStation.longitude as toStation:longitude"
        ])
        .from('Timetable')
        .innerJoin('Connection', function() {
          this.on('Timetable.fromStationId', 'Connection.fromStationId')
            .andOn('Timetable.toStationId', 'Connection.toStationId')
        })
        .innerJoin('Station AS FromStation', 'Timetable.fromStationId', 'FromStation.stationId')
        .innerJoin('Station AS ToStation', 'Timetable.toStationId', 'ToStation.stationId')
        .orderBy('tripId ASC, tripStepNumber ASC');

      return _.reduce(distinctDepartures, function(query, departure) {
        return query.orWhere(function() {
          this.where('tripId', '=', departure.tripId)
            .andWhere('tripStepNumber', '>=', departure.tripStepNumber)
        });
      }, query);
    })
    .then(function(results) {
      return _.chain(results)
        .map(buildSubObjectsAndOmitKeys.bind(null, ['fromStationId', 'toStationId']))
        .groupBy(function(result) {
          return result.line + ':' + result.direction;
        })
        .map(function(trips) {
          var firstOfTrip = trips[Object.keys(trips)[0]];
          return {
            line: firstOfTrip.line,
            direction: firstOfTrip.direction,
            tripsByNumber: _.chain(trips)
              .groupBy(function(lineResult) {
                return lineResult.tripId;
              })
              .values()
              .value()
          }
        })
        .value();
    });
};

var getDuration = function(step) {
  var parseStart = (step.hoursStart < 10 ? 'H' : 'HH') + ' ' + (step.minutesStart < 10 ? 'm' : 'mm');
  var start = step.hoursStart + ' ' + step.minutesStart;

  var parseEnd = (step.hoursEnd < 10 ? 'H' : 'HH') + ' ' + (step.minutesEnd < 10 ? 'm' : 'mm');
  var end = step.hoursEnd + ' ' + step.minutesEnd;

  return moment(end, parseEnd).diff(moment(start, parseStart), 'minutes');
};

var buildItinerary = function(timetables, date) {

  function createStep(timetable) {
    return {
      startStationId: timetable.fromStationId,
      endStationId: timetable.toStationId,
      hoursStart: timetable.hoursStart,
      minutesStart: timetable.minutesStart,
      hoursEnd: timetable.hoursEnd,
      minutesEnd: timetable.minutesEnd,
      line: timetable.line,
      numberOfStops: 1,
      tripId: timetable.tripId,
      fromTripStepNumber: timetable.tripStepNumber,
      toTripStepNumber: timetable.tripStepNumber
    }
  }

  var currentStep = createStep(timetables[0]);

  var startingTime = new Date(
    date.getFullYear(), date.getMonth(), date.getDate(),
    currentStep.hoursStart, currentStep.minutesStart, 0, 0
  );

  var itineraryDate;

  if (startingTime > date) {
    itineraryDate = moment(date).startOf('day').toDate();
  } else {
    itineraryDate = moment(date).add(1, 'day').startOf('day').toDate();
  }

  var itinerary = {
    date: itineraryDate,
    steps: [currentStep]
  };

  _.rest(timetables).forEach(function(timetable) {
    if (timetable.line === currentStep.line) {
      currentStep.numberOfStops += 1;
      currentStep.endStationId = timetable.toStationId;
      currentStep.hoursEnd = timetable.hoursEnd;
      currentStep.minutesEnd = timetable.minutesEnd;
      currentStep.toTripStepNumber = timetable.tripStepNumber;
    } else {
      currentStep.wait = getDuration({
        hoursStart: currentStep.hoursEnd, minutesStart: currentStep.minutesEnd,
        hoursEnd: timetable.hoursStart, minutesEnd: timetable.minutesStart
      });
      currentStep.duration = getDuration(currentStep);

      currentStep = createStep(timetable);
      itinerary.steps.push(currentStep);
    }
  });

  var lastStep = _.last(itinerary.steps);
  lastStep.duration = getDuration(lastStep);

  return Promise.each(itinerary.steps, function(step) {
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
      })
      .then(function() {
        return Ticket.getNumberOfFreeSeats(step, moment(date).startOf('day').toDate());
      })
      .then(function(freeSeats) {
        step.freeSeats = freeSeats;
        return step;
      })
  })
    .then(function() {
      return itinerary;
    });
};

var calculateCost = function(itinerary) {
  return itinerary.steps.reduce(function(cost, step) {
    return cost + 100 + step.numberOfStops * 50;
  }, 0);
};

Timetable.getItinerary = function(fromStationId, toStationId, date) {
  return Connection.getPath(fromStationId, toStationId)
    .then(function(path) {
      return Timetable.getTimetablesForPath(path, date);
    })
    .then(function(timetables) {
      return buildItinerary(timetables, date)
    })
    .then(function(itinerary) {
      var firstStep = _.first(itinerary.steps);
      var lastStep = _.last(itinerary.steps);
      var startAndEndTime = {
        hoursStart: firstStep.hoursStart,
        minutesStart: firstStep.minutesStart,
        hoursEnd: lastStep.hoursEnd,
        minutesEnd: lastStep.minutesEnd
      };
      return _.extend(startAndEndTime, {
        date: itinerary.date,
        duration: getDuration(startAndEndTime),
        cost: calculateCost(itinerary),
        steps: itinerary.steps
      });
    })
};
