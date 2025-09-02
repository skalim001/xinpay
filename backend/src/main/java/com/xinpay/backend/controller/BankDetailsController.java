// File: BankDetailsController.java
package com.xinpay.backend.controller;

import com.xinpay.backend.model.BankDetails;
import com.xinpay.backend.service.BankDetailsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bank-details")
@CrossOrigin(origins = "*")
public class BankDetailsController {

    private final BankDetailsService service;

    public BankDetailsController(BankDetailsService service) {
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
}
