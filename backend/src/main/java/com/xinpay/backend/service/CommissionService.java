/*package com.xinpay.backend.service;

import com.xinpay.backend.model.Commission;
import com.xinpay.backend.repository.CommissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommissionService {

    @Autowired
    private CommissionRepository commissionRepository;

    // Save a new commission
    public Commission saveCommission(Commission commission) {
        return commissionRepository.save(commission);
    }

    // Get all commissions for a specific user
    public List<Commission> getCommissionsByUserId(String userId) {
        return commissionRepository.findByUserId(userId);
    }

    // Delete commission by ID
    public void deleteCommission(Long id) {
        commissionRepository.deleteById(id);
    }
}*/


package com.xinpay.backend.service;

import com.xinpay.backend.model.Commission;
import com.xinpay.backend.repository.CommissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CommissionService {

    @Autowired
    private CommissionRepository commissionRepository;

    @Autowired
    private BalanceService balanceService;

    // ✅ Save commission & add amount to USDT balance
    public Commission saveCommission(Commission commission) {
        Commission saved = commissionRepository.save(commission);
        balanceService.addUsdt(commission.getUserId(), commission.getAmount()); // 👈 Add to USDT balance
        return saved;
    }

    // ✅ Get all commissions for a specific user
    public List<Commission> getCommissionsByUserId(String userId) {
        return commissionRepository.findByUserId(userId);
    }

    // ✅ Delete commission by ID
    public void deleteCommission(Long id) {
        commissionRepository.deleteById(id);
    }
}

