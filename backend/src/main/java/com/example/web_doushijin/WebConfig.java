package com.example.web_doushijin;

import java.io.File;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
	    // 1. Lấy đường dẫn thư mục mà bạn đã tạo bằng tay ở project (manga_storage)
	    String projectDir = new File("manga_storage").getAbsolutePath();
	    
	    // 2. Nói với Java: Khi có yêu cầu /images/** thì thò tay vào projectDir mà lấy
	    registry.addResourceHandler("/images/**")
	            .addResourceLocations("file:" + projectDir + "/");
	}
}