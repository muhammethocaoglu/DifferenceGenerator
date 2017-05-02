package com.waes;

import com.waes.domain.BinaryDataDto;
import org.apache.tomcat.util.codec.binary.Base64;

/**
 * Created by z003hnxv on 02.05.2017.
 */
public final class TestUtil
{
    private TestUtil()
    {

    }

    public static BinaryDataDto createBinaryDataDto(String contentText)
    {
        BinaryDataDto binaryDataDto = new BinaryDataDto();
        binaryDataDto.setContent(new String(Base64.encodeBase64(contentText.getBytes())));
        return binaryDataDto;

    }
}
