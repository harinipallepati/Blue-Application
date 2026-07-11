package com.example.blue.service;

import com.example.blue.DTO.ComparisonRequest;
import com.example.blue.DTO.ComparisonResponse;
import com.example.blue.DTO.ProductSearchResult;
import com.example.blue.DTO.SmartSearchResponse;
import com.example.blue.model.Product;
import com.example.blue.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private RestTemplate restTemplate;
    @Value("${gemini.api.key}")
    private String apiKey;
    public String askGemini(String prompt) {
        String url=
                "https://generativelanguage.googleapis.com" +
                        "/v1beta/models/gemini-3.1-flash-lite:generateContent?key="+apiKey;
        HttpHeaders headers=new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String,Object> text=new HashMap<>();
        text.put("text",prompt);
        List<Map<String,Object>> parts=new ArrayList<>();
        parts.add(text);
        Map<String,Object> content=new HashMap<>();
        content.put("parts",parts);
        List<Map<String,Object>> contents=new ArrayList<>();
        contents.add(content);
        Map<String,Object> body=new HashMap<>();
        body.put("contents",contents);
        HttpEntity<Map<String,Object>> entity=new HttpEntity<>(body,headers);
        ResponseEntity<Map> response=restTemplate.postForEntity(url,entity,Map.class);
        Map responsebody=response.getBody();
        List candidates=(List)
                responsebody.get("candidates");
        Map candidate=(Map) candidates.get(0);
        Map contentMap=(Map)
                candidate.get("content");
        List responseparts=(List) contentMap.get("parts");
        Map part=(Map)responseparts.get(0);

        return part.get("text").toString();
    }

    public String generateDescription(Product p1) {
        String prompt= "You are an e-commerce product description writer.\n\n" +

                "Product Name: " + p1.getName() + "\n\n" +

                "Generate a professional product description.\n" +

                "Rules:\n" +
                "1. Focus ONLY on product features and benefits.\n" +
                "2. Mention performance, design, quality, comfort, durability and ideal use cases whenever appropriate.\n" +
                "3. DO NOT mention price.\n" +
                "4. DO NOT mention stock availability.\n" +
                "5. DO NOT mention discounts.\n" +
                "6. Write only 1-2 concise sentences.\n" +
                "7. Return only the description.";
        String description = askGemini(prompt);

        if (description.length() > 250) {
            description = description.substring(0, 250);
        }

        return description;
    }
    public SmartSearchResponse smartSearch(String query) {
        List<Product> products=productRepository.findAll();
        StringBuilder prompt=new StringBuilder();
        prompt.append("You are an AI shopping assistant.\n");
        prompt.append("Recommend ONLY from the products listed below.\n");
        prompt.append("Return ONLY the product IDs separated by commas.\n");
        prompt.append("Do not explain.\n");
        prompt.append("Do not return product names.\n");
        prompt.append("Do not return JSON.\n\n");

        prompt.append("Available Products:\n\n");

        for (Product product : products) {

            prompt.append("ID: ").append(product.getId()).append("\n");
            prompt.append("Name: ").append(product.getName()).append("\n");
            prompt.append("Price: ").append(product.getPrice()).append("\n");
            prompt.append("Description: ").append(product.getDescription()).append("\n\n");
        }

        prompt.append("User Query:\n");
        prompt.append(query);

        String result = askGemini(prompt.toString());

        String[] ids = result.split(",");

        List<ProductSearchResult> searchResults = new ArrayList<>();

        for (String id : ids) {

            Product product = productRepository
                    .findById(Long.parseLong(id.trim()))
                    .orElseThrow();

            searchResults.add(
                    new ProductSearchResult(
                            product.getName(),
                            product.getPrice(),
                            product.getStock(),
                            product.getDescription()
                    )
            );
        }

        SmartSearchResponse response = new SmartSearchResponse();
        response.setProducts(searchResults);

        return response;
    }
    public ComparisonResponse compareProducts(Long id1,Long id2) {
        Product product1 = productRepository.findById(id1)
                .orElseThrow();

        Product product2 = productRepository.findById(id2)
                .orElseThrow();

        String prompt =
                "You are an e-commerce shopping assistant.\n\n" +

                        "Compare these two products.\n\n" +

                        "Product 1:\n" +
                        "Name: " + product1.getName() + "\n" +
                        "Price: ₹" + product1.getPrice() + "\n" +
                        "Description: " + product1.getDescription() + "\n\n" +

                        "Product 2:\n" +
                        "Name: " + product2.getName() + "\n" +
                        "Price: ₹" + product2.getPrice() + "\n" +
                        "Description: " + product2.getDescription() + "\n\n" +

                        "Rules:\n" +
                        "1. Keep the response under 6 lines.\n" +
                        "2. Mention what Product 1 is best for.\n" +
                        "3. Mention what Product 2 is best for.\n" +
                        "4. Mention the biggest difference.\n" +
                        "5. End with 'Final Recommendation: <Product Name>'.\n" +
                        "6. Return plain text only.";

        String result = askGemini(prompt);

        ComparisonResponse response = new ComparisonResponse();
        response.setComparison(result);

        return response;
    }
}

