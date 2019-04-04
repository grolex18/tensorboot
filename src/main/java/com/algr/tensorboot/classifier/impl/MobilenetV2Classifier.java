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
import org.tensorflow.Tensor;

import com.algr.tensorboot.config.ModelConfig;
import com.algr.tensorboot.data.Recognition;
import com.algr.tensorboot.util.ImageUtil;
import lombok.extern.log4j.Log4j2;

/**
 * Mobilenet V2 classifier implementation.
 */
@Log4j2
@Component
public class MobilenetV2Classifier extends AbstractClassifier<BufferedImage, List<Recognition>, Float> {
    private static final int BYTES_IN_MB = 1024 * 1024;

    private final ModelConfig modelConfig;
    private List<String> labels;

    @Autowired
    public MobilenetV2Classifier(ModelConfig modelConfig) {
        this.modelConfig = modelConfig;
    }

    @PostConstruct
    public void setup() {
        try {
            byte[] graphBytes = FileUtils.readFileToByteArray(new File(modelConfig.getModelPath()));
            init(graphBytes, modelConfig.getInputLayerName(), modelConfig.getOutputLayerName());

            List<String> labelsList = IOUtils.readLines(modelConfig.getLabelsResource().getInputStream(), "UTF-8");
            labels = new ArrayList<>(labelsList);
            log.info("Initialized Tensorflow model ({}MB)", graphBytes.length / BYTES_IN_MB);
        } catch (IOException e) {
            log.error("Error during classifier initialization:", e);
        }
    }

    @Override
    protected Tensor<Float> getInputTensor(BufferedImage image) {
        BufferedImage croppedImage = ImageUtil.cropImageToRect(image);
        BufferedImage scaledImage = ImageUtil.scaleImage(croppedImage, modelConfig.getInputSize(), modelConfig.getInputSize());
        return ImageUtil.makeImageTensor(scaledImage, modelConfig.getImageMean(), modelConfig.getImageStd());
    }

    @Override
    protected List<Recognition> convertToResult(Tensor<Float> output) {
        int maxObjects = (int) output.shape()[1];
        float[] result;
        result = output.copyTo(new float[1][maxObjects])[0];
        return convertToRecognitions(result);
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
        release();
    }

}
