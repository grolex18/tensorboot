package com.algr.tensorboot;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.IOException;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.algr.tensorboot.filter.MDCFilter;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class TensorBootRestControllerIT {

    private static final String TEST_IMAGE_RESOURCE = "/banana.jpg";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MDCFilter mdcFilter;

    private byte[] testImage;

    @Before
    public void setup() throws IOException {
        mdcFilter.init(null);
        testImage = IOUtils.toByteArray(TensorBootRestControllerIT.class.getResourceAsStream(TEST_IMAGE_RESOURCE));
    }

    @Test
    public void testRecognizeFile() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.multipart("/TensorApi/recognizeFile")
                .file(new MockMultipartFile("file", "banana.jpg", "image/jpeg", testImage)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("fruit, banana")))
                .andExpect(jsonPath("$[0].confidence", is(0.9789109)));
    }
}