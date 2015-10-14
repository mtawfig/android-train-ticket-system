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

var graphPromise;
var getGraph = function() {
  if (graphPromise) {
    return graphPromise;
  } else {
    graphPromise = Connection.query()
      .then(function (connections) {
        var graph = new Graph();

        var groupedByStationAsDistanceNodes = _.chain(connections)
          .groupBy(function (connection) {
            return connection.fromStationId;
          })
          .mapValues(function (group) {
            return group.reduce(function (acc, station) {
              acc[station.toStationId] = 1;
              return acc;
            }, {})
          })
          .value();

        _.each(groupedByStationAsDistanceNodes, function (connections, stationId) {
          graph.addNode(stationId, connections)
        });

        return graph;
      });

    return graphPromise;
  }
};

Connection.getPath = function(fromStationId, toStationId) {
  return getGraph().then(function(graph) {
    var path = graph.path(fromStationId.toString(), toStationId.toString());

    return Station.query()
      .whereIn('stationId', path)
      .then(function(stations) {
        return path.map(function(stationId) {
          return _.find(stations, function(station) {
            return station.stationId == stationId
          });
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
