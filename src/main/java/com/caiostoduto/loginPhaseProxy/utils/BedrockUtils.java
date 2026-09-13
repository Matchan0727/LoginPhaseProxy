package com.caiostoduto.loginPhaseProxy.utils;

import java.util.UUID;

public class BedrockUtils {

    /**
     * Determines whether the given UUID belongs to a Bedrock (Geyser/Floodgate) player.
     * @param uuid The UUID of the player.
     * @return true if the player is identified as a Bedrock player, false otherwise.
     */
    public static boolean isBedrockPlayer(UUID uuid) {
        if (uuid == null) {
            return false;
        }

        // 1. Floodgate API
        try {
            Class<?> apiClass = Class.forName("org.geysermc.floodgate.api.FloodgateApi");
            Object api = apiClass.getMethod("getInstance").invoke(null);
            
            // Check if it's an online Floodgate player (Works for GlobalLink accounts too)
            try {
                Object isPlayer = apiClass.getMethod("isFloodgatePlayer", UUID.class).invoke(api, uuid);
                if (Boolean.TRUE.equals(isPlayer)) {
                    return true;
                }
            } catch (Throwable ignored) {}

            // Fallback: check if the UUID itself has the Floodgate prefix pattern (Default unlinked accounts)
            try {
                Object isId = apiClass.getMethod("isFloodgateId", UUID.class).invoke(api, uuid);
                if (Boolean.TRUE.equals(isId)) {
                    return true;
                }
            } catch (Throwable ignored) {}
        } catch (Throwable ignored) {
        }

        // 2. Geyser API
        try {
            Class<?> apiClass = Class.forName("org.geysermc.geyser.api.GeyserApi");
            Object api = apiClass.getMethod("api").invoke(null);
            Object isBedrock = apiClass.getMethod("isBedrockPlayer", UUID.class).invoke(api, uuid);
            if (Boolean.TRUE.equals(isBedrock)) {
                return true;
            }
        } catch (Throwable ignored) {
        }

        // 3. Fallback: UUID prefix check. 
        // Floodgate ID by default uses UUIDs where the most significant bits are 0 (e.g., 00000000-0000-0000-...)
        if (uuid.getMostSignificantBits() == 0L && uuid.getLeastSignificantBits() != 0L) {
            return true;
        }

        return false;
    }
}
