'use strict';

var Station = require('../models/Station'),
  Connection = require('../models/Connection'),
  Joi = require('joi'),
  schema = require('../schema'),
  Boom = require('boom'),
  Promise = require('bluebird'),
  _ = require('lodash');

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
      var fromStationId = encodeURIComponent(request.params.fromStationId);
      var toStationId = encodeURIComponent(request.params.toStationId);
      Connection.getPath(fromStationId, toStationId)
        .then(function(stations) {
          reply(stations);
        })
        .catch(function (reason) {
          reply(Boom.notFound(reason));
        });
    }
  });
};
