package com.mutualfund;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class MutualFundApplication {

    public static void main(String[] args) {
        SpringApplication.run(MutualFundApplication.class, args);
    }
}
