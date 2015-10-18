
exports.up = function (knex) {
  return knex.schema
    .createTable('Station', function (table) {
      table.bigincrements('stationId').primary();
      table.string('name');
      table.integer('labelRotationDegrees');
      table.float('latitude');
      table.float('longitude');
    })
    .createTable('Connection', function (table) {
      table.string('direction');
      table.string('line');
      table.biginteger('fromStationId').unsigned().references('stationId').inTable('Station').onDelete('CASCADE');
      table.biginteger('toStationId').unsigned().references('stationId').inTable('Station').onDelete('CASCADE');
      table.primary(['fromStationId', 'toStationId']);
    })
    .createTable('Timetable', function(table) {
      table.biginteger('fromStationId').unsigned().references('stationId').inTable('Station').onDelete('CASCADE');
      table.biginteger('toStationId').unsigned().references('stationId').inTable('Station').onDelete('CASCADE');
      table.integer('hoursStart');
      table.integer('minutesStart');
      table.integer('hoursEnd');
      table.integer('minutesEnd');
      table.integer('tripNumber');
      table.integer('tripStepNumber');
    })
    .createTable('User', function(table) {
      table.bigincrements('userId').primary();
      table.string('email').unique();
      table.string('name');
      table.string('password');
      table.enu('role', ['user', 'inspector'])
    })
};

exports.down = function (knex) {
  return knex.schema
    .dropTableIfExists('Timetable')
    .dropTableIfExists('Connection')
    .dropTableIfExists('Station');

};
