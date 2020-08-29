
const express = require('express');
const router = express.Router();
const XMLHttpRequest = require('xmlhttprequest').XMLHttpRequest;

var xmlRequest = new XMLHttpRequest();

router.get('/', (req, res, next) => {

    xmlRequest.open("GET", "http://127.0.0.1:8888/matcha/SQL/Users.json")

    xmlRequest.onload = function(){

        if (this.status == 200){
        const object = JSON.parse(this.responseText);
        res.status(200).json(object);
        }

        return;

    }

    xmlRequest.send();
});

module.exports = router;