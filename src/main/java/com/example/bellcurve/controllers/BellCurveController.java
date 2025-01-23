package com.example.bellcurve.controllers;

import com.example.bellcurve.services.BellCurveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/bell-curve")
public class BellCurveController {

    @Autowired
    private BellCurveService bellCurveService;

    // API to get actual percentage
    @GetMapping("/actual")
    public ResponseEntity<Map<String, Double>> getActualPercentage() {
        return ResponseEntity.ok(bellCurveService.calculateActualPercentage());
    }

    // API to get deviation
    @GetMapping("/deviation")
    public ResponseEntity<Map<String, Double>> getDeviation() {
        return ResponseEntity.ok(bellCurveService.calculateDeviation());
    }

    // API to get employee suggestions
    @GetMapping("/suggestions")
    public ResponseEntity<List<BellCurveService.EmployeeSuggestion>> getSuggestions() {
        return ResponseEntity.ok(bellCurveService.getSuggestions());
    }
}
