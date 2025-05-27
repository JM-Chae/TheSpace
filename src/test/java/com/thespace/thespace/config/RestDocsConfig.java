package com.thespace.thespace.config;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;


@Configuration
@Profile("test")
public class RestDocsConfig {
    public static MockMvc restDocsConfig(WebApplicationContext context, RestDocumentationContextProvider restDocumentation) {

        return MockMvcBuilders.webAppContextSetup(context)
            .alwaysDo(print())
            .alwaysDo(write())
            .apply(documentationConfiguration(restDocumentation))
            .addFilter(new CharacterEncodingFilter("UTF-8", true))
            .apply(SecurityMockMvcConfigurers.springSecurity())
            .build();
    }

    public static RestDocumentationResultHandler write(){
        return MockMvcRestDocumentation.document(
            "{class-name}/{method-name}",
            Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
            Preprocessors.preprocessResponse(Preprocessors.prettyPrint())
        );
    }

    public static class CsrfRestDocumentationRequestBuilders {

        public static MockHttpServletRequestBuilder put(String urlTemplate, Object... uriVars) {
            return RestDocumentationRequestBuilders.put(urlTemplate, uriVars).with(csrf());
        }

        public static MockHttpServletRequestBuilder post(String urlTemplate, Object... uriVars) {
            return RestDocumentationRequestBuilders.post(urlTemplate, uriVars).with(csrf());
        }

        public static MockHttpServletRequestBuilder patch(String urlTemplate, Object... uriVars) {
            return RestDocumentationRequestBuilders.patch(urlTemplate, uriVars).with(csrf());
        }

        public static MockHttpServletRequestBuilder delete(String urlTemplate, Object... uriVars) {
            return RestDocumentationRequestBuilders.delete(urlTemplate, uriVars).with(csrf());
        }
    }
}
