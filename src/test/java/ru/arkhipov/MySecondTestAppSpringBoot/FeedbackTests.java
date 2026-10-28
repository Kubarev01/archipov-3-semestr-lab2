package ru.arkhipov.MySecondTestAppSpringBoot;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import ru.arkhipov.MySecondTestAppSpringBoot.controller.*;
import ru.arkhipov.MySecondTestAppSpringBoot.service.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.containsString;

class FeedbackTests {
    static final ObjectMapper JSON = new ObjectMapper();
    MockMvc mvc;
    LocalValidatorFactoryBean validator;

    @BeforeEach void setup() {
        validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mvc = MockMvcBuilders.standaloneSetup(new MyController(new ValidationServiceImpl()))
                .setControllerAdvice(new JsonExceptionHandler()).setValidator(validator)
                .setMessageConverters(new org.springframework.http.converter.json.MappingJackson2HttpMessageConverter(
                    new ObjectMapper().disable(com.fasterxml.jackson.databind.DeserializationFeature.ACCEPT_FLOAT_AS_INT))).build();
    }
    @AfterEach void close() { validator.close(); }

    static Stream<JsonNode> cases() throws Exception {
        JsonNode cases = JSON.readTree(FeedbackTests.class.getResourceAsStream("/cases.json"));
        return StreamSupport.stream(cases.spliterator(), false);
    }

    @ParameterizedTest(name = "{index}: {0}")
    @MethodSource("cases")
    void checksEveryValidationCase(JsonNode test) throws Exception {
        JsonNode body = test.get("body");
        String raw = body.isTextual() ? body.asText() : JSON.writeValueAsString(body);
        var result = mvc.perform(post("/feedback").contentType(MediaType.APPLICATION_JSON).content(raw))
                .andExpect(status().is(test.get("status").asInt()))
                .andExpect(jsonPath("$.code").value(test.get("status").asInt() == 200 ? "success" : "failed"))
                .andExpect(jsonPath("$.errorCode").value(test.get("error").asText()))
                .andExpect(jsonPath("$.systemTime").isNotEmpty());
        for (JsonNode fragment : test.get("contains")) {
            result.andExpect(jsonPath("$.errorMessage").value(containsString(fragment.asText())));
        }
    }

    @Test void unexpectedExceptionReturnsSafe500() throws Exception {
        ValidationService broken = result -> { throw new IllegalStateException("private diagnostic"); };
        MockMvc failingMvc = MockMvcBuilders.standaloneSetup(new MyController(broken)).build();
        failingMvc.perform(post("/feedback").contentType(MediaType.APPLICATION_JSON)
                .content("{\"uid\":\"1\",\"operationUid\":\"op\",\"systemTime\":\"now\",\"communicationId\":1}"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.errorCode").value("UnknownException"))
                .andExpect(jsonPath("$.errorMessage").value("Произошла непредвиденная ошибка"));
    }
}
