package example.security;

import com.github.benmanes.caffeine.cache.AsyncCacheLoader;
import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * Converter that gets the API key from the incoming headers and converts it to an {@link Authentication}
 * that can be checked by the {@link KeyAuthenticationManager}.
 */
@Component
public class KeyAuthenticationConverter implements ServerAuthenticationConverter {
    private static final Logger LOG = LoggerFactory.getLogger(KeyAuthenticationConverter.class);
    private static final String API_KEY_HEADER_NAME = "API_KEY";

    private final AsyncLoadingCache<String, KeyAuthenticationToken> tokenCache;

    public KeyAuthenticationConverter(DataSource dataSource) {
        this.tokenCache = Caffeine.newBuilder()
                .maximumSize(10_000)
                .expireAfterAccess(5, TimeUnit.MINUTES)
                .buildAsync(new DatabaseCacheLoader(dataSource));
    }

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        return Mono.justOrEmpty(exchange)
                .flatMap(serverWebExchange -> Mono.justOrEmpty(serverWebExchange.getRequest().getHeaders().get(API_KEY_HEADER_NAME)))
                .filter(headerValues -> !headerValues.isEmpty())
                .flatMap(headerValues -> lookup(headerValues.get(0)));
    }

    /**
     * Lookup authentication token in cache.
     *
     * @param apiKey api key
     * @return
     */
    private Mono<KeyAuthenticationToken> lookup(final String apiKey) {
        return Mono.fromFuture(tokenCache.get(apiKey));
    }

    /**
     * Loads the cache authentication data from the database.
     */
    private static class DatabaseCacheLoader implements AsyncCacheLoader<String, KeyAuthenticationToken> {

        private final DataSource dataSource;

        DatabaseCacheLoader(DataSource dataSource) {
            this.dataSource = dataSource;
        }

        @Override
        public CompletableFuture<KeyAuthenticationToken> asyncLoad(String key, Executor executor) {
            return CompletableFuture.supplyAsync(() -> {
                try (Connection conn = dataSource.getConnection()) {
                    final String sql = "SELECT * FROM auth WHERE api_key = ?";

                    try (PreparedStatement ps = conn.prepareStatement(sql)) {
                        ps.setString(1, key);

                        try (ResultSet rs = ps.executeQuery()) {
                            if (rs.next()) {
                                // Create an authentication object to hold the credentials, principal,
                                // permissions, etc. that will be validated by the KeyAuthenticationManager
                                KeyAuthenticationToken authentication = new KeyAuthenticationToken(key, rs.getString("user_id"));
                                return authentication;
                            } else {
                                return null;
                            }
                        }
                    }
                } catch (SQLException e) {
                    // In the event of database errors during authentication just log the error and return
                    // an empty mono which will result in the request failing authentication.
                    LOG.error("An error occurred during api key authentication.", e);
                    return null;
                }
            }, executor);
        }

        @Override
        public CompletableFuture<Map<String, KeyAuthenticationToken>> asyncLoadAll(Iterable<? extends String> keys, Executor executor) {
            throw new UnsupportedOperationException();
        }

        @Override
        public CompletableFuture<KeyAuthenticationToken> asyncReload(String key, KeyAuthenticationToken oldValue, Executor executor) {
            return asyncLoad(key, executor);
        }
    }
}
