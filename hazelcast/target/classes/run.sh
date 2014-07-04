#!/bin/sh

java -server -Djava.net.preferIPv4Stack=true -cp ../lib/hazelcast-3.0.1.jar com.hazelcast.examples.TestApp

