package com.hyd.pipes_bakery_backend.config;

import java.time.Duration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
@EnableConfigurationProperties(ImageProperties.class)
public class StaticResourceConfig implements WebMvcConfigurer {

    private static final String CUSTOM_CAKES_DIRECTORY = "custom-cakes";

    private final ImageProperties imageProperties;

    public StaticResourceConfig(ImageProperties imageProperties) {
        this.imageProperties = imageProperties;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // Uploaded product images get a unique name, so browsers and the CDN can keep them:
        // without an explicit policy Spring Security marks every response as no-store
        registry.addResourceHandler(imageProperties.getUrlPattern())
                .addResourceLocations("file:" + imageProperties.getPath() + "/")
                .setCacheControl(CacheControl.maxAge(Duration.ofDays(365)).cachePublic().immutable());

        // Customer photos for the edible print: cached by the customer's browser only, never by the CDN
        registry.addResourceHandler(imageProperties.getUrlPattern().replace("**", CUSTOM_CAKES_DIRECTORY + "/**"))
                .addResourceLocations("file:" + imageProperties.getPath() + "/" + CUSTOM_CAKES_DIRECTORY + "/")
                .setCacheControl(CacheControl.maxAge(Duration.ofDays(1)).cachePrivate());
    }
}
