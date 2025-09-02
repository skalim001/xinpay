package com.xinpay.backend.controller;

import com.xinpay.backend.model.UsdtWithdrawRequest;
import com.xinpay.backend.service.UsdtWithdrawService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/admin/usdt-withdraw")
@CrossOrigin(origins = "*")
public class UsdtWithdrawAdminController {

    @Autowired
    private UsdtWithdrawService withdrawService;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    // Get all pending USDT withdraw requests
    @GetMapping("/pending")
    public ResponseEntity<List<Map<String, Object>>> getPendingWithdrawals() {
        List<UsdtWithdrawRequest> pendingList = withdrawService.getPendingWithdrawals();
        List<Map<String, Object>> response = new ArrayList<>();

        for (UsdtWithdrawRequest req : pendingList) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("id", req.getId());
            entry.put("userId", req.getUserId());
            entry.put("amount", req.getAmount());
            entry.put("walletAddress", req.getWalletAddress());
            entry.put("requestedAt", req.getRequestedAt() != null ? formatter.format(req.getRequestedAt()) : "-");
            entry.put("approved", req.isApproved());
            entry.put("rejected", req.isRejected());
            response.add(entry);
        }

        return ResponseEntity.ok(response);
    }

    // Approve a USDT withdrawal
    @PutMapping("/{id}/approve")
    public ResponseEntity<String> approveWithdraw(@PathVariable Long id) {
        boolean success = withdrawService.approveWithdrawal(id);
        return success
                ? ResponseEntity.ok("✅ Withdrawal approved.")
                : ResponseEntity.status(404).body("❌ Request not found or insufficient balance.");
    }

    // Reject a USDT withdrawal
    @PutMapping("/{id}/reject")
    public ResponseEntity<String> rejectWithdraw(@PathVariable Long id) {
        boolean success = withdrawService.rejectWithdrawal(id);
        return success
                ? ResponseEntity.ok("❌ Withdrawal rejected.")
                : ResponseEntity.status(404).body("❌ Request not found or already processed.");
    }

    // Optional: Get full history for a user
    @GetMapping("/all/{userId}")
    public ResponseEntity<List<Map<String, Object>>> getAllByUser(@PathVariable String userId) {
        List<UsdtWithdrawRequest> history = withdrawService.getAllWithdrawalsByUser(userId);
        List<Map<String, Object>> response = new ArrayList<>();

        for (UsdtWithdrawRequest entry : history) {
            Map<String, Object> row = new HashMap<>();
            row.put("id", entry.getId());
            row.put("userId", entry.getUserId());
            row.put("amount", entry.getAmount());
            row.put("walletAddress", entry.getWalletAddress());
            row.put("requestedAt", entry.getRequestedAt() != null ? formatter.format(entry.getRequestedAt()) : "-");
            row.put("approved", entry.isApproved());
            row.put("rejected", entry.isRejected());
            response.add(row);
        }

        return ResponseEntity.ok(response);
    }
}
