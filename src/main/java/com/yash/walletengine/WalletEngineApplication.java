package com.yash.walletengine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class WalletEngineApplication {

    private static final Logger log = LoggerFactory.getLogger(WalletEngineApplication.class);

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(WalletEngineApplication.class, args);
        String[] activeProfiles = context.getEnvironment().getActiveProfiles();
        if (activeProfiles.length == 0) {
            log.info(">>> No active profile set, using default configuration");
        } else {
            log.info(">>> Active profile(s): {}", String.join(", ", activeProfiles));
        }
        String label = context.getEnvironment().getProperty("app.environment-label");
        log.info(">>> app.environment-label = {}", label);
    }
}