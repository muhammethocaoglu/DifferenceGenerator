package com.waes.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.waes.app.exceptions.BinaryDataNotFoundInRepoException;
import com.waes.app.exceptions.BinaryDataNotSavedException;
import com.waes.domain.BinaryData;
import com.waes.domain.BinaryDataDto;
import com.waes.repository.BinaryDataRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

import static com.waes.util.Constants.DIFF_ARRAY;
import static com.waes.util.Constants.EQUALITY;
import static com.waes.util.Constants.LEFT;
import static com.waes.util.Constants.LENGTH;
import static com.waes.util.Constants.OFFSET;
import static com.waes.util.Constants.RIGHT;
import static com.waes.util.Constants.SIZE_EQUALITY;

/**
 * BinaryDataService is to save binary data to BinaryDataRepository and calculate difference between content values in BinaryData objects saved to repository.
 */
@Service
@Slf4j
public class BinaryDataService
{
    private BinaryDataRepository binaryDataRepository;

    @Autowired
    public BinaryDataService(BinaryDataRepository binaryDataRepository)
    {
        this.binaryDataRepository = binaryDataRepository;
    }

    public BinaryData saveBinaryData(String id, String side, final BinaryDataDto binaryDataDto)
    {
        BinaryData binaryData = new BinaryData(id, side, binaryDataDto.getContent());
        BinaryData savedBinaryData;
        try
        {
            savedBinaryData = binaryDataRepository.save(binaryData);
        }
        catch (DataAccessException ex)
        {
            StringBuilder message = new StringBuilder("Failed to save binary data:");
            message.append(" id: ").append(id);
            message.append(" side: ").append(side);
            message.append(" binaryDataDto: ").append(binaryDataDto);

            log.error(message.toString());

            throw new BinaryDataNotSavedException(message.toString());
        }

        return savedBinaryData;
    }

    @Transactional
    public String calculateBinaryDataDifference(String id)
    {
        BinaryData binaryDataLeft = Optional.ofNullable(binaryDataRepository.findByIdAndSide(id, LEFT))
                .orElseThrow(() -> new BinaryDataNotFoundInRepoException(
                        "Binary data with given id " + id + " and side " + LEFT + " cannot be found in repository"));
        BinaryData binaryDataRight = Optional.ofNullable(binaryDataRepository.findByIdAndSide(id, RIGHT))
                .orElseThrow(() -> new BinaryDataNotFoundInRepoException(
                        "Binary data with given id " + id + " and side " + RIGHT + " cannot be found in repository"));
        String leftContent = binaryDataLeft.getContent();
        String rightContent = binaryDataRight.getContent();

        return createDifferenceDataJsonObject(leftContent, rightContent);

    }

    private String createDifferenceDataJsonObject(String leftContent, String rightContent)
    {
        JsonObject differenceData = new JsonObject();
        if (leftContent.equals(rightContent))
        {
            differenceData.addProperty(EQUALITY, true);
            return differenceData.toString();
        }

        if (leftContent.length() != rightContent.length())
        {
            differenceData.addProperty(SIZE_EQUALITY, false);
            return differenceData.toString();
        }

        differenceData.add(DIFF_ARRAY, calculateDifferenceOffsetAndLengthValues(leftContent, rightContent));

        return differenceData.toString();
    }

    public JsonArray calculateDifferenceOffsetAndLengthValues(String leftContent, String rightContent)
    {
        JsonArray diffArray = new JsonArray();

        int i = 0;
        int diffOffset;
        int diffLength;
        while (i < leftContent.length())
        {
            if (leftContent.charAt(i) != rightContent.charAt(i))
            {
                diffOffset = i;
                diffLength = 1;
                i++;
                while (i < leftContent.length() && leftContent.charAt(i) != rightContent.charAt(i))
                {
                    diffLength++;
                    i++;
                }
                JsonObject diffObject = new JsonObject();
                diffObject.addProperty(OFFSET, diffOffset);
                diffObject.addProperty(LENGTH, diffLength);
                diffArray.add(diffObject);

            }
            i++;
        }

        return diffArray;

    }

}
