package com.algr.tensorboot.classifier.impl;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.algr.tensorboot.classifier.Classifier;
import com.algr.tensorboot.controller.error.ServiceException;
import com.algr.tensorboot.data.Recognition;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class PooledClassifier implements Classifier {

    private final Classifier delegate;
    private final ExecutorService executorService;

    public PooledClassifier(Classifier delegate, int maxExecutorsCount) {
        this.delegate = delegate;
        executorService = Executors.newFixedThreadPool(maxExecutorsCount);
    }

    public List<Recognition> processImage(BufferedImage image) {
        try {
            Future<List<Recognition>> future = executorService.submit(() -> delegate.processImage(image));
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
}
