const request = require('request')
let _resolve, _reject
let default_provider_endpoint = "http://mqtt-provider.iot-serverless.svc:8080/api/feed"

function main (msg) {
  console.dir(msg);
  let promise;

  provider_endpoint = msg.hasOwnProperty('provider_endpoint') ? msg.provider_endpoint : default_provider_endpoint

  if (msg.lifecycleEvent === 'CREATE') {
    promise = new Promise( (resolve, reject) => {
      _resolve = resolve
      _reject = reject
      create(msg, provider_endpoint)
    })
  } else if (msg.lifecycleEvent === 'UPDATE') {
    promise = new Promise( (resolve, reject) => {
      _resolve = resolve
      _reject = reject
      remove(msg, provider_endpoint)
    })
  } else if (msg.lifecycleEvent === 'DELETE') {
    promise = new Promise( (resolve, reject) => {
      _resolve = resolve
      _reject = reject
      remove(msg, provider_endpoint)
    })
  }
  return (typeof promise !== 'undefined' ? promise : {done: true})
}

function create (msg, provider_endpoint) {
  if (!msg.hasOwnProperty('topic')) {
    _reject({done:true, error: 'Missing mandatory feed properties, must include topic.' })
  }

  const body = {
    triggerName: msg.triggerName,
    topic: msg.topic,
    authKey: msg.authKey
  }
  console.dir(body)
  request({
    method: "POST",
    uri: provider_endpoint,
    json: body
  }, handle_response)
}

function remove (msg, provider_endpoint) {
  request({
    method: "DELETE",
    uri: provider_endpoint + msg.triggerName
  }, handle_response)
}

function handle_response (err, res, body) {
  if (!err && (res.statusCode === 200 || res.statusCode === 204)) {
    console.log('mqtt feed: http request success.')
    _resolve({done: true})
  } 

  if(res) {
    console.log('mqtt feed: Error invoking provider: ', res.statusCode, body)
    _reject({done: true, error: 'mqtt feed: Error invoking provider: ' + res.statusCode + '\n' + JSON.stringify(body, null, 4)})
  } else {
    console.log('mqtt feed: Error invoking provider:', err);
    _reject ({done: true, error: 'mqtt feed: Error invoking provider: ' + err })
  }
}