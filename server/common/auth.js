
var User = require('../models/User.js');
var moment = require('moment');

var validate = function (decoded, request, callback) {

  if (moment().diff(moment(decoded.iat), 'seconds') > 3600) {
    return callback(null, false);
  }

  User.query()
    .where('userId', decoded.userId)
    .then(function(user) {
      var isValid = !!user;

      if (isValid) {
        callback(null, true);
      } else {
        callback(null, false);
      }
    });
};


module.exports = {
  validate: validate
};
