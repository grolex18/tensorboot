#!/usr/bin/env bash
CID=`docker ps -q --filter ancestor=tensorboot`
if [[ ! -z $CID ]];then
	echo "Stopping existing container..."
	docker stop -t 1 $CID
fi
echo "Building container..."
./mvnw clean package -DskipTests=true -Pdocker -PdownloadModel
echo "Starting container..."
docker run -p 8081:8080 -t tensorboot