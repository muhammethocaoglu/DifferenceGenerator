package com.waes.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * BinaryDataDto presentation BinaryData to be used in REST endpoints. It only has
 * a base64 encoded binary data in String format as a field.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BinaryDataDto
{
    private String content;
}
