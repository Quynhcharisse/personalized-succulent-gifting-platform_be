package com.exe201.group1.psgp_be.services.implementors;

import com.exe201.group1.psgp_be.dto.response.ResponseObject;
import com.exe201.group1.psgp_be.models.Account;
import com.exe201.group1.psgp_be.models.Wallet;
import com.exe201.group1.psgp_be.repositories.AccountRepo;
import com.exe201.group1.psgp_be.repositories.WalletRepo;
import com.exe201.group1.psgp_be.services.JWTService;
import com.exe201.group1.psgp_be.services.WalletService;
import com.exe201.group1.psgp_be.utils.CookieUtil;
import com.exe201.group1.psgp_be.utils.ResponseBuilder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WalletServiceImpl implements WalletService {

    JWTService jwtService;
    AccountRepo accountRepo;
    private final WalletRepo walletRepo;

    @Override
    public ResponseEntity<ResponseObject> getWallet(HttpServletRequest httpRequest) {
        Account account = CookieUtil.extractAccountFromCookie(httpRequest, jwtService, accountRepo);

        if (account == null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Tài khoản không hợp lệ", null);
        }

        if (account.getUser() == null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Không tìm thấy thông tin người dùng", null);
        }

        Wallet wallet = account.getWallet();

        if (wallet == null) {
            wallet = Wallet.builder()
                    .account(account)
                    .balance(BigDecimal.ZERO)
                    .build();

            wallet = walletRepo.save(wallet);

            account.setWallet(wallet);
            accountRepo.save(account);
        }

        return ResponseBuilder.build(HttpStatus.OK, "Lấy ví thành công", wallet.getBalance());
    }
}
