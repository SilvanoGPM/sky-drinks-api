package com.github.skyg0d.skydrinksapi.filter;

import com.github.skyg0d.skydrinksapi.exception.details.ExceptionDetails;
import com.github.skyg0d.skydrinksapi.util.ExceptionUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Log4j2
public class ExceptionHandlerFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

            response.setStatus(status.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(ExceptionUtils.convertObjectToJson(ExceptionDetails.createExceptionDetails(ex, status)));
        }
    }

}
