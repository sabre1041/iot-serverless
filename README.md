# iot-serverless

Serverless technologies to manage and process Internet of Things (IoT) assets

## Prerequisites

The following prerequisites must be met in order to properly deploy the project

* OpenShift cluster
* OpenWhisk Installed (Can use [this](https://github.com/projectodd/openwhisk-openshift) implementation) in a project called `openwhisk`
* Ansible (For automated provisioning)
* [Node](https://nodejs.org/en/) and [npm](https://www.npmjs.com/)

## Deploying the Infrastructure

Use the following steps to provision the project

1. Clone the repository
   
    ```
    git clone https://github.com/sabre1041/iot-serverless
    ```

2. Change directories into the repository

    ```
    cd iot-serverless
    ```

3. Update Ansible Dependencies

    ```
    ansible-galaxy install -r requirements.yml --roles-path=galaxy
    ```

4. Run _openshift-applier_

    ```
    ansible-playbook -i applier/inventory galaxy/openshift-applier/playbooks/openshift-cluster-seed.yml
    ```

Once complete, all of the resources should be available in OpenShift

### SDN Considerations

Since OpenWhisk communicates with resources not only within its own namespace, but resources that were deployed in the prior section, additional steps may be necessary if the [multitenant-sdn-plugin](https://docs.openshift.com/container-platform/latest/install_config/configuring_sdn.html) has been configured. 

Join the two networks by issuing the following command:

```
oc adm pod-network join-projects --to=openwhisk iot-serverless
```

## OpenWhisk Configurations

This section describes how to configure OpenWhisk for the project

### Package Creation

Create a package

    ```
    wsk -i package create --shared yes iot-serverless
    ```

## Configure Actions

Several actions are available as function to perform a specific functionality:

1. Create an action to format the input

    ```
    wsk -i action update formatInput iot-serverless-openwhisk-functions/format/format_input.js
    ```

2. Create an action to enrich the input

    1. Install dependencies

    ```
    pushd  iot-serverless-openwhisk-functions/enricher
    npm install
    ```
    2. Configure environment variables

    The action makes use of environment variables that describe how to connect to the mongodb instance. The function will utilize a file called `.env` within the project folder. A template of this file called [example.env](iot-serverless-openwhisk-functions/enricher/example.env) is available to manually provide the set of properties. The required values are sourced from the mongodb secret that was created when the mongodb instance was provisioned.

    Copy the template file to create the required file:

    ```
    cp example.env .env
    ```

    The following values must be configured:

    * MongoDB Username
    * MondoDB Password
    * MongoDB Database

    These values are stored in the mongodb secret which can be seen by running the following command

    ```
    oc describe secret mongodb -n iot-serverless
    ```

    Underneath _Data_, notice the keys defined within the secret which we can utilize.

    To obtain a particular value, execute the following command:

    ```
    oc get secrets mongodb -o jsonpath='{.data.<key>}' -n iot-serverless | base64 -d
    ```

    Replace `<key>` with the particular key from the mongodb secret (such as database-name).

    Note: When running on OSX, you will need to modify the `base64` command above to be `base64 -D`

    Update the `.env` file with the decoded values

    3. Package and deploy the function

    ```
    npm run package
    npm run deploy
    popd 
    ```

3. Create a Sequence Action

    ```
    wsk -i action update processAsset --sequence formatInput,enricher
    ```


## Configure MQTT Feed


1. Create Feed Action

    ```
    wsk -i action update -a feed true iot-serverless/mqttFeed iot-serverless-mqtt-feed/action/feed_action.js
    ```

## Configure OpenWhisk for Software Sensor

2. Create Trigger

    ```
    wsk -i trigger create softwareSensorTrigger --feed iot-serverless/mqttFeed -p topic ".sf.>"
    ```

3. Create rule

    ```
    wsk -i rule create softwareSensorRule softwareSensorTrigger processAsset
    ```

## Scale Up Software Sensor

By default, the Software sensor contains 0 replicas. Execute the following command to scale the software sensor to 1 replica:

    ```
    oc scale dc/software-sensor --replicas=1
    ```