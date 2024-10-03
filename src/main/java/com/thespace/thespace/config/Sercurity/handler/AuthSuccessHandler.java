package com.thespace.thespace.config.Sercurity.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
@Configuration
public class AuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler
  {
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
      {
        String targetUrl = "http://localhost:5173/space";
        try
          {
            response.setStatus(HttpServletResponse.SC_OK);
            PrintWriter writer = response.getWriter();
            writer.write("{\"redirectUrl\":\"" + targetUrl + "\"}");
            writer.flush();
            writer.close();
          }catch(IOException e)
          {
            log.error(e.getMessage(), e);
          }
      }
  }
