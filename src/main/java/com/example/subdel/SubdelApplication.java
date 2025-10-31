package com.example.subdel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.hateoas.config.EnableHypermediaSupport;

@SpringBootApplication(
        scanBasePackages = {"com.example.subdel", "com.example.subdel_api"},
        exclude = { DataSourceAutoConfiguration.class}
)
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
public class SubdelApplication {

    public static void main(String[] args) {
        SpringApplication.run(SubdelApplication.class, args);
    }

}
