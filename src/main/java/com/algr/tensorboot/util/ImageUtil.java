package com.algr.tensorboot.util;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.nio.FloatBuffer;
import org.tensorflow.Tensor;

public class ImageUtil {
    public static BufferedImage cropImageToRect(BufferedImage input) {
        int w = input.getWidth();
        int h = input.getHeight();
        int outSize = Math.min(w, h);
        int shiftX = w > outSize ? (w - outSize) / 2 : 0;
        int shiftY = h > outSize ? (h - outSize) / 2 : 0;
        return input.getSubimage(shiftX, shiftY, outSize, outSize);
    }

    public static BufferedImage scaleImage(BufferedImage input, int outWidth, int outHeight) {
        int w = input.getWidth();
        int h = input.getHeight();
        BufferedImage output = new BufferedImage(outWidth, outHeight, BufferedImage.TYPE_INT_ARGB);
        AffineTransform at = new AffineTransform();
        at.scale((float) outWidth / w, (float) outHeight / h);
        AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BICUBIC);
        output = scaleOp.filter(input, output);

        return output;
    }

    public static Tensor<Float> makeImageTensor(BufferedImage img, int imageMean, float imageStd) {
        // DirectColorModel: rmask=ff0000 gmask=ff00 bmask=ff amask=ff000000
        int[] data = ((DataBufferInt) img.getData().getDataBuffer()).getData();
        float[] fdata = new float[data.length * 3];
        for (int i = 0; i < data.length; i++) {
            int val = data[i];
            fdata[i * 3 + 0] = (((val >> 16) & 0xFF) - imageMean) / imageStd;
            fdata[i * 3 + 1] = (((val >> 8) & 0xFF) - imageMean) / imageStd;
            fdata[i * 3 + 2] = ((val & 0xFF) - imageMean) / imageStd;
        }
        final long BATCH_SIZE = 1;
        final long CHANNELS = 3;
        long[] shape = new long[]{BATCH_SIZE, img.getHeight(), img.getWidth(), CHANNELS};
        return Tensor.create(shape, FloatBuffer.wrap(fdata));
    }
}
