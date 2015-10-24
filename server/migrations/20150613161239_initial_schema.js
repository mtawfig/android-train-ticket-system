
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
    .createTable('Trip', function(table) {
      table.integer('tripId');
      table.biginteger('trainId').unsigned().references('trainId').inTable('Train').onDelete('CASCADE');
    })
    .createTable('Timetable', function(table) {
      table.bigincrements('timetableId').primary();
      table.biginteger('fromStationId').unsigned().references('stationId').inTable('Station').onDelete('CASCADE');
      table.biginteger('toStationId').unsigned().references('stationId').inTable('Station').onDelete('CASCADE');
      table.integer('hoursStart');
      table.integer('minutesStart');
      table.integer('hoursEnd');
      table.integer('minutesEnd');
      table.integer('tripId').references('tripId').inTable('Trip').onDelete('CASCADE');
      table.integer('tripStepNumber');
    })
    .createTable('User', function(table) {
      table.bigincrements('userId').primary();
      table.string('email').unique();
      table.string('name');
      table.string('password');
      table.enu('role', ['user', 'inspector']).defaultTo('user')
    })
    .createTable('Train', function(table) {
      table.bigincrements('trainId').primary();
      table.integer('capacity').notNullable();
    })
    .createTable('Ticket', function(table) {
      table.bigincrements('ticketId').primary();
      table.biginteger('userId').unsigned().references('userId').inTable('User').onDelete('CASCADE');
      table.date('date').notNullable();
      table.time('purchaseTime');
      table.biginteger('fromStationId').unsigned().references('stationId').inTable('Station').onDelete('CASCADE');
      table.biginteger('toStationId').unsigned().references('stationId').inTable('Station').onDelete('CASCADE');
      table.string('uuid', 36).unique();
      table.string('signature');
      table.integer('tripId').references('tripId').inTable('Trip').onDelete('CASCADE');
      table.integer('fromTripStepNumber');
      table.integer('toTripStepNumber');
      table.integer('seatNumber');
      table.boolean('used').defaultTo('false');
    })
};

exports.down = function (knex) {
  return knex.schema
    .dropTableIfExists('Train')
    .dropTableIfExists('Trip')
    .dropTableIfExists('Ticket')
    .dropTableIfExists('User')
    .dropTableIfExists('Timetable')
    .dropTableIfExists('Connection')
    .dropTableIfExists('Station');
};
