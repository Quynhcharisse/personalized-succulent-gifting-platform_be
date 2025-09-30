package com.exe201.group1.psgp_be;

import com.exe201.group1.psgp_be.enums.Role;
import com.exe201.group1.psgp_be.models.Account;
import com.exe201.group1.psgp_be.models.AppConfig;
import com.exe201.group1.psgp_be.models.User;
import com.exe201.group1.psgp_be.repositories.AccountRepo;
import com.exe201.group1.psgp_be.repositories.AppConfigRepo;
import com.exe201.group1.psgp_be.repositories.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@SpringBootApplication
@RequiredArgsConstructor
public class PsgpBeApplication implements CommandLineRunner {

    private final AccountRepo accountRepo;
    private final UserRepo userRepo;
    private final AppConfigRepo appConfigRepo;

    public static void main(String[] args) {
        SpringApplication.run(PsgpBeApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // Init ADMIN account
        initAdminAccount();

        // Init SELLER account
        initSellerAccount();

        //Init app config
        initAppConfig();
    }

    private void initAdminAccount() {
        String email = "huatri2004@gmail.com";
        if (accountRepo.findByEmail(email).isPresent()) {
            System.out.println("Admin account already exists");
            return;
        }

        // Create Admin Account
        Account adminAccount = Account.builder()
                .email(email)
                .role(Role.ADMIN)
                .active(true)
                .registerDate(LocalDateTime.now())
                .build();

        adminAccount = accountRepo.save(adminAccount);

        // Create Admin User
        User adminUser = User.builder()
                .account(adminAccount)
                .name("System Administrator")
                .phone("0923456789")
                .gender("male")
                .address("Admin Office, HCM")
                .avatarUrl("https://static.vecteezy.com/system/resources/thumbnails/011/675/374/small_2x/man-avatar-image-for-profile-png.png")
                .build();

        adminUser = userRepo.save(adminUser);
        adminAccount.setUser(adminUser);
        accountRepo.save(adminAccount);

        System.out.println("Created ADMIN account: " + email);
    }

    private void initSellerAccount() {
        String email = "vannhuquynhp@gmail.com";
        if (accountRepo.findByEmail(email).isPresent()) {
            System.out.println("Seller account already exists");
            return;
        }

        // Create Seller Account
        Account sellerAccount = Account.builder()
                .email(email)
                .role(Role.SELLER)
                .active(true)
                .registerDate(LocalDateTime.now())
                .build();

        sellerAccount = accountRepo.save(sellerAccount);

        // Create Seller User
        User sellerUser = User.builder()
                .account(sellerAccount)
                .name("Best Seller")
                .phone("0709142394")
                .gender("female")
                .address("Nguyen Duan, HCM")
                .avatarUrl("https://cdn-icons-png.freepik.com/512/6833605.png")
                .build();

        sellerUser = userRepo.save(sellerUser);
        sellerAccount.setUser(sellerUser);
        accountRepo.save(sellerAccount);

        System.out.println("Created SELLER account: " + email);
    }

    private void initAppConfig(){
        if (appConfigRepo.count() == 0){
            appConfigRepo.saveAll(List.of(
                    AppConfig.builder()
                            .key("accessory")
                            .value(null)
                            .build(),
                    AppConfig.builder()
                            .key("business")
                            .value(Map.of("sellRate", 1))
                            .build()
            ));
        }
    }

}
