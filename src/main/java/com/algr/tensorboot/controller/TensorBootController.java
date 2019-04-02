package com.algr.tensorboot.controller;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.algr.tensorboot.controller.error.ServiceException;
import com.algr.tensorboot.data.Recognition;
import com.algr.tensorboot.data.RecognitionResult;
import lombok.extern.log4j.Log4j2;
import springfox.documentation.annotations.ApiIgnore;

@Log4j2
@ApiIgnore
@Controller
public class TensorBootController {
    private static final String IMAGE_ATTR = "IMAGE";

    private final ImageProcessingService imageProcessingService;

    @Autowired
    public TensorBootController(ImageProcessingService imageProcessingService) {
        this.imageProcessingService = imageProcessingService;
    }

    @RequestMapping(value = "/uploadForm", method = RequestMethod.GET)
    public String handleGetForm() {
        return "uploadForm";
    }

    @RequestMapping(value = "/img", method = RequestMethod.GET)
    public void handleGetImg(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {
        BufferedImage image = (BufferedImage) httpServletRequest.getSession().getAttribute(IMAGE_ATTR);
        if (image != null) {
            httpServletResponse.setContentType("image/png");
            ImageIO.write(image, "png", httpServletResponse.getOutputStream());
        } else {
            httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @RequestMapping(value = "/uploadForm", method = RequestMethod.POST)
    public String handleUploadForm(@RequestParam("file") MultipartFile file, Model model, HttpServletRequest httpServletRequest) {
        log.debug("Image upload requested");
        RecognitionResult recognitionResult = imageProcessingService.processImageFile(file);
        List<Recognition> recognitions = recognitionResult.getRecognitions();
        httpServletRequest.getSession().setAttribute(IMAGE_ATTR, recognitionResult.getImagePreview());
        log.debug("Found objects: {}", recognitions);
        if (recognitions.isEmpty()) {
            model.addAttribute("message", "No objects found");
        } else {
            model.addAttribute("recognitions", recognitions);
        }

        return "uploadForm";
    }

    @ExceptionHandler(RuntimeException.class)
    public ModelAndView doResolveException(RuntimeException e) {
        ModelAndView modelAndView;
        if (e instanceof MaxUploadSizeExceededException) {
            modelAndView = new ModelAndView("uploadForm");
            modelAndView.getModel().put("message", "File is too large");
        } else if (e instanceof ServiceException) {
            log.info("Error during processing request", e);
            modelAndView = new ModelAndView("uploadForm");
            modelAndView.getModel().put("message", e.getMessage());
        } else {
            log.info("Error during processing request", e);
            modelAndView = new ModelAndView("error");
            modelAndView.getModel().put("message", "Internal server error");
        }
        return modelAndView;
    }
}