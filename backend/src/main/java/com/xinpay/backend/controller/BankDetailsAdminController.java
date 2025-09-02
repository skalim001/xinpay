// File: BankDetailsAdminController.java
package com.xinpay.backend.controller;

import com.xinpay.backend.model.BankDetails;
import com.xinpay.backend.service.BankDetailsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/bank-details")
@CrossOrigin(origins = "*")
public class BankDetailsAdminController {

    private final BankDetailsService service;

    public BankDetailsAdminController(BankDetailsService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<BankDetails> getBankDetails() {
        BankDetails details = service.getBankDetails();
        return (details == null) ? ResponseEntity.noContent().build() : ResponseEntity.ok(details);
    }

    @PostMapping("/update")
    public ResponseEntity<BankDetails> updateBankDetails(@RequestBody BankDetails details) {
        if (details.getAccountNumber() == null || details.getIfscCode() == null || details.getAccountHolder() == null) {
            return ResponseEntity.badRequest().build();
        }
        BankDetails updated = service.updateBankDetails(details);
        return ResponseEntity.ok(updated);
    }

    // Optionally: Admin panel QR update
    @PostMapping("/update-with-qr")
    public ResponseEntity<BankDetails> updateBankDetailsWithQr(
            @RequestParam String accountNumber,
            @RequestParam String ifscCode,
            @RequestParam String accountHolder,
            @RequestParam String qrUrl
    ) {
        try {
            BankDetails newDetails = new BankDetails(accountNumber, ifscCode, accountHolder, qrUrl);
            BankDetails updated = service.updateBankDetails(newDetails);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
