package com.example.batteryweb;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AppConfigTests {
    @Autowired
    private AppConfig config;

    @Test
    void testAppConfig() {
        System.out.println("enos.apim.addr = "+ config.addr);
        System.out.println("enos.app.accessKeyr = "+ config.accessKey);
        System.out.println("enos.app.secretr = "+ config.accessSecret);
    }
}
