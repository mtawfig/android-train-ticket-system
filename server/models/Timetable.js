'use strict';

var Model = require('objection').Model;
var Promise = require('bluebird');
var _ = require('lodash');

var Knex = require('knex');
var knexConfig = require('../knexfile');
var knex = Knex(knexConfig.development);

/**
 * @extends Model
 * @constructor
 */
function Timetable() {
  Model.apply(this, arguments);
}

Model.extend(Timetable);
module.exports = Timetable;

Timetable.tableName = 'Timetable';
Timetable.idColumn = 'timetableId';

Timetable.getTimetablesForPath = function (path, startTime) {
  var hours = startTime.getHours();
  var minutes = startTime.getMinutes();

  var bestTimetable = [];

  var getBest = function(path, hours, minutes) {
    if (path.length === 1) {
      return bestTimetable;
    }

    var fromStationId = path[0].stationId;
    var toStationId = path[1].stationId;

    return knex('Timetable')
      .where('Timetable.fromStationId', '=', fromStationId)
      .andWhere('Timetable.toStationId', '=', toStationId)
      .andWhere(function () {
        this
          .where(function () {
            this.where('hoursStart', '=', hours)
              .andWhere('minutesStart', '>=', minutes)
          })
          .orWhere('hoursStart', '>', hours)
      })
      .orderByRaw('hoursStart ASC, minutesStart ASC')
      .first()
      .innerJoin('Connection', function() {
        this.on('Timetable.fromStationId', 'Connection.fromStationId')
          .andOn('Timetable.toStationId', 'Connection.toStationId')
      })
      .then(function (best) {
        if (!best) {
          // skips to next day
          return getBest(path, 0, 0);
        }
        bestTimetable.push(best);
        return getBest(_.rest(path), best.hoursEnd, best.minutesEnd)
      })
  };

  return getBest(path, hours, minutes);
};

Timetable.relationMappings = {
  fromStation: {
    relation: Model.OneToOneRelation,
    modelClass: __dirname + '/Station',
    join: {
      from: 'Timetable.fromStationId',
      to: 'Station.stationId'
    }
  },
  toStation: {
    relation: Model.OneToOneRelation,
    modelClass: __dirname + '/Station',
    join: {
      from: 'Timetable.toStationId',
      to: 'Station.stationId'
    }
  }
};
