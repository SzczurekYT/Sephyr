package yt.szczurek.sephyr.network;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.network.BalmNetworking;
import net.minecraft.server.level.ServerPlayer;
import yt.szczurek.sephyr.spell.casting.ServerCastingManager;

public class ServerNetworkHandler {
    public static void register() {
        BalmNetworking networking = Balm.networking();
        networking.registerServerboundPacket(
                AddCastWordPacket.TYPE,
                AddCastWordPacket.class,
                AddCastWordPacket.CODEC,
                ServerNetworkHandler::handleAddCastWord
        );
        networking.registerServerboundPacket(
                CastWordTimeoutPacket.TYPE,
                CastWordTimeoutPacket.class,
                CastWordTimeoutPacket.CODEC,
                ServerNetworkHandler::handleCastWordTimeout
        );
    }

    public static void handleAddCastWord(ServerPlayer player, final AddCastWordPacket packet) {
        ServerCastingManager.addWord(player, packet.word());
    }

    public static void handleCastWordTimeout(ServerPlayer player, final CastWordTimeoutPacket ignoredPacket) {
        ServerCastingManager.onTimeout(player);
    }
}
