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


module.exports = function (server) {
  server.route({
    method: 'GET',
    path: '/stations',
    config: {
      auth: false
    },
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
    path: '/stations/{stationId}/timetable',
    config: {
      auth: false,
      validate: {
        params: {
          stationId: schema.station.id
        }
      }
    },
    handler: function (request, reply) {
      var stationId = request.params.stationId;

      Timetable.getTimetable(stationId)
        .then(function(timetables) {
          reply(timetables);
        });
    }
  });

  server.route({
    method: 'GET',
    path: '/stations/{fromStationId}/to/{toStationId}',
    config: {
      auth: false,
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

      Timetable.getItinerary(fromStationId, toStationId, startDate)
        .then(function(itinerary) {
          reply(itinerary);
        })
        .catch(function (reason) {
          reply(Boom.notFound(reason));
        });
    }
  });
};
