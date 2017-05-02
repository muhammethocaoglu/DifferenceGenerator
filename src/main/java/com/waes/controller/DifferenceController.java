package com.waes.controller;

import com.waes.app.exceptions.EmptyParameterException;
import com.waes.domain.BinaryData;
import com.waes.domain.BinaryDataDto;
import com.waes.service.BinaryDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import static com.waes.app.ExceptionConstants.CONTENT_EMPTY;
import static com.waes.app.ExceptionConstants.ID_EMPTY;
import static com.waes.util.Constants.LEFT;
import static com.waes.util.Constants.RIGHT;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Controller class for saving base64 encoded binary data in String format to repository and
 * calculating difference between strings saved using left / right diff endpoints.
 */
@Controller
@Slf4j
public class DifferenceController
{

    private BinaryDataService binaryDataService;

    @Autowired
    public DifferenceController(BinaryDataService binaryDataService)
    {
        this.binaryDataService = binaryDataService;
    }

    @RequestMapping(method = POST, value = "/v1/diff/{id}/left")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public BinaryData leftDiff(@PathVariable("id") final String id,
            @RequestBody final BinaryDataDto binaryDataDto)
    {
        validateId(id);
        validateBinaryDataContent(binaryDataDto.getContent());
        return binaryDataService.saveBinaryData(id, LEFT, binaryDataDto);
    }

    @RequestMapping(method = POST, value = "/v1/diff/{id}/right")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public BinaryData rightDiff(@PathVariable("id") final String id,
            @RequestBody final BinaryDataDto binaryDataDto)
    {
        validateId(id);
        validateBinaryDataContent(binaryDataDto.getContent());
        return binaryDataService.saveBinaryData(id, RIGHT, binaryDataDto);
    }

    @RequestMapping(method = GET, value = "/v1/diff/{id}", produces = APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public String difference(@PathVariable("id") final String id)
    {
        validateId(id);
        return binaryDataService.calculateBinaryDataDifference(id);
    }


    private void validateId(String id)
    {
        if(!StringUtils.hasText(id))
        {
            log.error(ID_EMPTY);
            throw new EmptyParameterException(ID_EMPTY);
        }

    }

    private void validateBinaryDataContent(String content)
    {
        if (!StringUtils.hasText(content))
        {
            log.error(CONTENT_EMPTY);
            throw new EmptyParameterException(CONTENT_EMPTY);
        }
    }
}
