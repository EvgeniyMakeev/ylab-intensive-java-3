package dev.makeev.coworking_service_app.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class TokenUtil {
    private static final Map<String, String> tokenStore = new HashMap<>();

    public static String generateToken(String login) {
        String token = UUID.randomUUID().toString();
        tokenStore.put(token, login);
        return token;
    }

    public static Optional<String> validateToken(String token) {
        return Optional.ofNullable(tokenStore.get(token));
    }

    public static void invalidateToken(String token) {
        tokenStore.remove(token);
    }
}