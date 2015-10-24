'use strict';

var User = require('../models/User');
var Boom = require('boom');
var Timetable = require('../models/Timetable.js');
var Connection = require('../models/Connection.js');
var Ticket = require('../models/Ticket.js');
var schema = require('../schema/index.js');
var moment = require('moment');
var Promise = require('bluebird');
var Queue = require('promise-queue');

var creditCardService = {
  charge: function(cardNumber, month, year, securityCode, amount) {
    return new Promise(function (resolve, reject) {
      if (cardNumber % 2 === 0) {
        resolve();
      } else {
        reject(new Error('Invalid credit card'));
      }
    })
  }
};

Queue.configure(Promise);
var maxConcurrent = 1;
var maxQueue = Infinity;
var queue = new Queue(maxConcurrent, maxQueue);

module.exports = function(server) {

  server.route({
    method: 'POST',
    path: '/tickets/{fromStationId}/to/{toStationId}',
    config: {
      auth: 'jwt',
      validate: {
        params: {
          fromStationId: schema.station.id,
          toStationId: schema.station.id
        },
        payload: {
          arraySeatNumber: schema.ticket.arraySeatNumber.optional(),
          date: schema.ticket.date.optional(),
          cardNumber: schema.ticket.cardNumber.required(),
          monthExpiration: schema.ticket.monthExpiration.required(),
          yearExpiration: schema.ticket.yearExpiration.required(),
          cardSecurityCode: schema.ticket.cardSecurityCode.required()
        }
      }
    },
    handler: function(request, reply) {
      var user = request.auth.credentials.user;
      var date = request.payload.date || moment().startOf('day').toDate();
      var arraySeatNumber = request.payload.arraySeatNumber || [];

      var tickets;
      var itinerary;

      queue.add(function() {
          return Timetable.getItinerary(request.params.fromStationId, request.params.toStationId, date)
            .then(function(createdItinerary) {
              itinerary = createdItinerary;
              return Ticket.createTickets(user, itinerary, arraySeatNumber, date);
            })
            .then(function(createdTickets) {
              tickets = createdTickets;
              return creditCardService.charge(
                request.payload.cardNumber,
                request.payload.monthExpiration,
                request.payload.yearExpiration,
                request.payload.cardSecurityCode,
                itinerary.cost
              )
            })
            .then(function() {
              return Promise.map(tickets, function(ticket) {
                return Ticket.query().insert(ticket)
                  .then(function(insertedTicket) {
                    return insertedTicket;
                  });
              });
            });
        })
        .then(function(insertedTickets) {
          reply(insertedTickets);
        })
        .catch(function(error) {
          reply(Boom.badRequest(error));
        });
    }
  });
};
