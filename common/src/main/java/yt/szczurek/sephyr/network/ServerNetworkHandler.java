package yt.szczurek.sephyr.network;

import net.minecraft.server.level.ServerPlayer;
import yt.szczurek.sephyr.spell.casting.ServerCastingManager;

public class ServerNetworkHandler {
    public static void handleAddCastWord(final AddCastWordPacket packet, ServerPlayer player) {
        ServerCastingManager.addWord(player, packet.word());
    }

    public static void handleCastWordTimeout(final CastWordTimeoutPacket ignoredPacket, ServerPlayer player) {
        ServerCastingManager.onTimeout(player);
    }
}
