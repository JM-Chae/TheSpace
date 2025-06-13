package com.thespace.config.security;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import org.slf4j.Logger;

public class SuccessHandler
  {
    static void successHandler(HttpServletResponse response, Logger log)
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
