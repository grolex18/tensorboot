package com.algr.tensorboot.classifier;

public interface Classifier<I, O> {
    O classify(I output);
    void release();
}
