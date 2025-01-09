# java-mqtt-pubsub

## Requirements
[Docker](https://www.docker.com/)

## Technologies 
[Java 17 (openjdk)](https://openjdk.org/projects/jdk/17/), [Maven](https://maven.apache.org/), [RabbitMQ](https://www.rabbitmq.com/)

## sh folder

The sh folder allows you to quickly launch docker commands to perform tasks

## Build project
On the root folder
```bash
sh sh/build
```

## Start RabbitMQ server
```bash
sh sh/broker
```

The service is deployed at http://localhost:5672

HTTP API / UI management is deployed at http://localhost:15672

## Run consumer
```bash
sh sh/consumer
```

Run java class to listen messages from the broker 

## Run producer
```bash
sh sh/producer
```

Run java class to send messages to the broker 
