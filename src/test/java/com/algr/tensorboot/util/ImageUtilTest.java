package com.algr.tensorboot.util;

import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.image.BufferedImage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ImageUtilTest {

    @Mock
    private BufferedImage imageMock;
    @Mock
    private BufferedImage croppedMock;

    @Test
    public void cropImageToRectTest() {
        cropImageToRectTest(100, 200, 0, 50, 100, 100);
        cropImageToRectTest(200, 100, 50, 0, 100, 100);
        cropImageToRectTest(200, 200, 0, 0, 200, 200);
    }

    private void cropImageToRectTest(int inputWidth, int inputHeight, int x, int y, int outWidth, int outHeight) {
        // Setup
        when(imageMock.getWidth()).thenReturn(inputWidth);
        when(imageMock.getHeight()).thenReturn(inputHeight);
        when(imageMock.getSubimage(anyInt(), anyInt(), anyInt(), anyInt())).thenReturn(croppedMock);

        // Exercise
        BufferedImage result = ImageUtil.cropImageToRect(imageMock);

        // Verify
        verify(imageMock).getSubimage(x, y, outWidth, outHeight);
        assertSame(croppedMock, result);
    }
}