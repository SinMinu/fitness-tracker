package com.fitness.tracker.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api")
public class GooglePlacesController {

    private final String GOOGLE_API_KEY = "YOUR_GOOGLE_MAPS_API_KEY";

    @GetMapping("/google-places")
    public ResponseEntity<?> getNearbyPlaces(@RequestParam double lat, @RequestParam double lng, @RequestParam String keyword) {
        String url = String.format(
                "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=%f,%f&radius=5000&keyword=%s&key=%s",
                lat, lng, keyword, GOOGLE_API_KEY
        );

        RestTemplate restTemplate = new RestTemplate();
        Object response = restTemplate.getForObject(url, Object.class);
        return ResponseEntity.ok(response);
    }
}
