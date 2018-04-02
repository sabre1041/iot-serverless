var path = require("path");
const format= require('util').format;
require('dotenv').config({path: path.join(__dirname, '.env')});

var MongoClient = require('mongodb').MongoClient;
var url = format('mongodb://%s:%s@%s:27017/%s', process.env.MONGODB_USER, process.env.MONGODB_PASSWORD, process.env.MONGODB_HOST, process.env.MONGODB_DATABASE);

function enrich(params) {

    var topic = params.topic;

    if(topic) {
      return new Promise(function(resolve, reject) {

        MongoClient.connect(url, function(err, client){
          
          if(err) {
            console.log(err);
            reject({"error":err})
          }

          var db = client.db(process.env.MONGODB_DATABASE);

          db.collection('assets').findOne({"topic": topic}, function (err, doc) {
            if(err) {
              console.log(err);
              reject(err)
            }
            
            if(doc) {
                for(index in doc) {
                  if(index != "_id") {
                    params[index] = doc[index];
                  }
                }
            }
            else {
                console.log("No Asset Found with topic '%s'", topic);
            }
            client.close();
            resolve(params);
        });


        });

      }) 
    }
    else {
      console.log("Topic Has Not Been Provided")
      return params;
    }
    
  };

exports.main = enrich;