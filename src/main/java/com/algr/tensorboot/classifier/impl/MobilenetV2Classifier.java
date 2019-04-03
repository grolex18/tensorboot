package com.algr.tensorboot.classifier.impl;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tensorflow.Graph;
import org.tensorflow.Session;
import org.tensorflow.Tensor;

import com.algr.tensorboot.classifier.Classifier;
import com.algr.tensorboot.config.ModelConfig;
import com.algr.tensorboot.data.Recognition;
import com.algr.tensorboot.util.ImageUtil;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class MobilenetV2Classifier implements Classifier {
    private static final int BYTES_IN_MB = 1024 * 1024;

    private final ModelConfig modelConfig;
    private Graph model;
    private List<String> labels;

    @Autowired
    public MobilenetV2Classifier(ModelConfig modelConfig) {
        this.modelConfig = modelConfig;
    }

    @PostConstruct
    public void init() throws IOException {
        byte[] graphDef = FileUtils.readFileToByteArray(new File(modelConfig.getModelPath()));
        model = new Graph();
        model.importGraphDef(graphDef);

        List<String> labelsList = IOUtils.readLines(modelConfig.getLabelsResource().getInputStream(), "UTF-8");
        labels = new ArrayList<>(labelsList);
        log.info("Initialized Tensorflow model ({}MB)", graphDef.length / BYTES_IN_MB);
    }

    @Override
    public List<Recognition> processImage(BufferedImage image) {
        log.trace("Started image processing");
        BufferedImage croppedImage = ImageUtil.cropImageToRect(image);
        BufferedImage scaledImage = ImageUtil.scaleImage(croppedImage, modelConfig.getInputSize(), modelConfig.getInputSize());
        log.trace("Started inference");
        float[] output = runSession(scaledImage, model);
        List<Recognition> recognitions = convertToRecognitions(output);
        log.trace("Finished inference");
        return recognitions;
    }

    private float[] runSession(BufferedImage image, Graph model) {
        try (Session session = new Session(model);
             Tensor<Float> input = ImageUtil.makeImageTensor(image, modelConfig.getImageMean(), modelConfig.getImageStd())) {
            List<Tensor<?>> outputs =
                    session
                            .runner()
                            .feed(modelConfig.getInputLayerName(), input)
                            .fetch(modelConfig.getOutputLayerName())
                            .run();
            try (Tensor<Float> classesT = outputs.get(0).expect(Float.class)) {
                int maxObjects = (int) classesT.shape()[1];
                return classesT.copyTo(new float[1][maxObjects])[0];
            }
        }
    }

    private List<Recognition> convertToRecognitions(float[] classes) {
        List<Recognition> found = new ArrayList<>();
        for (int i = 0; i < classes.length; ++i) {
            if (classes[i] >= modelConfig.getThreshold()) {
                found.add(new Recognition(labels.get(i), classes[i]));
            }
        }
        return found;
    }

    @PreDestroy
    public void tearDown() {
        model.close();
    }

}
