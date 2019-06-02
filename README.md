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
pass=""
# these value should be what are in your ~/.aws/credentials file
val1="drink_more_ovaltine"
ivClass='org.jasypt.iv.RandomIvGenerator'
./encrypt.sh input=$val1 password=$pass algorithm=$algorithm keyObtentionIterations=$itr ivGeneratorClassName=$ivClass
# update if you change the original val1
enc1='NXizB8v7GdFlDpUFLHO5OethTMZoJNYrQOkRfd3LbOIYIy9ctQUFTA=='
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

