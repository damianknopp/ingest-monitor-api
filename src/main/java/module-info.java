// Seeing a split module error message
//  ie " module spring.webflux reads package com.amazonaws.auth.policy.actions from both aws.java.sdk.sqs and aws.java.sdk.cloudwatch"
// https://github.com/aws/aws-sdk-java/issues/1350
//
module dmk.aws.ingest.monitor.api {
//    requires java.xml.bind;
//    requires javax.transaction.api;
//    requires validation.api;
    requires spring.beans;
    requires spring.core;
    requires spring.context;
    requires spring.tx;
    requires spring.web;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires slf4j.api;
    requires spring.webflux;
    requires springfox.core;
    requires reactor.core;
    requires org.apache.commons.lang3;
    requires aws.java.sdk.cloudwatch;
    requires aws.java.sdk.core;
    requires aws.java.sdk.kinesis;
    requires aws.java.sdk.lambda;
    requires aws.java.sdk.sqs;
    requires jasypt;
}