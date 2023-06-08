# horreum-cli

A CLI tool to interact with a remote Horreum service

## pre-requists

 - Java 17+
 - mvn 3.8.6+
 - running instance of Horreum (https://github.com/Hyperfoil/Horreum)

## building

1. build the project run;

```shell
$ ./mvnw clean package
```

2. create `.env` file with horreum configuration

```shell
$ cat ./.env 
horreum.uri=http://localhost:8080
horreum.user=user
horreum.password=secret
```

## run cli

To run the cli tool;

```shell
$ java -jar ./target/quarkus-app/quarkus-run.jar [-hV] [COMMAND]
```

For example;

```shell
$ java -jar ./target/quarkus-app/quarkus-run.jar test list
Found Tests: 
114 - amq-broker-message-processing
226 - aws-kinesis-sink-connector
```

## Available commands

```shell
Commands:
  horreum-uri  Show horreum current URI
  run
      data                 Get run data payload
      dataset              Get dataset payload
      summary              List all datasets
      list                 List all runs
      regression           Perform regression analysis on existing run
      new                  Upload a new run
      new-with-regression  Upload a new run and test datasets for regression
  test
      info                  Print detail of a test
      list                  List all tests

```