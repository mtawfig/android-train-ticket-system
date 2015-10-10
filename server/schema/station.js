'use strict';

var Joi = require('joi');

module.exports = {
    name: Joi.string().min(1).max(1024)
};
