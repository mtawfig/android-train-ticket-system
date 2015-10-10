var Knex = require('knex');
var knexConfig = require('../knexfile');
var knex = Knex(knexConfig.development);

var LABEL_LEFT = 90;
var LABEL_DOWN = 180;
var LABEL_UP = 0;

var station = function(id, name, latitude, longitude, degrees) {
  return {
    stationId: id,
    name: name,
    latitude: latitude,
    longitude: longitude,
    labelRotationDegrees: degrees
  };
};

var connection = function(stationA, stationB, direction, line) {
  return {
    fromStationId: stationA.stationId,
    toStationId: stationB.stationId,
    direction: direction,
    line: line
  };
};

var campanha = station( 1, 'Campanhã', 41.1505929, -8.5859497, LABEL_LEFT );
var espinho = station( 2, 'Espinho', 41.0043836, -8.6456402, LABEL_LEFT );
var francelos = station( 3, 'Francelos', 41.0812921, -8.6475706, LABEL_LEFT );
var saoromao = station( 4, 'S. Romão', 41.277871, -8.5536718,LABEL_LEFT );
var ermesinde = station( 5, 'Ermesinde', 41.2169514, -8.5540581, LABEL_LEFT );
var parada = station( 6, 'Parada', 41.1602043, -8.3726025, LABEL_DOWN );
var saomartinho = station( 7, 'S. Martinho', 41.1603533, -8.4695933, LABEL_UP );

var defaultStations = [
  campanha, espinho, francelos, saoromao, ermesinde, parada, saomartinho
];

var defaultConnections = [
  connection(campanha, francelos, 'S', 'A'),
  connection(campanha, ermesinde, 'N', 'A'),
  connection(campanha, saomartinho, 'E', 'A'),

  connection(francelos, campanha, 'N', 'A'),
  connection(francelos, espinho, 'S', 'A'),

  connection(espinho, francelos, 'N', 'A'),

  connection(ermesinde, saoromao, 'N', 'A'),
  connection(ermesinde, campanha, 'S', 'A'),

  connection(saoromao, ermesinde, 'N', 'A'),

  connection(saomartinho, campanha, 'W', 'B'),
  connection(saomartinho, parada, 'E', 'B'),

  connection(parada, saomartinho, 'W', 'B')
];

module.exports = {
  seed: function() {
    return knex('Station')
      .insert(defaultStations)
      .then(function () {
        return knex('Connection')
          .insert(defaultConnections)
      })
      .then(function() {
        console.log('Initial setup complete!');
      });
  }
};
