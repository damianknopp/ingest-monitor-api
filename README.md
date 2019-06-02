# AWS Ingest Endpoints

This project contains a simple RESTful wrapper around AWS endpoints used to monitor ingest

## Prerequisites

Download [jasypt 1.9.3](https://github.com/jasypt/jasypt/releases/tag/jasypt-1.9.3)

Consider [sdkman](https://sdkman.io/) to download java openjdk 11+

Or download java [openjdk 11+](https://jdk.java.net/archive/) directly

## Setup
This project needs an AWS access key and access secret


If you downloaded jasypt, run this round trip script to generate and verify your encrypted values

```bash
#!/bin/bash

# these values should be the same as what is expected in AwsCredsConfig
algorithm=PBEWITHMD5ANDTRIPLEDES
itr=9000
pass="uA8-S?(8D{I@*S\Si"
# these value should be what are in your ~/.aws/credentials file
val1="drink_more_ovaltine"
ivClass='org.jasypt.iv.RandomIvGenerator'
./encrypt.sh input=$val1 password=$pass algorithm=$algorithm keyObtentionIterations=$itr ivGeneratorClassName=$ivClass
# update if you change the original val1
enc1='/L1VKc/qWM8XAQkOCLO50CTs5AfrWjP3PlUdXEQnRPel4/wIzAq8Mw=="'
./decrypt.sh input=$enc1 password=$pass algorithm=$algorithm keyObtentionIterations=$itr ivGeneratorClassName=$ivClass
```

Once you have a value that looks like this `NXizB8v7GdFlDpUFLHO5OethTMZoJNYrQOkRfd3LbOIYIy9ctQUFTA==` add it to `aws-creds-development.properties` 

Like this;
```properties
dmk.aws.access.key=ENC(NXizB8v7GdFlDpUFLHO5OethTMZoJNYrQOkRfd3LbOIYIy9ctQUFTA==)
dmk.aws.access.secret=ENC(aws_encrypted_secret_here)
```

**Dont check the key into the repo!**


Next you will need to create a Lambda, event source mapping, and related source (SQS, Kinesis, etc)
If you do not already have something in place, this project's scripts can help you get setup quickly [dmk-aws](https://github.com/damianknopp/dmk-aws)

## Build while developing

* Set the ide to auto compile
    * For Intellij
        * set Build -> reload classes in background always
        * Set additional path to current dir
* Watch for file changes
```bash
mvn spring-boot:run
```


_See_ `application-development.properties` and 
```
spring.devtools.restart.additional-paths=.
spring.devtools.livereload.enabled=true
spring.devtools.restart.enabled=true
```

Editing a file should reload the server with updated code.
_Note:_ The swagger docs do not seem to update, 
    and the server does not always have updated code on my machine. So the dev hot reloading is probably b0rked
## Build to deploy
```bash
mvn clean install
```

This should produce a jar in the `target` directory

## View
Visit the local [swagger page](http://localhost:8080/swagger-ui.html)


## Post-Mortem

Originally this project was to use [Spring Webflux](https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html) and the Java [AWS v2 API](https://aws.amazon.com/sdk-for-java/)
The Webflux reactive API and AWS v2 API both use asynchronous constructs that should work nicely together. Including, the [Mono.fromFuture](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html) and the many AWS v2 client calls that return a [CompletableFuture](https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/kinesis/KinesisAsyncClient.html)
The goal was to chain them together like so
```java
return Mono.fromFuture(kinesisAsyncClient.listStreams())
```
However Spring Webflux and AWS v2 APIs both use Netty and there appears to be a conflict on the classpath between the two versions. In fact the AWS v2 API embeds a [forked version of Netty](https://sdk.amazonaws.com/java/api/latest/io/netty/handler/codec/http2/ForkedHttp2MultiplexCodec.html) classes in AWS jars.

Instead of resolving the conflict, I used the AWS v1 API and used a more verbose and brittle asynchronous construct like so;

```java
Mono.fromFuture(CompletableFuture.supplyAsync(() -> {
    try {
        return kinesisAsyncClient.listStreamsAsync().get();
    } catch (Exception e) {
        throw new RuntimeException(e);
    }
}, scheduledExecutorService));
```

Next, the Controller style Webflux was used vs the Functional style since Controller style seemed to play nicer with Swagger documentation

AWS credentials were embedded in the application. Running the code in an environment where IAM roles can be used is probably more ideal. Eventually the goal is to use oauth