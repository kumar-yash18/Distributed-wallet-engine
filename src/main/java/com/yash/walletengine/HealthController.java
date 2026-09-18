package com.yash.walletengine;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @RestController = @Controller + @ResponseBody.
 *
 * @Controller alone tells Spring "this class handles HTTP requests," but by default
 * it expects each method to return the NAME of a view (like an HTML template) to render.
 * @ResponseBody tells Spring "don't treat the return value as a view name — serialize it
 * directly into the HTTP response body instead" (as JSON, via the Jackson library that
 * spring-boot-starter-web pulled in automatically).
 *
 * Because we combined both into @RestController, every method below can just return a
 * plain Java object and Spring will convert it to JSON for us.
 *
 * This class itself is discovered by @ComponentScan (declared on
 * WalletEngineApplication) and instantiated into the IoC container as a singleton bean —
 * we never call `new HealthController()` anywhere ourselves.
 */
@RestController
public class HealthController {

    /**
     * @GetMapping("/health") maps HTTP GET requests to the path "/health" onto this
     * method. Internally, Spring's DispatcherServlet intercepts every incoming request,
     * looks at its path + HTTP verb, and routes it to whichever method has a matching
     * @GetMapping/@PostMapping/etc. This is the "front controller" pattern.
     *
     * Returning a Map<String, String> here gets serialized by Jackson into:
     *   {"status":"UP"}
     */
    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "UP");
    }
}
