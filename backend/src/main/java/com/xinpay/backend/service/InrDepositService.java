// ✅ SERVICE
package com.xinpay.backend.service;

import com.xinpay.backend.model.Balance;
import com.xinpay.backend.model.InrDepositRequest;
import com.xinpay.backend.repository.BalanceRepository;
import com.xinpay.backend.repository.InrDepositRequestRepository;
import com.xinpay.backend.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
public class InrDepositService {

    @Autowired
    private InrDepositRequestRepository inrDepositRequestRepository;

    @Autowired
    private BalanceRepository balanceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    public InrDepositRequest uploadDeposit(String userId, MultipartFile file, Double amount) throws IOException {
        String originalName = file.getOriginalFilename();
        if (amount == null || amount <= 0 || originalName == null || originalName.isEmpty() || file.getSize() == 0) {
            throw new IOException("Invalid input");
        }

        String extension = originalName.contains(".") ? originalName.substring(originalName.lastIndexOf('.')) : "";
        String fileName = UUID.randomUUID() + extension;

        String uploadDir = System.getProperty("user.home") + File.separator + "xinpay-uploads" + File.separator;
        File uploadPath = new File(uploadDir);
        if (!uploadPath.exists()) uploadPath.mkdirs();

        File destination = new File(uploadDir + fileName);
        file.transferTo(destination);

        InrDepositRequest deposit = new InrDepositRequest();
        deposit.setUserId(userId);
        deposit.setImageUrl(fileName);
        deposit.setVerified(false);
        deposit.setRejected(false);
        deposit.setAmount(amount);

        return inrDepositRequestRepository.save(deposit);
    }


    public boolean verifyDeposit(Long id) {
        Optional<InrDepositRequest> opt = inrDepositRequestRepository.findById(id);
        if (opt.isEmpty()) return false;
        InrDepositRequest req = opt.get();
        if (!req.isVerified()) {
            req.setVerified(true);
            req.setVerifiedAt(java.time.LocalDateTime.now());
            inrDepositRequestRepository.save(req);

            Balance balance = balanceRepository.findById(req.getUserId()).orElseGet(() -> {
                Balance b = new Balance();
                b.setUserId(req.getUserId());
                b.setInrBalance(0.0);
                b.setUsdtBalance(0.0);
                return b;
            });

            balance.setInrBalance(balance.getInrBalance() + req.getAmount());
            balanceRepository.save(balance);
            
            
            try {
                Long userIdLong = Long.parseLong(req.getUserId());
                userRepository.findById(userIdLong).ifPresent(user -> {
                    emailService.sendInrDepositApprovedEmail(user.getEmail(), user.getFullName(), req.getAmount());
                });
            } catch (NumberFormatException ignored) {}
            
            
        }
        return true;
    }

    public boolean rejectDeposit(Long id) {
        Optional<InrDepositRequest> opt = inrDepositRequestRepository.findById(id);
        if (opt.isEmpty()) return false;
        InrDepositRequest req = opt.get();
        req.setRejected(true);
        inrDepositRequestRepository.save(req);
        return true;
    }

    public Optional<InrDepositRequest> getDepositByUserId(String userId) {
        return inrDepositRequestRepository.findTopByUserIdOrderByIdDesc(userId);
    }

    public List<InrDepositRequest> getAllDepositsByUser(String userId) {
        return inrDepositRequestRepository.findAllByUserIdOrderByIdDesc(userId);
    }

    public List<InrDepositRequest> getPendingDeposits() {
        return inrDepositRequestRepository.findByVerifiedFalseAndRejectedFalse();
    }

    public double getTotalBalanceByUser(String userId) {
        Balance balance = balanceRepository.findById(userId).orElse(null);
        return balance != null ? balance.getInrBalance() : 0.0;
    }
}
