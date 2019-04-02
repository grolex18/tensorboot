package com.algr.tensorboot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import lombok.Getter;

@Getter
@Configuration
public class ModelConfig {
    @Value("${tensorboot.model.inputSize}")
    private Integer inputSize;

    @Value("${tensorboot.model.imageMean}")
    private Integer imageMean;

    @Value("${tensorboot.model.imageStd}")
    private Float imageStd;

    @Value("${tensorboot.model.inputLayerName}")
    private String inputLayerName;

    @Value("${tensorboot.model.outputLayerName}")
    private String outputLayerName;

    @Value("${tensorboot.model.path}")
    private String modelPath;

    @Value("${tensorboot.model.labelsResource}")
    private Resource labelsResource;

    @Value("${tensorboot.model.threshold}")
    private Float threshold;
}
