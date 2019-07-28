'use strict'
const merge = require('webpack-merge')
const prodEnv = require('./prod.env')

module.exports = merge(prodEnv, {
  NODE_ENV: '"development"',
  //VUE_APP_WEBSOCKET_URL : 'localhost:9001/websocket/TCP_SERVER'
  BASE_API: '"http://127.0.0.1:9001"',
});
