'use strict';

var Model = require('objection').Model;
var Promise = require('bluebird');
var uuid = require('node-uuid');
var _ = require('lodash');
var crypto = require('crypto');
var fs = require('fs');
var stringify = require('json-stable-stringify')

var privateKey = fs.readFileSync(process.cwd() + '/certificates/private.key').toString('utf8');
var publicKey = fs.readFileSync(process.cwd() + '/certificates/public.key').toString('utf8');

var Knex = require('knex');
var knexConfig = require('../knexfile');
var knex = Knex(knexConfig.development);

/**
 * @extends Model
 * @constructor
 */
function Ticket() {
  Model.apply(this, arguments);
}

Model.extend(Ticket);
module.exports = Ticket;

Ticket.tableName = 'Ticket';
Ticket.idColumn = 'ticketId';

Ticket.relationMappings = {
  fromStation: {
    relation: Model.OneToOneRelation,
    modelClass: __dirname + '/Station',
    join: {
      from: 'Ticket.fromStationId',
      to: 'Station.stationId'
    }
  },
  toStation: {
    relation: Model.OneToOneRelation,
    modelClass: __dirname + '/Station',
    join: {
      from: 'Ticket.toStationId',
      to: 'Station.stationId'
    }
  },
  user: {
    relation: Model.OneToOneRelation,
    modelClass: __dirname + '/User',
    join: {
      from: 'Ticket.userId',
      to: 'User.userId'
    }
  }
};

var getNumberOfSeatsTaken = function(step, date) {
  return knex('Ticket')
    .count('* as numberSeatsTaken')
    .where('tripId', step.tripId)
    .andWhere('fromTripStepNumber', '>=', step.fromTripStepNumber)
    .andWhere('toTripStepNumber', '<=', step.toTripStepNumber)
    .andWhere('date', date)
    .then(function(count) {
      return count[0].numberSeatsTaken;
    })
};

var getTrain = function(step) {
  return knex('Trip')
    .first()
    .where('tripId', step.tripId)
    .innerJoin('Train', 'trip.trainId', 'train.trainId')
};

Ticket.getFreeSeats = function(step, date) {
  var arraySeatsTaken;
  return getSeatsOfTrip(step, date)
    .then(function(result) {
      arraySeatsTaken = result;
      return getTrain(step);
    })
    .then(function(train) {
      var freeSeats = [];

      for(var seatNumber = 0; seatNumber < train.capacity; seatNumber++) {
        if (!_.contains(arraySeatsTaken, seatNumber)) {
          freeSeats.push(seatNumber);
        }
      }

      return freeSeats;
    });
};

var getSeatsOfTrip = function(step, date) {
  return knex.select('seatNumber').from('Ticket')
    .where('tripId', step.tripId)
    .andWhere('fromTripStepNumber', '>=', step.fromTripStepNumber)
    .andWhere('toTripStepNumber', '<=', step.toTripStepNumber)
    .andWhere('date', date)
    .orderBy('seatNumber ASC')
    .then(function(seats) {
      return seats.map(function(seat) { return seat.seatNumber });
    })
};

var pickRandomSeat = function(filledSeats, train) {
  for(var seatNumber = 0; seatNumber < train.capacity; seatNumber++) {
    if (!_.contains(filledSeats, seatNumber)) {
      return seatNumber;
    }
  }
};

Ticket.createTickets = function(user, itinerary, arraySeatNumber) {
  return Promise.map(itinerary.steps, function createTicket(step, stepIndex) {
    var numberSeatsTaken;
    var train;
    return getNumberOfSeatsTaken(step, itinerary.date)
      .then(function(count) {
        numberSeatsTaken = count;
        return getTrain(step);
      })
      .then(function(trainObject) {
        train = trainObject;
        if (train.capacity <= numberSeatsTaken) {
          throw new Error('Max capacity for this train has been reached')
        }

        return getSeatsOfTrip(step, itinerary.date);
      })
      .then(function(filledSeats) {

        var selectedSeatNumber = _.isNumber(arraySeatNumber[stepIndex]) ? arraySeatNumber[stepIndex] : pickRandomSeat(filledSeats, train);

        var isValidSeatNumber = selectedSeatNumber >= 0 && selectedSeatNumber < train.capacity;
        var isSeatTaken = _.contains(filledSeats, selectedSeatNumber);

        if (!isValidSeatNumber) {
          throw new Error('The seat is invalid! Train capacity is only ' + train.capacity);
        }

        if (isSeatTaken) {
          throw new Error('The seat is already taken for a step in the trip!')
        }

        var ticket = {
          userId: user.userId,
          date: itinerary.date.getTime(),
          purchaseTime: new Date().getTime(),
          fromStationId: step.startStation.stationId,
          toStationId: step.endStation.stationId,
          uuid: uuid.v1(),
          tripId: step.tripId,
          fromTripStepNumber: step.fromTripStepNumber,
          toTripStepNumber: step.toTripStepNumber,
          seatNumber: selectedSeatNumber,
          hoursStart: step.hoursStart,
          minutesStart: step.minutesStart,
          hoursEnd: step.hoursEnd,
          minutesEnd: step.minutesEnd
        };

        var buffer = new Buffer(stringify(ticket));
        var encodedString = buffer.toString('utf8');
        var sign = crypto.createSign('RSA-SHA1');
        sign.update(encodedString);
        ticket.signature = sign.sign(privateKey, 'hex');
        ticket.used = false;

        /*
        // VERIFICATION EXAMPLE SERVER SIDE
        var verifier = crypto.createVerify('RSA-SHA1');
        verifier.update(encodedString);
        var success = verifier.verify(publicKey, ticket.signature, 'hex');
        console.log(success);
        */

        return ticket;
      })
  });
};

Ticket.getTickets = function(user) {
  return Ticket.query()
    .eager('[fromStation, toStation]')
    .where('userId', user.userId)
    .orderByRaw('date DESC, hoursStart DESC, minutesStart DESC')
    .then(function(tickets) {
      tickets.forEach(function(ticket) {
        ticket.used = !!ticket.used;
      });
      return tickets;
    });
};

Ticket.getAllTickets = function() {
  return Ticket.query()
    .eager('[fromStation, toStation, user]')
    .then(function(tickets) {
      tickets.forEach(function(ticket) {
        ticket.used = !!ticket.used;
        delete ticket.user.password;
      });
      return tickets;
    });
};
