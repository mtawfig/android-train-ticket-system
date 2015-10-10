// the order of the require clauses should be maintained, there are dependencies

module.exports = function (server) {
  require('./station')(server);
};
