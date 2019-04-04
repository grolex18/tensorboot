package com.algr.tensorboot.classifier.impl;

import java.util.List;
import org.springframework.util.Assert;
import org.tensorflow.Graph;
import org.tensorflow.Session;
import org.tensorflow.Tensor;

import com.algr.tensorboot.classifier.Classifier;
import lombok.extern.log4j.Log4j2;

/**
 * Abstract classifier that implements Tensorflow-specific operations.
 * Need to implement abstract {@link #getInputTensor(Object)} and {@link #convertToResult(Tensor)} methods.
 * Classifier should be initialized using {@link #init(byte[], String, String)} before usage.
 * @param <I> Classifier input type
 * @param <O> Classifier output type
 * @param <T> Tensorflow model tensor data type
 */
@Log4j2
public abstract class AbstractClassifier<I, O, T> implements Classifier<I, O> {
    private Graph model;
    private String inputLayerName;
    private String outputLayerName;

    /**
     * Converts classifier input into tensor for processing.
     */
    protected abstract Tensor<T> getInputTensor(I input);
    /**
     * Converts classifier output into classifier response.
     */
    protected abstract O convertToResult(Tensor<T> output);

    /**
     * Initialize classifier
     * @param graphBytes Model graph binary data
     * @param inputLayerName  Input layer name
     * @param outputLayerName Output layer name
     */
    public void init(byte[] graphBytes, String inputLayerName, String outputLayerName) {
        Assert.notNull(graphBytes, "Model data shouldn't be null");
        Assert.notNull(inputLayerName, "Input layer name shouldn't be null");
        Assert.notNull(outputLayerName, "Output layer name shouldn't be null");

        model = new Graph();
        model.importGraphDef(graphBytes);
        this.inputLayerName = inputLayerName;
        this.outputLayerName = outputLayerName;
    }

    @Override
    public O classify(I input) {
        Assert.notNull(model, "Classifier is not initialized");
        log.trace("Started image processing");
        try (Session session = new Session(model);
             Tensor<T> inputTensor = getInputTensor(input)) {
            log.trace("Started inference");
            List<Tensor<?>> outputs =
                    session
                            .runner()
                            .feed(inputLayerName, inputTensor)
                            .fetch(outputLayerName)
                            .run();

            log.trace("Finished inference");
            try (@SuppressWarnings("unchecked") Tensor<T> output = (Tensor<T>) outputs.get(0)) {
                return convertToResult(output);
            }
        }
    }

    @Override
    public void release() {
        log.info("Releasing model");
        model.close();
    }
}
