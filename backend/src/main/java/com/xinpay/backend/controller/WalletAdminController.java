package com.xinpay.backend.controller;

import com.xinpay.backend.service.UsdtWalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/wallet")
@CrossOrigin(origins = "*")
public class WalletAdminController {

    @Autowired
    private UsdtWalletService walletService;

    // ✅ Fetch the current TRC20 address for admin panel display
    @GetMapping("/usdt-address")
    public ResponseEntity<String> getUsdtAddress() {
        String address = walletService.getLatestTrc20Address();
        return (address != null) ? ResponseEntity.ok(address) : ResponseEntity.status(404).body("TRC20 address not found");
    }

    // ✅ Update the TRC20 address directly from admin panel
    @PutMapping("/usdt-address")
    public ResponseEntity<String> updateUsdtAddress(@RequestBody String newAddress) {
        String msg = walletService.updateTrc20Address(newAddress);
        return ResponseEntity.ok(msg);
    }
}
