package com.lkkp.runwith;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class RunwithApplication {

    public static void main(String[] args) {
        SpringApplication.run(RunwithApplication.class, args);
    }

}
