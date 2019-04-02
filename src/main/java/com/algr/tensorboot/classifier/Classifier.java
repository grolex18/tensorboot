package com.algr.tensorboot.classifier;

import java.awt.image.BufferedImage;
import java.util.List;

import com.algr.tensorboot.data.Recognition;

public interface Classifier {
    List<Recognition> processImage(BufferedImage image);
}
