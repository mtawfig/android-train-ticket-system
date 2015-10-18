'use strict';

var Joi = require('joi');

module.exports = {
  id: Joi.number().integer().min(1),
  email: Joi.string().email(),
  name: Joi.string().min(1).max(24),
  password: Joi.string().min(3).max(256)
};
