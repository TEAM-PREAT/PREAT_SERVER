package com.andes.preat.exception.invalidToken;

import com.andes.preat.exception.CustomException;

import static com.andes.preat.exception.ErrorStatusCode.TOKEN_NOT_REQUIRED;

public class NotRequiredToken extends CustomException {
    public NotRequiredToken() {
        super(TOKEN_NOT_REQUIRED);
    }
}
