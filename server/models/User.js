'use strict';

var Model = require('objection').Model;
var moment = require('moment');

/**
 * @extends Model
 * @constructor
 */
function User() {
  Model.apply(this, arguments);
}

Model.extend(User);
module.exports = User;

User.tableName = 'User';
User.idColumn = 'userId';

User.validate = function (decoded, request, callback) {
  if (moment().unix() > decoded.iat) {
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


User.relationMappings = {
  /*
  connections: {
    relation: Model.OneToManyRelation,
    modelClass: __dirname + '/Connection',
    join: {
      from: 'User.UserId',
      to: 'Connection.fromUserId'
    }
  }
  */
};
