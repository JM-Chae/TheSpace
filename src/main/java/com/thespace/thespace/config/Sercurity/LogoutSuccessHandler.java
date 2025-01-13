package com.thespace.thespace.config.Sercurity;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

@Slf4j
@Configuration
public class LogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler
  {
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
      {
        SuccessHandler.successHandler(response, log);
      }
  }
