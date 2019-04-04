# TensorBoot project
Demo application that deploys TensorFlow models as a SpringBoot microservice. 
Exposes REST services (with Swagger html docs) and simple web page for image recognition using 
[MobilenetV2](https://github.com/tensorflow/models/tree/master/research/slim/nets/mobilenet) pretrained model. 

Contains examples of image file upload using form and how to use browser webcam and call REST service using javascript.

<img src="https://github.com/Grolex18/tensorboot/blob/master/img/Screen1.png" width="300">
<img src="https://github.com/Grolex18/tensorboot/blob/master/img/Screen2.png" width="300">

## Prerequisites
To build the project, you need to have these installed:
   JDK 8+ - download it from [here](https://www.oracle.com/technetwork/java/javase/downloads/).

## Download model
*Required before builds/deployment*
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

## Application configuration file
Application is configured in [application.yml](src/main/resources/application.yml):

```yaml
services:
  baseApiPath: /TensorApi                                            # Application services context path

spring:
  servlet:
    multipart:
      enabled: true
      resolve-lazily: true
      max-file-size: 1512KB                                          # Upload file size limit
      max-request-size: 1512KB                                       # Upload request size limit

tensorboot:
  model:
    path: model/mobilenet_v2_1.4_224_frozen.pb                       # Path to model file
    inputSize: 224                                                   # The input size. A square image of inputSize x inputSize is assumed.
    imageMean: 0                                                     # The assumed mean of the image values. 
    imageStd: 255                                                    # The assumed std of the image values.
    inputLayerName: input                                            # The label of the image input node.
    outputLayerName: MobilenetV2/Predictions/Reshape_1               # The label of the output node.
    labelsResource: classpath:/mobilenet_v2_labels.txt               # Path to resource with labels
    threshold: 0.1                                                   # Object detection threshold
  previewSize: 320                                                   # Width of the previews
  maxExecutorsCount: 10                                              # Executors pool size for images processing

server:
  servlet:
    session:
      timeout: 2m                                                    # Timeout for storing uploaded image previews in sessions 
```

# Deploy as Docker container
Build container using profile *docker*:

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

## Deploy to Heroku

**Note that app is memory intensive, so free nodes will complain**

Login cli to Heroku: 
```sh
$ heroku login
```
Create app:
```sh
$ heroku create
```
Rename app:
```sh
$ heroku apps:rename tensorboot
```
Limit memory (optional for free account)
```sh
$ heroku config:set JAVA_TOOL_OPTIONS="-Xmx300m"
```
Deploy app to Heroku:
```sh
$ ./mvnw clean heroku:deploy
```
Check app logs:
```sh
$ heroku logs --tail
```


## Deploying to Pivotal Cloud Foundry

Build application distribution zip:
```sh
$ ./mvnw clean package -PdistZip
```

Login cli to Cloud Foundry:

```sh
$ cf login -a https://api.run.pivotal.io
API endpoint: https://api.run.pivotal.io

Email> <YOUR EMAIL>

Password> 
Authenticating...
OK
...
```

Push application:
```sh
$ cf push 
```

Check application status:
```sh
$ cf apps 
```

Delete application:
```sh
$ cf delete tensorboot 
```


## TODO

- More model options (text, audio, video examples)
- Use browser camera to catch photos/audio/video
- Add more configuration options and restrictions to simplify production deployment

## License
Copyright (C) 2019 Oleksiy Grechanov

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
