# iot-serverless

Serverless technologies to manage and process Internet of Things (IoT) assets

## Prerequisites

The following prerequisites must be met in order to properly deploy the project

* OpenShift cluster
* OpenWhisk Installed (Can use [this](https://github.com/projectodd/openwhisk-openshift) implementation) in a project called `openwhisk`
* Ansible (For automated provisioning)

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

## Configure MQTT Feed

1. Create package

    ```
    wsk -i package create --shared yes iot-serverless
    ```

2. Create Feed Action

    ```
    wsk -i action update -a feed true iot-serverless/mqttFeed iot-serverless-mqtt-feed/action/feed_action.js
    ```

## Configure OpenWhisk for Software Sensor

1. Create Trigger

    ```
    wsk -i trigger create softwareSensorTrigger --feed iot-serverless/mqttFeed -p topic ".sf.>"
    ```

2. Create Action

    ```
    wsk -i action create formatTopicAction iot-serverless-openwhisk-action/format_topic.js
    ```

3. Create rule

    ```
    wsk -i rule create softwareSensorRule softwareSensorTrigger formatTopicAction
    ```

## Scale Up Software Sensor

By default, the Software sensor contains 0 replicas. Execute the following command to scale the software sensor to 1 replica:

    ```
    oc scale dc/software-sensor --replicas=1
    ```