package com.algr.tensorboot.classifier.impl;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.algr.tensorboot.classifier.Classifier;
import com.algr.tensorboot.controller.error.ServiceException;
import lombok.extern.log4j.Log4j2;

/**
 * Pooled classifier wrapper to limit the number of simultaneous threads handling TF sessions.
 */
@Log4j2
public class PooledClassifier<I, O> implements Classifier<I, O> {

    private final Classifier<I, O> delegate;
    private final ExecutorService executorService;

    public PooledClassifier(Classifier<I, O> delegate, int maxExecutorsCount) {
        this.delegate = delegate;
        executorService = Executors.newFixedThreadPool(maxExecutorsCount);
    }

    public O classify(I input) {
        try {
            Future<O> future = executorService.submit(() -> delegate.classify(input));
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error during task processing", e);
            if (e.getCause() instanceof ServiceException) {
                throw (ServiceException) e.getCause();
            } else {
                throw new ServiceException("Internal server error");
            }
        }
    }

    @Override
    public void release() {
        executorService.shutdown();
    }
}
