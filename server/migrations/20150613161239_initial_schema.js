
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
    });
};

exports.down = function (knex) {
  return knex.schema
    .dropTableIfExists('Connection')
    .dropTableIfExists('Station');

};
