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

var buildSubObjectsAndOmitKeys = function(omitList, result) {
  return _.reduce(result, function(newResult, n, key) {
    var split = key.split(':');
    var subKey;
    if (split.length === 2) {
      key = split[0];
      subKey = split[1];
      if (!newResult[key]) {
        newResult[key] = {};
      }
      newResult[key][subKey] = n;
    } else if (omitList.indexOf(key) === -1) {
      newResult[key] = n;
    }
    return newResult;
  }, {});
};

Timetable.getTimetable = function (departingStationId) {
  return knex('Timetable')
    .distinct('tripNumber', 'tripStepNumber')
    .where('Timetable.fromStationId', departingStationId)
    .then(function(distinctDepartures) {
      var query = knex.select([
          "Timetable.fromStationId",
          "Timetable.toStationId",
          "hoursStart",
          "minutesStart",
          "hoursEnd",
          "minutesEnd",
          "tripNumber",
          "tripStepNumber",
          "direction",
          "line",
          "FromStation.stationId as fromStation:stationId",
          "FromStation.name as fromStation:name",
          "FromStation.labelRotationDegrees as fromStation:labelRotationDegrees",
          "FromStation.latitude as fromStation:latitude",
          "FromStation.longitude as fromStation:longitude",
          "ToStation.stationId as toStation:stationId",
          "ToStation.name as toStation:name",
          "ToStation.labelRotationDegrees as toStation:labelRotationDegrees",
          "ToStation.latitude as toStation:latitude",
          "ToStation.longitude as toStation:longitude"
        ])
        .from('Timetable')
        .innerJoin('Connection', function() {
          this.on('Timetable.fromStationId', 'Connection.fromStationId')
            .andOn('Timetable.toStationId', 'Connection.toStationId')
        })
        .innerJoin('Station AS FromStation', 'Timetable.fromStationId', 'FromStation.stationId')
        .innerJoin('Station AS ToStation', 'Timetable.toStationId', 'ToStation.stationId')
        .orderBy('tripNumber ASC, tripStepNumber ASC');

      return _.reduce(distinctDepartures, function(query, departure) {
        return query.orWhere(function() {
          this.where('tripNumber', '=', departure.tripNumber)
            .andWhere('tripStepNumber', '>=', departure.tripStepNumber)
        });
      }, query);
    })
    .then(function(results) {
      return _.chain(results)
        .map(buildSubObjectsAndOmitKeys.bind(null, ['fromStationId', 'toStationId']))
        .groupBy(function(result) {
          return result.line + ':' + result.direction;
        })
        .map(function(trips) {
          var firstOfTrip = trips[Object.keys(trips)[0]];
          return {
            line: firstOfTrip.line,
            direction: firstOfTrip.direction,
            trips: _.chain(trips)
              .groupBy(function(lineResult) {
                return lineResult.tripNumber;
              })
              .value()
          }
        })
        .value();
    });
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
