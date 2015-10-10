'use strict';

var Station = require('../models/Station'),
  Joi = require('joi'),
  schema = require('../schema'),
  Boom = require('boom'),
  Promise = require('bluebird');

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
};
