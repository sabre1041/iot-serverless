var path = require("path");
const format= require('util').format;
require('dotenv').config({path: path.join(__dirname, '.env')});

var MongoClient = require('mongodb').MongoClient;

function enrich(params) {

    // Validate Parameters
    var mongoDbUser = process.env.MONGODB_USER;
    var mongoDbPassword = process.env.MONGODB_PASSWORD;
    var mongoDbHost = process.env.MONGODB_HOST;
    var mongoDbDatabase = process.env.MONGODB_DATABASE;

    if(!mongoDbUser || !mongoDbPassword || !mongoDbHost || !mongoDbDatabase) {
      return {error: "Database Values Have Not Been Provided!"}
    }

    var url = format('mongodb://%s:%s@%s:27017/%s', mongoDbUser, mongoDbPassword, mongoDbHost, mongoDbDatabase);


    var topic = params.topic;

    if(topic) {
      return new Promise(function(resolve, reject) {

        MongoClient.connect(url, function(err, client){
          
          if(err) {
            console.error(err);
            reject({"error":err.message});
            return;
          }

          var db = client.db(mongoDbDatabase);

          db.collection('assets').findOne({"topic": topic}, function (err, doc) {
            if(err) {
              console.error(err);
              reject({"error":err.message});
              return;
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

      });
    }
    else {
      console.log("Topic Has Not Been Provided")
      return params;
    }
    
  };

exports.main = enrich;