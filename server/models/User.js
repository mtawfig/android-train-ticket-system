'use strict';

var Model = require('objection').Model;

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

User.validate = function(user) {
  User.query()
    .where('userId', user.userId)
    .then(function(user) {
      return !!user;
    })
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
