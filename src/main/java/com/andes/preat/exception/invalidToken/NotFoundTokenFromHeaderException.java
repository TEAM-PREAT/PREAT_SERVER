package com.andes.preat.exception.invalidToken;

import com.andes.preat.exception.CustomException;

import static com.andes.preat.exception.ErrorStatusCode.TOKEN_HEADER_EMPTY;

public class NotFoundTokenFromHeaderException extends CustomException {
    public NotFoundTokenFromHeaderException() {
        super(TOKEN_HEADER_EMPTY);
    }
}
