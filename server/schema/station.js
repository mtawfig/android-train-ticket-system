'use strict';

var Joi = require('joi');

module.exports = {
  id: Joi.number().integer().min(1),
  name: Joi.string().min(5).max(20),
  date: Joi.date().iso()
};
