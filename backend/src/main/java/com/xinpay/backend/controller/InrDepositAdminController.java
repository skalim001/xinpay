package com.xinpay.backend.controller;

import com.xinpay.backend.model.InrDepositRequest;
import com.xinpay.backend.service.InrDepositService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/admin/inr-deposits")
@CrossOrigin(origins = "*") // allow admin panel access from any origin
public class InrDepositAdminController {

    @Autowired
    private InrDepositService inrDepositService;

    private final String BASE_URL = "https://xinpay-wtfu.onrender.com";

    // ✅ Fetch all pending deposits for admin panel
    @GetMapping("/pending")
    public ResponseEntity<List<Map<String, Object>>> getPendingDeposits() {
        List<InrDepositRequest> pending = inrDepositService.getPendingDeposits();
        List<Map<String, Object>> result = new ArrayList<>();
        for (InrDepositRequest deposit : pending) {
            Map<String, Object> row = new HashMap<>();
            row.put("id", deposit.getId());
            row.put("userId", deposit.getUserId());
            row.put("amount", deposit.getAmount());
            row.put("status", deposit.isVerified() ? "Verified" : deposit.isRejected() ? "Rejected" : "Pending");
            row.put("type", deposit.getAmount() < 0 ? "Withdrawal" : "Deposit");
            row.put("screenshotUrl", BASE_URL + "/uploads/" + deposit.getImageUrl());
            result.add(row);
        }
        return ResponseEntity.ok(result);
    }

    // ✅ Verify a deposit
    @PutMapping("/{id}/verify")
    public ResponseEntity<?> verify(@PathVariable Long id) {
        boolean status = inrDepositService.verifyDeposit(id);
        return status ? ResponseEntity.ok("Verified") : ResponseEntity.status(404).body("Not found");
    }

    // ✅ Reject a deposit
    @PutMapping("/{id}/reject")
    public ResponseEntity<?> reject(@PathVariable Long id) {
        boolean status = inrDepositService.rejectDeposit(id);
        return status ? ResponseEntity.ok("Rejected") : ResponseEntity.status(404).body("Not found");
    }

    // ✅ Fetch all deposits for a specific user
    @GetMapping("/all/{userId}")
    public ResponseEntity<List<Map<String, Object>>> getAll(@PathVariable String userId) {
        List<InrDepositRequest> all = inrDepositService.getAllDepositsByUser(userId);
        List<Map<String, Object>> response = new ArrayList<>();
        for (InrDepositRequest deposit : all) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("id", deposit.getId());
            entry.put("userId", deposit.getUserId());
            entry.put("amount", deposit.getAmount());
            entry.put("verified", deposit.isVerified());
            entry.put("rejected", deposit.isRejected());
            entry.put("type", deposit.getAmount() < 0 ? "Withdrawal" : "Deposit");
            if (deposit.getVerifiedAt() != null) {
                String formatted = deposit.getVerifiedAt().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                entry.put("verifiedAt", formatted);
            }
            response.add(entry);
        }
        return ResponseEntity.ok(response);
    }
}
