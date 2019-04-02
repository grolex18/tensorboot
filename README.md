# TensorBoot project
Demo application that deploys TensorFlow models as a SpringBoot microservice. 
Exposes REST services (with Swagger html docs) and simple web page for image recognition using [MobilenetV2](https://github.com/tensorflow/models/tree/master/research/slim/nets/mobilenet) pretrained model. 

## Prerequisites
To build the project, you need to have these installed:
   JDK 8+ - download it from [here](https://www.oracle.com/technetwork/java/javase/downloads/).

## Download model
```sh
$ ./downlod_model.sh
```
Model will be stored under [model](/model/) folder.

Also you can use *downloadModel* maven profile to download model during build:
```sh
$ ./mvnw clean install -PdownloadModel
```

## Build project

To build the project, run:

```sh
$ ./mvnw clean install
```

## Running the application locally

```sh
$ ./run.sh
```
Possible parameters for script are:
```
Usage: ./run.sh [-c -d]
   -c Recompile project
   -d Enable debug
```

## Services endpoints
Main application page: [http://localhost:8080/](http://localhost:8080/)

UI file upload page: [http://localhost:8080/uploadForm](http://localhost:8080/uploadForm)

Rest services are deployed under [http://localhost:8080/TensorApi](http://localhost:8080/TensorApi) context

## Swagger

All REST services have their definitions exposed using Swagger. Once the demo is running locally you can access Swagger using these URLS:

* [Swagger UI](http://localhost:8080/swagger-ui.html)
* [JSON contract](http://localhost:8080/v2/api-docs)

# Logging
Application is configured to log to console and into file.
You can find daily rolling log files under "log" folder.

# Deploy as Docker container
Build container using profile 'docker':

```sh
$ ./mvnw clean package -Pdocker
```
Run container:

```sh
$ docker run -p 8081:8080 -t tensorboot
```

Or you can use script (by default, services are exposed on port 8081):

```sh
$ ./run-docker.sh
```