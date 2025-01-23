package com.example.bellcurve.services;

import com.example.bellcurve.entities.Employee;
import com.example.bellcurve.entities.RatingDistribution;
import com.example.bellcurve.repositories.EmployeeRepository;
import com.example.bellcurve.repositories.RatingDistributionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BellCurveService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private RatingDistributionRepository ratingDistributionRepository;

    // Calculate actual percentage of employees in each category
    public Map<String, Double> calculateActualPercentage() {
        List<Employee> employees = employeeRepository.findAll();
        int total = employees.size();

        Map<String, Double> actualPercentage = employees.stream()
                .collect(Collectors.groupingBy(Employee::getCurrentCategory,
                        Collectors.collectingAndThen(Collectors.counting(), count -> (count.doubleValue() / total) * 100)));

        ratingDistributionRepository.findAll().forEach(rd ->
                actualPercentage.putIfAbsent(rd.getCategory(), 0.0)
        );

        return actualPercentage;
    }

    // Calculate deviation between actual and standard percentages
    public Map<String, Double> calculateDeviation() {
        Map<String, Double> actualPercentage = calculateActualPercentage();

        return ratingDistributionRepository.findAll().stream()
                .collect(Collectors.toMap(
                        RatingDistribution::getCategory,
                        rd -> Math.abs(actualPercentage.getOrDefault(rd.getCategory(), 0.0) - rd.getStandardPercentage())
                ));
    }

    // Suggest employees to be shifted to another category
    public List<EmployeeSuggestion> getSuggestions() {
        Map<String, Double> deviation = calculateDeviation();
        Map<String, Double> actualPercentage = calculateActualPercentage();

        List<EmployeeSuggestion> suggestions = new ArrayList<>();

        // Suggest employees based on deviation and categories
        deviation.forEach((category, dev) -> {
            if (dev > 0) {
                employeeRepository.findAll().stream()
                        .filter(emp -> emp.getCurrentCategory().equals(category))
                        .forEach(emp -> suggestions.add(new EmployeeSuggestion(emp.getName(), category, dev)));
            }
        });

        return suggestions;
    }

    public static class EmployeeSuggestion {
        private String employeeName;
        private String category;
        private double deviation;

        public EmployeeSuggestion(String employeeName, String category, double deviation) {
            this.employeeName = employeeName;
            this.category = category;
            this.deviation = deviation;
        }

        // Getters and Setters
        public String getEmployeeName() {
            return employeeName;
        }

        public void setEmployeeName(String employeeName) {
            this.employeeName = employeeName;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public double getDeviation() {
            return deviation;
        }

        public void setDeviation(double deviation) {
            this.deviation = deviation;
        }
    }
}
