package com.algr.tensorboot.data;

import java.awt.image.BufferedImage;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecognitionResult {

    private BufferedImage imagePreview;
    private List<Recognition> recognitions;
}
