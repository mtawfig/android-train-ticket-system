'use strict';

var Model = require('objection').Model;

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
