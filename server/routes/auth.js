'use strict';

var User = require('../models/User'),
  Joi = require('joi'),
  Boom = require('boom'),
  bcrypt = require('bcrypt'),
  JWT = require('jsonwebtoken');

module.exports = function(server) {
  server.route({
    method: 'POST',
    path: '/login',
    config: {
      auth: false,
      validate: {
        payload: {
          email: Joi.string().email().required(),
          password: Joi.string().min(3).max(256).required()
        }
      }
    },
    handler: function (request, reply) {

      var session = {
        iat: new Date().getTime()
      };

      if (request.payload && request.payload.email && request.payload.password) {
        User.query()
          .where('email', request.payload.email)
          .first()
          .then(function(user) {

            if (user && bcrypt.compareSync(request.payload.password, user.password)) {
              delete user.password;
              session.user = user;

              var token = JWT.sign(session, process.env.JWT_SECRET);

              reply({token: token});
            } else {
              reply(Boom.badRequest('Bad user or password'));
            }
          })
      }
    }
  });

};
