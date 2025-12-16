package com.mutualfund.config;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.mutualfund.model.entity.MutualFund;
import com.mutualfund.model.entity.Nav;
import com.mutualfund.model.entity.User;
import com.mutualfund.repository.MutualFundRepository;
import com.mutualfund.repository.NavRepository;
import com.mutualfund.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Initializes the database with default data at application startup. Creates an admin user and
 * loads popular Indian mutual funds if they don't already exist.
 *
 * @author Mutual Fund Management System
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MutualFundRepository mutualFundRepository;
    private final NavRepository navRepository;

    /**
     * Initializes the database with admin user and mutual funds if not already present.
     *
     * @param args command line arguments passed to the application
     * @throws Exception if initialization fails
     */
    @Override
    public void run(String... args) throws Exception {
        initializeAdminUser();
        initializeMutualFunds();
    }

    /**
     * Creates the default admin user if it doesn't exist in the database. Uses credentials:
     * username=admin, password=admin123, role=ADMIN
     */
    private void initializeAdminUser() {
        String adminUsername = "admin";

        if (!userRepository.existsByUsername(adminUsername)) {
            User admin =
                    User.builder()
                            .username(adminUsername)
                            .password(passwordEncoder.encode("admin123"))
                            .role(User.Role.ADMIN)
                            .build();

            userRepository.save(admin);
            log.info("Admin user created successfully with username: {}", adminUsername);
        } else {
            log.info("Admin user already exists, skipping initialization");
        }
    }

    /**
     * Loads popular Indian mutual funds with NAV data from yesterday. Only creates funds if the
     * table is empty.
     */
    private void initializeMutualFunds() {
        if (mutualFundRepository.count() > 0) {
            log.info("Mutual funds already exist, skipping initialization");
            return;
        }

        LocalDate yesterday = LocalDate.now().minusDays(1);
        log.info("Initializing mutual funds with NAV date: {}", yesterday);

        // Popular Indian mutual funds with realistic NAV values
        createMutualFundWithNav("SBI Bluechip Fund", new BigDecimal("72.45"), yesterday);
        createMutualFundWithNav(
                "HDFC Balanced Advantage Fund", new BigDecimal("368.92"), yesterday);
        createMutualFundWithNav(
                "ICICI Prudential Bluechip Fund", new BigDecimal("98.76"), yesterday);
        createMutualFundWithNav("Axis Bluechip Fund", new BigDecimal("54.23"), yesterday);
        createMutualFundWithNav("Kotak Standard Multicap Fund", new BigDecimal("65.18"), yesterday);
        createMutualFundWithNav("Mirae Asset Large Cap Fund", new BigDecimal("89.34"), yesterday);
        createMutualFundWithNav("Parag Parikh Flexi Cap Fund", new BigDecimal("78.56"), yesterday);
        createMutualFundWithNav("UTI Nifty Index Fund", new BigDecimal("156.23"), yesterday);
        createMutualFundWithNav("Nippon India Small Cap Fund", new BigDecimal("112.89"), yesterday);
        createMutualFundWithNav("SBI Small Cap Fund", new BigDecimal("145.67"), yesterday);
        createMutualFundWithNav(
                "HDFC Mid-Cap Opportunities Fund", new BigDecimal("178.45"), yesterday);
        createMutualFundWithNav("Axis Midcap Fund", new BigDecimal("92.34"), yesterday);
        createMutualFundWithNav(
                "DSP Equity Opportunities Fund", new BigDecimal("321.78"), yesterday);
        createMutualFundWithNav("Tata Digital India Fund", new BigDecimal("45.67"), yesterday);
        createMutualFundWithNav(
                "Motilal Oswal Nasdaq 100 Fund", new BigDecimal("67.89"), yesterday);

        log.info("Successfully initialized 15 mutual funds with NAV history");
    }

    /**
     * Creates and saves a mutual fund entity with NAV entry in NAV history table.
     *
     * @param name the name of the mutual fund
     * @param navValue the Net Asset Value
     * @param navDate the NAV date
     */
    private void createMutualFundWithNav(String name, BigDecimal navValue, LocalDate navDate) {
        // Create mutual fund
        MutualFund fund = MutualFund.builder().name(name).build();
        MutualFund savedFund = mutualFundRepository.save(fund);

        // Create NAV history entry
        Nav nav =
                Nav.builder()
                        .fundId(savedFund.getFundId())
                        .nav(navValue)
                        .navDate(navDate)
                        .deleted(false)
                        .build();
        navRepository.save(nav);

        log.debug("Created mutual fund: {} with NAV: {} on {}", name, navValue, navDate);
    }
}
