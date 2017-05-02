package com.waes.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.waes.TestUtil;
import com.waes.app.exceptions.BinaryDataNotFoundInRepoException;
import com.waes.app.exceptions.BinaryDataNotSavedException;
import com.waes.domain.BinaryData;
import com.waes.domain.BinaryDataDto;
import com.waes.repository.BinaryDataRepository;
import org.apache.tomcat.util.codec.binary.Base64;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.waes.TestConstants.TEST_CONTENT;
import static com.waes.TestConstants.TEST_CONTENT_LONG;
import static com.waes.TestConstants.TEST_ID;
import static com.waes.TestConstants.TEST_LEFT;
import static com.waes.TestConstants.TEST_RIGHT;
import static com.waes.util.Constants.DIFF_ARRAY;
import static com.waes.util.Constants.EQUALITY;
import static com.waes.util.Constants.LEFT;
import static com.waes.util.Constants.OFFSET;
import static com.waes.util.Constants.RIGHT;
import static com.waes.util.Constants.SIZE_EQUALITY;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Unit tests for {@link BinaryDataService}.
 */
@Test
public class BinaryDataServiceTest
{

    @Mock
    private BinaryDataRepository binaryDataRepository;

    @InjectMocks
    private BinaryDataService underTest;

    @Captor
    private ArgumentCaptor<BinaryData> binaryDataArgumentCaptor;

    @BeforeClass
    public void init()
    {
        initMocks(this);
    }

    @Test
    public void shouldSaveBinaryData()
    {
        BinaryDataDto binaryDataDto = TestUtil.createBinaryDataDto(TEST_LEFT);
        underTest.saveBinaryData(TEST_ID, RIGHT, binaryDataDto);
        verify(binaryDataRepository).save(binaryDataArgumentCaptor.capture());
        assertEquals(binaryDataArgumentCaptor.getValue().getContent(), binaryDataDto.getContent());
        assertEquals(binaryDataArgumentCaptor.getValue().getId(), TEST_ID);
        assertEquals(binaryDataArgumentCaptor.getValue().getSide(), RIGHT);
    }

    @Test(expectedExceptions = BinaryDataNotSavedException.class)
    public void shouldThrowBinaryDataNotSavedExceptionWhenBinaryDataCannotBeSavedToBinaryDataRepository()
    {
        BinaryDataDto binaryDataDto = TestUtil.createBinaryDataDto(TEST_CONTENT);
        BinaryData binaryData = new BinaryData(TEST_ID, LEFT, binaryDataDto.getContent());
        when(binaryDataRepository.save(binaryData)).thenThrow(new BinaryDataNotSavedException(""));
        underTest.saveBinaryData(TEST_ID, LEFT, binaryDataDto);

    }

    @Test
    public void shouldCalculateBinaryDataDifferenceWithEqualContent()
    {
        String differenceJson = initializeRepositoryMocksAndCalculateBinaryDataDifference(TEST_CONTENT, TEST_CONTENT);

        JsonParser jsonParser = new JsonParser();
        JsonObject differenceJsonObject = jsonParser.parse(differenceJson).getAsJsonObject();

        assertEquals(differenceJsonObject.get(EQUALITY).getAsBoolean(), true);
    }

    @Test
    public void shouldCalculateBinaryDataDifferenceWithInequalSizedContent()
    {
        String differenceJson = initializeRepositoryMocksAndCalculateBinaryDataDifference(TEST_CONTENT, TEST_CONTENT_LONG);

        JsonParser jsonParser = new JsonParser();
        JsonObject differenceJsonObject = jsonParser.parse(differenceJson).getAsJsonObject();

        assertEquals(differenceJsonObject.get(SIZE_EQUALITY).getAsBoolean(), false);
    }

    @Test(expectedExceptions = BinaryDataNotFoundInRepoException.class)
    public void shouldThrowBinaryDataNotFoundInRepoExceptionWhenBinaryDataWithIdAndSideNotFoundInBinaryDataRepository()
    {
        when(binaryDataRepository.findByIdAndSide(TEST_ID, LEFT)).thenThrow(new BinaryDataNotFoundInRepoException(""));
        underTest.calculateBinaryDataDifference(TEST_ID);
    }



    private String initializeRepositoryMocksAndCalculateBinaryDataDifference(String testContentLeft, String testContentRight)
    {
        String base64EncodedTestContentLeft = new String(Base64.encodeBase64(testContentLeft.getBytes()));
        String base64EncodedTestContentRight = new String(Base64.encodeBase64(testContentRight.getBytes()));
        BinaryData binaryDataLeft = new BinaryData(TEST_ID, LEFT, base64EncodedTestContentLeft);
        BinaryData binaryDataRight = new BinaryData(TEST_ID, RIGHT, base64EncodedTestContentRight);
        when(binaryDataRepository.findByIdAndSide(TEST_ID, LEFT)).thenReturn(binaryDataLeft);
        when(binaryDataRepository.findByIdAndSide(TEST_ID, RIGHT)).thenReturn(binaryDataRight);

        return underTest.calculateBinaryDataDifference(TEST_ID);

    }

}
