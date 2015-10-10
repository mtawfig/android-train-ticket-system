'use strict';

var Model = require('objection').Model;

/**
 * @extends Model
 * @constructor
 */
function Station() {
  Model.apply(this, arguments);
}

Model.extend(Station);
module.exports = Station;

Station.tableName = 'Station';
Station.idColumn = 'stationId';

Station.relationMappings = {
  connections: {
    relation: Model.OneToManyRelation,
    modelClass: __dirname + '/Connection',
    join: {
      from: 'Station.stationId',
      to: 'Connection.fromStationId'
    }
  }
};
