package example.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * Controller that requires an API key to access.
 */
@RestController
public class SecureController {
    private static final Logger LOG = LoggerFactory.getLogger(NonSecureController.class);

    @GetMapping(value = "/api/v1/secure")
    public Mono<ResponseEntity<?>> secure() {
        return Mono.fromSupplier(() -> {
            LOG.info("Received request: /api/v1/secure");
            return ResponseEntity.ok().build();
        });
    }
}
