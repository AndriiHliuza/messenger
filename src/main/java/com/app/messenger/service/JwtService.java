package com.app.messenger.service;

import com.app.messenger.controller.dto.Token;
import com.app.messenger.controller.dto.TokenValidationResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface JwtService {
    void getNewAccessToken(HttpServletRequest request, HttpServletResponse response) throws Exception;
    TokenValidationResponse validate(Token token) throws Exception;
}
