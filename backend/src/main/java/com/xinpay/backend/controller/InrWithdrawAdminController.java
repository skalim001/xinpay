package com.xinpay.backend.controller;

import com.xinpay.backend.model.InrWithdrawRequest;
import com.xinpay.backend.service.InrWithdrawService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/admin/inr-withdraw")
@CrossOrigin(origins = "*")
public class InrWithdrawAdminController {

    @Autowired
    private InrWithdrawService withdrawService;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    // Get all pending INR withdraw requests
    @GetMapping("/pending")
    public ResponseEntity<List<Map<String, Object>>> getPendingWithdrawals() {
        List<InrWithdrawRequest> pendingList = withdrawService.getPendingWithdrawals();
        List<Map<String, Object>> response = new ArrayList<>();

        for (InrWithdrawRequest req : pendingList) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("id", req.getId());
            entry.put("userId", req.getUserId());
            entry.put("amount", req.getAmount());
            entry.put("accountNumber", req.getAccountNumber());
            entry.put("ifscCode", req.getIfscCode());
            entry.put("requestedAt", req.getRequestedAt() != null ? formatter.format(req.getRequestedAt()) : "-");
            entry.put("approved", req.isApproved());
            entry.put("rejected", req.isRejected());
            response.add(entry);
        }

        return ResponseEntity.ok(response);
    }

    // Approve a withdrawal
    @PutMapping("/{id}/approve")
    public ResponseEntity<String> approveWithdraw(@PathVariable Long id) {
        boolean success = withdrawService.approveWithdrawal(id);
        return success
                ? ResponseEntity.ok("✅ Withdrawal approved.")
                : ResponseEntity.status(404).body("❌ Request not found or balance insufficient.");
    }

    // Reject a withdrawal
    @PutMapping("/{id}/reject")
    public ResponseEntity<String> rejectWithdraw(@PathVariable Long id) {
        boolean success = withdrawService.rejectWithdrawal(id);
        return success
                ? ResponseEntity.ok("❌ Withdrawal rejected.")
                : ResponseEntity.status(404).body("❌ Request not found or already processed.");
    }

    // Optional: Get full history for a user (if needed)
    @GetMapping("/all/{userId}")
    public ResponseEntity<List<Map<String, Object>>> getAllByUser(@PathVariable String userId) {
        List<InrWithdrawRequest> history = withdrawService.getAllWithdrawalsByUser(userId);
        List<Map<String, Object>> response = new ArrayList<>();

        for (InrWithdrawRequest entry : history) {
            Map<String, Object> row = new HashMap<>();
            row.put("id", entry.getId());
            row.put("userId", entry.getUserId());
            row.put("amount", entry.getAmount());
            row.put("accountNumber", entry.getAccountNumber());
            row.put("ifscCode", entry.getIfscCode());
            row.put("requestedAt", entry.getRequestedAt() != null ? formatter.format(entry.getRequestedAt()) : "-");
            row.put("approved", entry.isApproved());
            row.put("rejected", entry.isRejected());
            response.add(row);
        }

        return ResponseEntity.ok(response);
    }
}
