package com.sinqia.sqspii.response;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
public class ErrorResponse extends DefaultResponse {

    private List<SinqiaError> body;

}
