package com.thespace.thespace.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.GsonHttpMessageConverter;

import java.time.LocalDateTime;


@Configuration
public class GeneralConfig
  {
    @Bean
    public ModelMapper getMapper()
      {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
            .setSkipNullEnabled(true)
            .setFieldMatchingEnabled(true)
            .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
            .setMatchingStrategy(MatchingStrategies.LOOSE);

        return modelMapper;
      }

    @Bean
    public Gson gson() {

      return new GsonBuilder()
          .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
          .disableHtmlEscaping()
          .setLenient()
          .create();
    }

    @Bean
    public GsonHttpMessageConverter gsonHttpMessageConverter()
      {
      GsonHttpMessageConverter gsonConverter = new GsonHttpMessageConverter();
      gsonConverter.setGson(gson());
      return gsonConverter;
    }
  }
