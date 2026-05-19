package com.example.game_match.global.config;

import java.nio.file.Path;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final String[] allowedOriginPatterns;
    private final Path uploadDir;

    public WebConfig(
            @Value("${custom.allowed.origins}") String[] allowedOriginPatterns,
            @Value("${file.upload-dir}") String uploadDir
    ) {
        this.allowedOriginPatterns = allowedOriginPatterns;
        this.uploadDir = Path.of(uploadDir);
    }

    // 프론트엔드에서 백엔드 API를 호출할 수 있도록 CORS 정책을 등록한다.
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns(allowedOriginPatterns)
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    // 업로드된 이미지 파일을 /images/** 경로로 접근할 수 있게 정적 리소스 핸들러를 등록한다.
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**")
                .addResourceLocations(uploadDir.toUri().toString());
    }
}
