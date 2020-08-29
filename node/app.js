const express = require('express');
const app = express();
const bodyParser = require('body-parser');

app.use(bodyParser.urlencoded({extended: false}));
app.use(bodyParser.json());

const userPost = require('./router_post');
const userGet = require('./router_get');

app.use('/post', userPost);
app.use('/get', userGet);

module.exports = app;