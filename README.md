# Spring Live 2020
This application demonstrates how to build reliable streaming pipelines.
It is extremely difficult to build event driven streaming pipelines that 
guarantee delivery across distributed systems. This application shows
how easy it is build such pipelines using an [Reactor RabbitMQ Streams](https://pivotal.github.io/reactor-rabbitmq-streams/docs/current/)

## Key Features 
In this demo we will highlight following features of [Reactor RabbitMQ Streams](https://pivotal.github.io/reactor-rabbitmq-streams/docs/current/)
* Use [Reactor RabbitMQ Streams](https://pivotal.github.io/reactor-rabbitmq-streams/docs/current/) as auto-configuration for Spring Boot.
* Configure RabbitMQ endpoints via externalized config i.e. [applicattion.yaml](https://github.com/msathe-tech/spring-live-reactor-rmq-streams/blob/master/src/main/resources/application.yaml)
* Manage RabbitMQ topology as code
* Use Avro schema for defining business events
* Create strongly typed streams using Avro objects
* Transform events 
* Transaction semantics across distributed systems 
* ...all of the above using simple reactor and functional APIs


## Quick steps

* Setup your app
```shell script
git clone https://github.com/msathe-tech/spring-live-reactor-rmq-streams
cd spring-live-reactor-rmq-streams
mvn package
```

* Start the local RabbitMQ. You need docker on your workstation. 
```shell script
./deploy-rabbit
``` 

* Access the RabbitMQ management console using [localhost:15672](http://localhost:15672). Use guest/guest for credentials.

* Create topology 
```shell script
./setup-topology
```

* Verify the topology using management console [localhost:15672](http://localhost:15672)

* From a separate shell (try to split your pane if possible so that you can see everything together) start the multiplier application
```shell script
./multipler
```

* From a separate shell (try to split your pane if possible so that you can see everything together) start the producer application
```shell script
./producer
```

* Watch the movement of messages using management console [localhost:15672](http://localhost:15672)


