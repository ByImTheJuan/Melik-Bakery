package com.hyd.pipes_bakery_backend.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
@EnableConfigurationProperties(ImageProperties.class)
public class StaticResourceConfig implements WebMvcConfigurer {

    private final ImageProperties imageProperties;

    public StaticResourceConfig(ImageProperties imageProperties) {
        this.imageProperties = imageProperties;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler(imageProperties.getUrlPattern())
                .addResourceLocations("file:" + imageProperties.getPath() + "/");
    }
}
