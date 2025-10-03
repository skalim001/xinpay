package com.xinpay.backend.controller;

import com.xinpay.backend.model.UsdtDepositRequest;
import com.xinpay.backend.service.UsdtDepositService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/admin/usdt-deposits")
@CrossOrigin(origins = "*") // allow web admin
public class UsdtDepositAdminController {

    @Autowired
    private UsdtDepositService usdtDepositService;

    // ✅ Get all pending deposits (non-verified and non-rejected)
    @GetMapping("/pending")
    public ResponseEntity<List<Map<String, Object>>> getPendingDeposits() {
        List<UsdtDepositRequest> pending = usdtDepositService.getPendingDeposits();
        List<Map<String, Object>> result = new ArrayList<>();
        String baseUrl = "https://xinpay-wtfu.onrender.com;" // your deployed URL

        for (UsdtDepositRequest deposit : pending) {
            Map<String, Object> row = new HashMap<>();
            row.put("id", deposit.getId());
            row.put("userId", deposit.getUserId());
            row.put("status", deposit.isVerified() ? "Verified" : "Pending");
            row.put("amount", deposit.getAmount());
            row.put("screenshotUrl", baseUrl + "/uploads/" + deposit.getImageUrl());
            result.add(row);
        }

        return ResponseEntity.ok(result);
    }

    // ✅ Verify USDT deposit
    @PutMapping("/{id}/verify")
    public ResponseEntity<?> verify(@PathVariable Long id) {
        boolean status = usdtDepositService.verifyDeposit(id);
        return status ? ResponseEntity.ok("Verified") : ResponseEntity.status(404).body("Not found");
    }

    // ✅ Reject USDT deposit
    @PutMapping("/{id}/reject")
    public ResponseEntity<?> reject(@PathVariable Long id) {
        boolean status = usdtDepositService.rejectDeposit(id);
        return status ? ResponseEntity.ok("Rejected") : ResponseEntity.status(404).body("Not found or already verified");
    }
}
