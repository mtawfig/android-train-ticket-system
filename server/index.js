'use strict';

var Hapi = require('hapi');

var server = new Hapi.Server();
server.connection({
  port: process.argv[2] || 8000
});

// DB setup
var Knex = require('knex');
var knexConfig = require('./knexfile');
var Model = require('objection').Model;
var knex = Knex(knexConfig.development);
Model.knex(knex);

require('./routes/')(server);

// Test route
server.route({
  method: 'GET',
  path: '/',
  handler: function (request, reply) {
    reply('Hello! Yes, this is working.');
  }
});

// Start the server
server.start(function() {
  console.log('Server running at:', server.info.uri);
});
