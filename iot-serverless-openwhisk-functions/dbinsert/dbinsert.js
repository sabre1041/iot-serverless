var path = require("path");
const format= require('util').format;
require('dotenv').config({path: path.join(__dirname, '.env')});

var MongoClient = require('mongodb').MongoClient;

function dbinsert(params) {

    // Validate Parameters
    var mongoDbUser = process.env.MONGODB_USER;
    var mongoDbPassword = process.env.MONGODB_PASSWORD;
    var mongoDbHost = process.env.MONGODB_HOST;
    var mongoDbDatabase = process.env.MONGODB_DATABASE;

    if(!mongoDbUser || !mongoDbPassword || !mongoDbHost || !mongoDbDatabase) {
      return {error: "Database Values Have Not Been Provided!"}
    }

    var url = format('mongodb://%s:%s@%s:27017/%s', mongoDbUser, mongoDbPassword, mongoDbHost, mongoDbDatabase);


      return new Promise(function(resolve, reject) {

        MongoClient.connect(url, function(err, client){
          
          if(err) {
            console.error(err);
            reject({"error":err.message});
            return;
          }

          console.log("Connected to Database. About to Insert");

          var db = client.db(mongoDbDatabase);

          var result = Object.assign(params, {"date": new Date()})

          db.collection('results').insertOne(result, function (err, res) {
            if(err) {
              console.error(err);
              reject({"error":err.message});
              return;
            }
            
            console.log("Document Inserted!");
            client.close();
            resolve({"result": "ok"});
          });

        });

      });
    
  };

exports.main = dbinsert;