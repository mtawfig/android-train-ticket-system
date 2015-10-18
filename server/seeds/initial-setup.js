var Knex = require('knex');
var knexConfig = require('../knexfile');
var knex = Knex(knexConfig.development);

var bCrypt = require('bcrypt');

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

var timetable = function(stationA, stationB, hoursStart, minutesStart, hoursEnd, minutesEnd, tripNumber, tripStepNumber) {
  return {
    fromStationId: stationA.stationId,
    toStationId: stationB.stationId,
    hoursStart: hoursStart,
    minutesStart: minutesStart,
    hoursEnd: hoursEnd,
    minutesEnd: minutesEnd,
    tripNumber: tripNumber,
    tripStepNumber: tripStepNumber
  }
};

var user = function(name, email, password, role) {
  return {
    name: name,
    email: email,
    password: bCrypt.hashSync(password, bCrypt.genSaltSync(10), null),
    role: role
  }
};

var c = station( 1, 'Campanhã', 41.1505929, -8.5859497, LABEL_LEFT );
var b2 = station( 2, 'Espinho', 41.0043836, -8.6456402, LABEL_LEFT );
var b1 = station( 3, 'Francelos', 41.0812921, -8.6475706, LABEL_LEFT );
var a2 = station( 4, 'S. Romão', 41.277871, -8.5536718,LABEL_LEFT );
var a1 = station( 5, 'Ermesinde', 41.2169514, -8.5540581, LABEL_LEFT );
var c2 = station( 6, 'Parada', 41.1602043, -8.3726025, LABEL_DOWN );
var c1 = station( 7, 'S. Martinho', 41.1603533, -8.4695933, LABEL_UP );

var defaultStations = [
  c, b2, b1, a2, a1, c2, c1
];

var defaultConnections = [
  connection(c, b1, 'S', 'A'),
  connection(c, a1, 'N', 'A'),
  connection(c, c1, 'E', 'B'),

  connection(b1, c, 'N', 'A'),
  connection(b1, b2, 'S', 'A'),

  connection(b2, b1, 'N', 'A'),

  connection(a1, a2, 'N', 'A'),
  connection(a1, c, 'S', 'A'),

  connection(a2, a1, 'N', 'A'),

  connection(c1, c, 'W', 'B'),
  connection(c1, c2, 'E', 'B'),

  connection(c2, c1, 'W', 'B')
];

var defaultTimetables = [

  // ---------- A2, A1, C, B1, B2 ------------
  timetable(a2, a1,   9,  0,   9, 53,   1, 1),
  timetable(a1,  c,   9, 53,  10, 45,   1, 2),
  timetable( c, b1,  10, 45,  11, 38,   1, 3),
  timetable(b1, b2,  11, 38,  12, 30,   1, 4),

  timetable(a2, a1,  14,  0,  14, 53,   2, 1),
  timetable(a1,  c,  14, 53,  15, 45,   2, 2),
  timetable( c, b1,  15, 45,  16, 38,   2, 3),
  timetable(b1, b2,  16, 38,  17, 30,   2, 4),

  timetable(a2, a1,  18,  0,  18, 53,   3, 1),
  timetable(a1,  c,  18, 53,  19, 45,   3, 2),
  timetable( c, b1,  19, 45,  20, 38,   3, 3),
  timetable(b1, b2,  20, 38,  21, 30,   3, 4),

  // ---------- B2, B1, C, A1, A2 ------------
  timetable(b2, b1,   9,  0,   9, 53,   4, 1),
  timetable(b1,  c,   9, 53,  10, 45,   4, 2),
  timetable( c, a1,  10, 45,  11, 38,   4, 3),
  timetable(a1, a2,  11, 38,  12, 30,   4, 4),

  timetable(b2, b1,  14,  0,  14, 53,   5, 1),
  timetable(b1,  c,  14, 53,  15, 45,   5, 2),
  timetable( c, a1,  15, 45,  16, 38,   5, 3),
  timetable(a1, a2,  16, 38,  17, 30,   5, 4),

  timetable(b2, b1,  18,  0,  18, 53,   6, 1),
  timetable(b1,  c,  18, 53,  19, 45,   6, 2),
  timetable( c, a1,  19, 45,  20, 38,   6, 3),
  timetable(a1, a2,  20, 38,  21, 30,   6, 4),

  // ---------------- C2, C1, C --------------
  timetable(c2, c1,   9, 30,  10,  0,   7, 1),
  timetable(c1,  c,  10,  0,  10, 30,   7, 2),

  timetable(c2, c1,  14, 30,  15,  0,   8, 1),
  timetable(c1,  c,  15,  0,  15, 30,   8, 2),

  timetable(c2, c1,  18, 30,  19,  0,   9, 1),
  timetable(c1,  c,  19, 30,  19, 30,   9, 2),

  // ---------------- C, C1, C2 --------------
  timetable( c, c1,  11,  0,  11, 30,   10, 1),
  timetable(c1, c2,  11, 30,  12,  0,   10, 2),

  timetable( c, c1,  16,  0,  16, 30,   11, 1),
  timetable(c1, c2,  16, 30,  17,  0,   11, 2),

  timetable( c, c1,  20,  0,  20, 30,   12, 1),
  timetable(c1, c2,  20, 30,  21,  0,   12, 2)

];

var defaultUsers = [
  user('admin', 'admin@tt.com', '123', 'inspector'),
  user('user', 'user@tt.com', '123', 'user')
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
        return knex('Timetable')
          .insert(defaultTimetables)
      })
      .then(function() {
        return knex('Timetable')
          .insert(defaultTimetables)
      })
      .then(function() {
        return knex('User')
          .insert(defaultUsers)
      })
      .then(function() {
        console.log('Initial setup complete!');
      });
  }
};
