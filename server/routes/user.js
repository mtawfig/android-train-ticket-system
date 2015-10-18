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

module.exports = function(server) {
  server.route({
    method: 'GET',
    path: '/restricted',
    config: { auth: 'jwt' },
    handler: function(request, reply) {
      reply({text: 'You used a Token!'})
        .header("Authorization", request.headers.authorization);
    }
  });

};
