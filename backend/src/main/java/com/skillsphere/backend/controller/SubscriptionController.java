// SubscriptionController.java
package com.skillsphere.backend.controller;

import com.skillsphere.backend.model.Subscription;
import com.skillsphere.backend.model.SubscriptionPlan;
import com.skillsphere.backend.service.SubscriptionService;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private SecretKey secretKey;

    @PostMapping
    public ResponseEntity<Subscription> createSubscription(
            @RequestBody Map<String, String> request,
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Long userId = Long.parseLong(Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject());
        String plan = request.get("plan");
        Subscription subscription = subscriptionService.createSubscription(userId, plan);
        return ResponseEntity.ok(subscription);
    }

    @GetMapping("/user")
    public ResponseEntity<List<Subscription>> getUserSubscriptions(
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Long userId = Long.parseLong(Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject());
        List<Subscription> subscriptions = subscriptionService.getUserSubscriptions(userId);
        return ResponseEntity.ok(subscriptions);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Subscription> updateSubscription(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        String plan = request.get("plan");
        boolean active = Boolean.parseBoolean(request.get("active"));
        Subscription updatedSubscription = subscriptionService.updateSubscription(id, plan, active);
        return ResponseEntity.ok(updatedSubscription);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubscription(@PathVariable Long id) {
        subscriptionService.deleteSubscription(id);
        return ResponseEntity.ok().build();
    }

    // New endpoint to fetch all subscription plans (publicly accessible)
    @GetMapping("/plans")
    public ResponseEntity<List<SubscriptionPlan>> getAllSubscriptionPlans() {
        List<SubscriptionPlan> plans = subscriptionService.getAllSubscriptionPlans();
        return ResponseEntity.ok(plans);
    }
}