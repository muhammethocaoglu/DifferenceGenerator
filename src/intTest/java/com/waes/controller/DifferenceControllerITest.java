package com.waes.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.waes.SpringTestConfig;
import com.waes.TestUtil;
import com.waes.app.exceptions.EmptyParameterException;
import com.waes.domain.BinaryData;
import com.waes.domain.BinaryDataDto;
import com.waes.repository.BinaryDataRepository;
import com.waes.service.BinaryDataService;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.waes.IntTestConstants.API_ROOT_ENDPOINT;
import static com.waes.IntTestConstants.APPLICATION_JSON_CHARSET_UTF_8_CONTENT_TYPE;
import static com.waes.IntTestConstants.LEFT_DIFF_CONTEXT_PATH;
import static com.waes.IntTestConstants.LEFT_DIFF_CONTEXT_PATH_EMPTY_ID;
import static com.waes.IntTestConstants.RIGHT_DIFF_CONTEXT_PATH;
import static com.waes.IntTestConstants.TEST_ID;
import static com.waes.IntTestConstants.TEST_LEFT;
import static com.waes.IntTestConstants.TEST_RIGHT;
import static com.waes.util.Constants.LEFT;
import static com.waes.util.Constants.LENGTH;
import static com.waes.util.Constants.OFFSET;
import static com.waes.util.Constants.RIGHT;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
import static org.testng.AssertJUnit.assertEquals;

/**
 * Created by z003hnxv on 01.05.2017.
 */
@WebAppConfiguration
@SpringApplicationConfiguration(SpringTestConfig.class)
@TestPropertySource("classpath:application-db.properties")
public class DifferenceControllerITest extends AbstractTestNGSpringContextTests
{
    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    private BinaryDataRepository binaryDataRepository;

    @Autowired
    private BinaryDataService binaryDataService;

    @BeforeMethod
    public void setupAppContext() throws Exception
    {
        binaryDataRepository.deleteAll();
        mockMvc = webAppContextSetup(context).build();

    }

    @Test
    public void shouldCallLeftAndRightDiffAndDifferenceAndValidateDifferenceResult() throws Exception
    {
        doPostRequestToDiffEndpointsAndValidateResult(TEST_LEFT, LEFT, LEFT_DIFF_CONTEXT_PATH);
        doPostRequestToDiffEndpointsAndValidateResult(TEST_RIGHT, RIGHT, RIGHT_DIFF_CONTEXT_PATH);
        doGetRequestToDifferenceEndpointAndValidateResult();

    }

    @Test
    public void shouldThrowEmptyParameterExceptionWhenLeftDiffCalledWithEmptyId() throws Exception
    {
        BinaryDataDto binaryDataDto = TestUtil.createBinaryDataDto(TEST_LEFT);
        final ObjectMapper objectMapper = new ObjectMapper();
        final MockHttpServletRequestBuilder uploadBinaryDataBuilder = MockMvcRequestBuilders.post(API_ROOT_ENDPOINT + LEFT_DIFF_CONTEXT_PATH_EMPTY_ID)
                .contentType(
                        MediaType.APPLICATION_JSON_UTF8).content(objectMapper.writeValueAsString(binaryDataDto));
        getMockMvc().perform(uploadBinaryDataBuilder).andExpect(status().isBadRequest());
    }

    private void doPostRequestToDiffEndpointsAndValidateResult(String testContent, String side, String contextPath) throws Exception
    {
        BinaryDataDto binaryDataDto = doPostRequestToDiffEndpoints(testContent, contextPath);
        final BinaryData binaryData = binaryDataRepository.findByIdAndSide(TEST_ID, side);
        assertEquals(binaryData.getContent(), binaryDataDto.getContent());
        assertEquals(binaryData.getId(), TEST_ID);
        assertEquals(binaryData.getSide(), side);
    }

    private BinaryDataDto doPostRequestToDiffEndpoints(String testContent, String contextPath) throws Exception
    {
        BinaryDataDto binaryDataDto = TestUtil.createBinaryDataDto(testContent);
        final ObjectMapper objectMapper = new ObjectMapper();
        final MockHttpServletRequestBuilder uploadBinaryDataBuilder = MockMvcRequestBuilders.post(API_ROOT_ENDPOINT + contextPath).contentType(
                MediaType.APPLICATION_JSON_UTF8).content(objectMapper.writeValueAsString(binaryDataDto));
        getMockMvc().perform(uploadBinaryDataBuilder).andExpect(status().isCreated())
                .andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8_CONTENT_TYPE));
        return binaryDataDto;
    }

    private void validateResult(BinaryDataDto binaryDataDto, String side)
    {
        final BinaryData binaryData = binaryDataRepository.findByIdAndSide(TEST_ID, side);
        assertEquals(binaryData.getContent(), binaryDataDto.getContent());
        assertEquals(binaryData.getId(), TEST_ID);
        assertEquals(binaryData.getSide(), side);
    }

    private void doGetRequestToDifferenceEndpointAndValidateResult() throws Exception
    {
        String leftContent = new String(Base64.encodeBase64(TEST_LEFT.getBytes()));
        String rightContent = new String(Base64.encodeBase64(TEST_RIGHT.getBytes()));
        JsonArray diffArray = binaryDataService.calculateDifferenceOffsetAndLengthValues(leftContent, rightContent);

        final MockHttpServletRequestBuilder getBinaryDataDifferenceBuilder = MockMvcRequestBuilders.get(API_ROOT_ENDPOINT + TEST_ID);
        getMockMvc().perform(getBinaryDataDifferenceBuilder).andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8_CONTENT_TYPE))
                .andExpect(jsonPath("$.diffArray[0].offset", equalTo(getValueAtIndexFromDiffArray(diffArray, OFFSET, 0))))
                .andExpect(jsonPath("$.diffArray[0].length", equalTo(getValueAtIndexFromDiffArray(diffArray, LENGTH, 0))))
                .andExpect(jsonPath("$.diffArray[1].offset", equalTo(getValueAtIndexFromDiffArray(diffArray, OFFSET, 1))))
                .andExpect(jsonPath("$.diffArray[1].length", equalTo(getValueAtIndexFromDiffArray(diffArray, LENGTH, 1))));
    }

    private int getValueAtIndexFromDiffArray(JsonArray diffArray, String key, int index)
    {
        return diffArray.get(index).getAsJsonObject().get(key).getAsInt();
    }

    private MockMvc getMockMvc()
    {
        return mockMvc;
    }

}
