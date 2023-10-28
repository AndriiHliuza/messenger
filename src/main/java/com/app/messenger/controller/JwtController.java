package com.app.messenger.controller;

import com.app.messenger.controller.dto.Token;
import com.app.messenger.controller.dto.TokenValidationResponse;
import com.app.messenger.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/jwt")
@ResponseStatus(HttpStatus.OK)
public class JwtController {

    private final JwtService jwtService;
    @PostMapping("/access")
    public void getNewAccessToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws Exception {
        jwtService.getNewAccessToken(request, response);
    }

    @PostMapping("/validation")
    public TokenValidationResponse validate(@RequestBody Token token) throws Exception {
        return jwtService.validate(token);
    }
}
