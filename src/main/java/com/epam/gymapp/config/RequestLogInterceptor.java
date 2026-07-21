package com.epam.gymapp.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class RequestLogInterceptor implements HandlerInterceptor {

  private static final String START_TIME = "startTime";

  @Override
  public boolean preHandle(
      HttpServletRequest request, HttpServletResponse response, Object handler) {

    request.setAttribute(START_TIME, System.currentTimeMillis());
    return true;
  }

  @Override
  public void afterCompletion(
      HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    long start = (long) request.getAttribute(START_TIME);
    long timeMs = System.currentTimeMillis() - start;

    String uri = request.getRequestURI();
    String method = request.getMethod();
    int status = response.getStatus();
    String message = (ex != null) ? ex.getMessage() : HttpStatus.valueOf(status).getReasonPhrase();

    String logMsg = String.format("%s %s → %d %s (%d ms)", method, uri, status, message, timeMs);

    if (status >= 400) {
      log.warn(logMsg);
    } else {
      log.info(logMsg);
    }
  }
}
