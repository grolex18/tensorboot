package com.algr.tensorboot.controller;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.algr.tensorboot.classifier.Classifier;
import com.algr.tensorboot.classifier.impl.PooledClassifier;
import com.algr.tensorboot.controller.error.ServiceException;
import com.algr.tensorboot.data.Recognition;
import com.algr.tensorboot.data.RecognitionResult;
import com.algr.tensorboot.util.ImageUtil;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class ImageProcessingService {

    private final Classifier classifier;
    private final int previewSize;

    @Autowired
    public ImageProcessingService(Classifier classifier,
                                  @Value("${tensorboot.maxExecutorsCount}") int maxExecutorsCount,
                                  @Value("${tensorboot.previewSize}") int previewSize
    ) {
        this.classifier = new PooledClassifier(classifier, maxExecutorsCount);
        this.previewSize = previewSize;
    }

    public RecognitionResult processImageFile(MultipartFile file) {
        if (file == null) {
            throw new ServiceException("Failed reading image file");
        }
        BufferedImage image = null;
        try {
            image = ImageIO.read(file.getInputStream());
            if (image == null) {
                throw new ServiceException("Failed reading image file");
            }
            List<Recognition> recognitions = classifier.processImage(image);
            BufferedImage imagePreview = getImagePreview(image);
            return new RecognitionResult(imagePreview, recognitions);
        } catch (IOException e) {
            log.info("Error during reading file input stream", e);
            return new RecognitionResult();
        } finally {
            if (image != null) {
                image.flush();
            }
        }
    }

    private BufferedImage getImagePreview(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        float scale = (float) previewSize / width;
        return ImageUtil.scaleImage(image, previewSize, (int) (height * scale));
    }
}
