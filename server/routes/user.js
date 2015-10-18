'use strict';

var User = require('../models/User');
var Boom = require('boom');
var bcrypt = require('bcrypt');
var JWT = require('jsonwebtoken');
var schema = require('../schema/index.js');
var _ = require('lodash');

var loginHandler = function(request, reply) {
  User.query()
    .where('email', request.payload.email)
    .first()
    .then(function(user) {
      if (user && bcrypt.compareSync(request.payload.password, user.password)) {
        var credentials = {
          iat: new Date().getTime(),
          user: user
        };

        var token = JWT.sign(credentials, process.env.JWT_SECRET);

        reply({
          token: token,
          user: _.omit(user, ['password', 'role'])
        });

      }
      else {
        reply(Boom.badRequest('Bad user or password'));
      }
    })
};

module.exports = function(server) {
  server.route({
    method: 'POST',
    path: '/login',
    config: {
      auth: false,
      validate: {
        payload: {
          email: schema.user.email.required(),
          password: schema.user.password.required()
        }
      }
    },
    handler: loginHandler
  });

  server.route({
    method: 'POST',
    path: '/register',
    config: {
      auth: false,
      validate: {
        payload: {
          email: schema.user.email.required(),
          password: schema.user.password.required(),
          name: schema.user.name.required()
        }
      }
    },
    handler: function(request, reply) {
      User.query()
        .where('email', request.payload.email)
        .first()
        .then(function(user) {
          if (user) {
            reply(Boom.badRequest('Email already exists'));
          }
          else {
            User.query()
              .insert({
                name: request.payload.name,
                email: request.payload.email,
                password: bcrypt.hashSync(request.payload.password, bcrypt.genSaltSync(10), null)
              })
              .then(function(newUser) {
                if (newUser) {
                  loginHandler(request, reply);
                }
                else {
                  reply(Boom.wrap(new Error('Could not create user'), 500))
                }
              })
          }
        })
    }
  });

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
