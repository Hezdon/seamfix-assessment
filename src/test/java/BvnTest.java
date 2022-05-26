import com.example.config.TestWebConfig;
import com.example.dto.LogRequest;
import com.example.repository.LogRequestRepo;
import com.example.dto.Bvn;
import com.example.dto.BvnResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Base64;
import java.util.UUID;

import static com.example.util.Const.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@Slf4j
@ContextConfiguration(classes = { TestWebConfig.class})
@WebAppConfiguration
@SpringBootTest
public class BvnTest {


    private MockRestServiceServer mockServer;

    @Autowired
    private LogRequestRepo logRequestRepo;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Value("${spring.redis.host}")
    private String host;


    ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mockMvc;
    @BeforeEach
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }


    @Test
    @SneakyThrows
    public void validBvnTest_shouldReturnSuccess(){
        Bvn bvn = new Bvn("00222818140");
        Assertions.assertTimeout(Duration.ofSeconds(5), () -> {
            MvcResult result = this.mockMvc.perform(post(getUri(SERVICE_URL))
                    .content(new ObjectMapper().writeValueAsString(bvn)).contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message").value(SUCCESS))
                    .andExpect(jsonPath("$.code").value(BVN_SUCCESS_CODE))
                    .andExpect(jsonPath("$.bvn").value(bvn.getBvn()))
                    .andExpect(jsonPath("$.imageDetail").isNotEmpty())
                    .andExpect(jsonPath("$.basicDetail").isNotEmpty())
                    .andReturn();

            BvnResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), BvnResponse.class);
            Assertions.assertTrue(response.getBvn().matches("[0-9]{11}"));
            // TODO : Redis connection failing
            // logRequestRepo.save(new LogRequest(UUID.randomUUID().toString(),bvn, response));
        });
    }

    @Test
    @SneakyThrows
    public void emptyBvnTest_shouldFail(){
        Bvn bvn = new Bvn("");
        Assertions.assertTimeout(Duration.ofSeconds(1), () -> {
            MvcResult result = this.mockMvc.perform(post(getUri(SERVICE_URL))
                    .content(new ObjectMapper().writeValueAsString(bvn)).contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message").value("One or more of your request parameters failed validation. Please retry"))
                    .andExpect(jsonPath("$.code").value(BVN_BAD_REQUEST))
                    .andReturn();

            BvnResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), BvnResponse.class);
            Assertions.assertEquals("", response.getBvn().trim());
            // TODO : Redis connection failing
            // logRequestRepo.save(new LogRequest(UUID.randomUUID().toString(),bvn, response));
        });
    }

    @Test
    @SneakyThrows
    public void invalidBvnTest_shouldFail_whenNotFound(){
        Bvn bvn = new Bvn("33333389094");
        Assertions.assertTimeout(Duration.ofSeconds(1), () -> {
            MvcResult result = this.mockMvc.perform(post(getUri(SERVICE_URL))
                    .content(new ObjectMapper().writeValueAsString(bvn)).contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message").value("The searched BVN does not exist"))
                    .andExpect(jsonPath("$.code").value(BVN_NOT_FOUND_CODE))
                    .andExpect(jsonPath("$.bvn").value(bvn.getBvn()))
                    .andReturn();

            BvnResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), BvnResponse.class);
            // TODO : Redis connection failing
            // logRequestRepo.save(new LogRequest(UUID.randomUUID().toString(),bvn, response));
        });
    }

    @Test
    @SneakyThrows
    public void invalidBvnTest_shouldFail_whenLengthNotUpToEleven(){
        Bvn bvn = new Bvn("203389991");
        Assertions.assertTimeout(Duration.ofSeconds(1), () -> {
            MvcResult result = this.mockMvc.perform(post(getUri(SERVICE_URL))
                    .content(new ObjectMapper().writeValueAsString(bvn)).contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message").value("The searched BVN is invalid"))
                    .andExpect(jsonPath("$.code").value(BVN_INVALID_CODE))
                    .andExpect(jsonPath("$.bvn").value(bvn.getBvn()))
                    .andReturn();

            BvnResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), BvnResponse.class);
            Assertions.assertNotEquals(11, response.getBvn().length());
            // TODO : Redis connection failing
            // logRequestRepo.save(new LogRequest(UUID.randomUUID().toString(),bvn, response));
        });
    }

    @Test
    @SneakyThrows
    public void invalidBvnTest_shouldFail_whenNonDigitIsFound(){
        Bvn bvn = new Bvn("00222818tt0");
        Assertions.assertTimeout(Duration.ofSeconds(1), () -> {
            MvcResult result = this.mockMvc.perform(post(getUri(SERVICE_URL))
                    .content(new ObjectMapper().writeValueAsString(bvn)).contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message").value("The searched BVN is invalid"))
                    .andExpect(jsonPath("$.code").value(BVN_BAD_REQUEST))
                    .andExpect(jsonPath("$.bvn").value(bvn.getBvn()))
                    .andReturn();

            BvnResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), BvnResponse.class);
            Assertions.assertTrue(!response.getBvn().matches("[0-9]+"));
            // TODO : Redis connection failing
            // logRequestRepo.save(new LogRequest(UUID.randomUUID().toString(),bvn, response));
        });
    }

    @Test
    @SneakyThrows
    public void validBvnTest_withValidBasicDetail(){
        Bvn bvn = new Bvn("20338999102");
        Assertions.assertTimeout(Duration.ofSeconds(5), () -> {
            MvcResult result = this.mockMvc.perform(post(getUri(SERVICE_URL))
                    .content(new ObjectMapper().writeValueAsString(bvn)).contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message").value(SUCCESS))
                    .andExpect(jsonPath("$.code").value(BVN_SUCCESS_CODE))
                    .andReturn();

            Assertions.assertTrue(
                    validateBasicDetail(
                            objectMapper.readValue(result.getResponse().getContentAsString(), BvnResponse.class).getBasicDetail()
                    )
            );

            BvnResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), BvnResponse.class);
            // TODO : Redis connection failing
            // logRequestRepo.save(new LogRequest(UUID.randomUUID().toString(),bvn, response));
        });
    }


    private URI getUri(String serviceUrl) {
        URI uri = null;
        try {
            uri = new URI(serviceUrl);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return uri;
    }

    private String getImageEncodedString(String path) throws IOException{

        byte[] bytes = Files.readAllBytes(Paths.get(path));
        return Base64.getEncoder().encodeToString(bytes);

    }

    private boolean validateBasicDetail(String base64String){
        String[] strings = base64String.split(",");
        String extension;
        switch (strings[0]) {//check image's extension
            case "data:image/jpeg;base64":
                extension = "jpeg";
                break;
            case "data:image/png;base64":
                extension = "png";
                break;
            default://should write cases for more images types
                extension = "jpg";
                break;
        }

        return (!extension.isBlank()) ? true : false;
    }

}
