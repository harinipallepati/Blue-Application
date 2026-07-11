package com.example.blue.controller;


import com.example.blue.DTO.ComparisonRequest;
import com.example.blue.DTO.ComparisonResponse;
import com.example.blue.DTO.RecommendationRequest;
import com.example.blue.DTO.SmartSearchResponse;
import com.example.blue.service.GeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
public class AIController {
    @Autowired
    private GeminiService geminiService;
    @PostMapping("/smartsearch")
    public SmartSearchResponse smartSearch(@RequestBody RecommendationRequest request) {
        return geminiService.smartSearch(request.getQuery());
    }
    @PostMapping("/compareProducts")
    public ComparisonResponse compareProducts(@RequestBody ComparisonRequest request) {
        return geminiService.compareProducts(request.getId1(), request.getId2());
    }
}
