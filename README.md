# Spring Live 2020
This application demonstrates how to build reliable streaming pipelines.
It is extremely difficult to build event driven streaming pipelines that 
guarantee delivery across distributed systems. This application shows
how easy it is build such pipelines using an [Reactor RabbitMQ Streams](https://pivotal.github.io/reactor-rabbitmq-streams/docs/current/)

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

* Access the RabbitMQ management console using [localhost:15672](localhost:15672). Use guest/guest for credentials.

* Create topology 
```shell script
./setup-topology
```

* Verify the topology using management console [localhost:15672](localhost:15672)

* From a separate shell (try to split your pane if possible so that you can see everything together) start the multiplier application
```shell script
./multipler
```

* From a separate shell (try to split your pane if possible so that you can see everything together) start the producer application
```shell script
./producer
```


