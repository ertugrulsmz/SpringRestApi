package com.appsdeveloperblog.app.ws;

import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class WebMvcConfig implements WebMvcConfigurer {

    //there has to be another overwrite in websecurity config.
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry
                .addMapping("/**") //two asterisks means everything under that pattern
                .allowedMethods("*") //get,post...
                .allowedOrigins("*");

    }
}
