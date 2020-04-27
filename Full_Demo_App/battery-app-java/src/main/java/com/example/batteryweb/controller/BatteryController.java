package com.example.batteryweb.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@Slf4j
public class BatteryController {
    @RequestMapping("/")
    public String chat(HttpServletRequest request, Map<String, String> data) {
        return "battery";
    }
}
