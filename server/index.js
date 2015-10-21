'use strict';

var Hapi = require('hapi');
var User = require('./models/User.js');
var AuthJWT = require('hapi-auth-jwt2');

var server = new Hapi.Server();
server.connection({
  port: process.argv[3] || 8000
});

var privateServerKey = process.argv[2] || 'Drop the bass!';

// DB setup
var Knex = require('knex');
var knexConfig = require('./knexfile');
var Model = require('objection').Model;
var knex = Knex(knexConfig.development);
Model.knex(knex);

server.register(AuthJWT, function (err) {

  if(err){
    console.log(err);
  }

  server.auth.strategy('jwt', 'jwt', {
    key: privateServerKey,
    validateFunc: User.validate,
    verifyOptions: { algorithms: [ 'HS256' ] }
  });

  server.auth.default('jwt');

  require('./routes/')(server);
});

server.start(function() {
  console.log('Server running at:', server.info.uri);
});
