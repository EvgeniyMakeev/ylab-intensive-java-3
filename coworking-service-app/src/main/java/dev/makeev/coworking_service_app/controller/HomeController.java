package dev.makeev.coworking_service_app.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Hidden
public class HomeController {
    @RequestMapping("/")
    String index() {
        return "redirect:/swagger-ui/index.html";
    }
}
