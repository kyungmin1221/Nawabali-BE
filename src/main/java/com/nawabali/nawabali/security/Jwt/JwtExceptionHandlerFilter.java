package com.nawabali.nawabali.security.Jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nawabali.nawabali.exception.CustomException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j(topic = "Exception Filter")
@Order(1)
public class JwtExceptionHandlerFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            log.info("Exception Filter");
            filterChain.doFilter(request, response);
        } catch(CustomException e){
            setErrorResponse(response, e);
        }
    }

    public void setErrorResponse(HttpServletResponse response,CustomException e) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(e.getErrorCode().getHttpStatus().value());
        ErrorResponse errorResponse = new ErrorResponse(
                e.getErrorCode().getHttpStatus().value(),
                e.getErrorCode().getHttpStatus().getReasonPhrase(),
                e.getMessage()
        );
        String jsonResponse = new ObjectMapper().writeValueAsString(errorResponse);
        response.getWriter().write(jsonResponse);
    }

    @AllArgsConstructor
    @Getter
    public static class ErrorResponse {

        private int status;
        private String error;
        private String message;

    }
}
