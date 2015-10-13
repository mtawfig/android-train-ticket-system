'use strict';

var Model = require('objection').Model;
var Graph = require('node-dijkstra');
var _ = require('lodash');
var Station = require('./Station.js');

/**
 * @extends Model
 * @constructor
 */
function Connection() {
  Model.apply(this, arguments);
}

Model.extend(Connection);
module.exports = Connection;

Connection.tableName = 'Connection';

Connection.getPath = function(fromStationId, toStationId) {
  var route = new Graph();
  return Connection.query()
    .then(function (connections) {
      var groupedByStationAsDistanceNodes = _.chain(connections)
        .groupBy(function(connection) {
          return connection.fromStationId;
        })
        .mapValues(function(group) {
          return group.reduce(function(acc, station) {
            acc[station.toStationId] = 1;
            return acc;
          }, {})
        })
        .value();

      _.each(groupedByStationAsDistanceNodes, function(connections, stationId) {
        route.addNode(stationId, connections)
      });

      var path = route.path(fromStationId, toStationId);

      return Station.query()
        .eager('connections')
        .whereIn('stationId', path)
        .then(function(stations) {
          return path.map(function(stationId, index) {
            var station = _.find(stations, function(station) {
              return station.stationId == stationId
            });

            if (index + 1 < path.length) {
              var nextStationId = parseInt(path[index + 1], 10);
              var connection = _.find(station.connections, function(connection) {
                return connection.toStationId === nextStationId;
              });
            }

            if (connection) {
              station.connections = [connection];
            } else {
              station.connections = [];
            }

            return station;
          })
        })
    });
};

Connection.relationMappings = {
  fromStation: {
    relation: Model.OneToOneRelation,
    modelClass: __dirname + '/Station',
    join: {
      from: 'Connection.fromStationId',
      to: 'Station.stationId'
    }
  },
  toStation: {
    relation: Model.OneToOneRelation,
    modelClass: __dirname + '/Station',
    join: {
      from: 'Connection.toStationId',
      to: 'Station.stationId'
    }
  }
};
