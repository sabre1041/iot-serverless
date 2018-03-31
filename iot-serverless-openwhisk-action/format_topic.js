function main(params) {

    if(params.topic) {
      params.topic = params.topic.replace(/[.]/g,'/');
    }

    return { params };
  }