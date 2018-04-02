function main(params) {

    // Format the Topic to replace . with /
    if(params.topic) {
      params.topic = params.topic.replace(/[.]/g,'/');
    }

    // Parse the input data to provide lat/long
    if(params.data) {
      data_values = params.data.split(" ");

      params.latitude = data_values[0];
      params.longitude = data_values[1];
    }

    return params;
  }