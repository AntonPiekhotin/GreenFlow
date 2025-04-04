package org.greenflow.common.model.exception;

import lombok.Getter;

@Getter
public class GreenFlowException extends RuntimeException {

    int statusCode;

    public GreenFlowException(int statusCode, String msg) {
        super(msg);
        this.statusCode = statusCode;
    }

}
