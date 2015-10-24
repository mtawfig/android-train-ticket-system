'use strict';

var Model = require('objection').Model;
var Promise = require('bluebird');
var uuid = require('node-uuid');
var _ = require('lodash');
var crypto = require('crypto');
var fs = require('fs');

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
  }
};

var getNumberOfSeatsTaken = function(step, date) {
  var numberSeatsTaken;
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

Ticket.createTickets = function(user, itinerary, arraySeatNumber, date) {
  return Promise.map(itinerary.steps, function createTicket(step, stepIndex) {
    var numberSeatsTaken;
    var train;
    return getNumberOfSeatsTaken(step, date)
      .then(function(count) {
        numberSeatsTaken = count;
        return getTrain(step);
      })
      .then(function(trainObject) {
        train = trainObject;
        if (train.capacity <= numberSeatsTaken) {
          throw new Error('Max capacity for this train has been reached')
        }

        return getSeatsOfTrip(step, date);
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
          date: date,
          purchaseTime: new Date(),
          fromStationId: step.startStation.stationId,
          toStationId: step.endStation.stationId,
          uuid: uuid.v1(),
          tripId: step.tripId,
          fromTripStepNumber: step.fromTripStepNumber,
          toTripStepNumber: step.toTripStepNumber,
          seatNumber: selectedSeatNumber,
          used: false
        };

        var buffer = JSON.stringify(ticket);
        var sign = crypto.createSign('RSA-SHA1');
        sign.update(buffer);
        ticket.signature = sign.sign(privateKey, 'hex');

        /*
        // VERIFICATION EXAMPLE SERVER SIDE
        var verifier = crypto.createVerify('RSA-SHA1');
        verifier.update(buffer);
        var success = verifier.verify(publicKey, ticket.signature, 'hex');
        console.log(success);
        */

        return ticket;
      })
  });
};
