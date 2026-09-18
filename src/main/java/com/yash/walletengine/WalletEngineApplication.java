package com.yash.walletengine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point of the application.
 *
 * @SpringBootApplication is actually THREE annotations bundled into one:
 *   1. @Configuration      -> this class can define Spring beans
 *   2. @EnableAutoConfiguration -> Spring Boot guesses what you need (e.g. an embedded
 *      Tomcat server, a Jackson JSON converter) based on what's on the classpath,
 *      and configures it for you automatically. This is why adding
 *      spring-boot-starter-web to pom.xml was enough to get a working web server —
 *      you never wrote a line of server config.
 *   3. @ComponentScan      -> Spring scans this package (com.yash.walletengine) and
 *      everything under it, looking for classes annotated with @Component,
 *      @Service, @RestController, @Repository, etc. Anything it finds gets
 *      registered into the IoC container (see explanation below).
 */
@SpringBootApplication
public class WalletEngineApplication {

    public static void main(String[] args) {
        // SpringApplication.run() does the real work:
        // 1. Creates the ApplicationContext (the IoC container itself)
        // 2. Runs component scanning + auto-configuration
        // 3. Instantiates every discovered bean and wires their dependencies together
        // 4. Starts the embedded Tomcat server on port 8080 (default)
        SpringApplication.run(WalletEngineApplication.class, args);
    }
}
