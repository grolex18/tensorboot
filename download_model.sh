#!/usr/bin/env bash

MODEL_DIR=model
MODEL_FILE=mobilenet_v2_1.4_224_frozen.pb

MODEL_ARCHIVE=mobilenet_v2_1.4_224.tgz
MODEL_URL=https://storage.googleapis.com/mobilenet_v2/checkpoints/$MODEL_ARCHIVE

if [ ! -d "$MODEL_DIR" ]; then
    echo "Creating model dir"
    mkdir model
fi

cd $MODEL_DIR

if [ ! -f $MODEL_FILE ]; then
    echo "Downloading model archive..."
    wget $MODEL_URL
    echo "Extracting model file from archive..."
    tar -xf $MODEL_ARCHIVE  ./$MODEL_FILE
    echo "Removing archive..."
    rm mobilenet_v2_1.4_224.tgz
fi
echo "Done"
