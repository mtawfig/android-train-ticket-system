'use strict';

var database = {
  client: 'sqlite3',
  connection: {
    filename: './database.db'
  },
  seeds: {
    directory: './seeds'
  }
};

module.exports = {
  development: database,
  production: database
};
