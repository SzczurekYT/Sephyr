package yt.szczurek.sephyr.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;
import yt.szczurek.sephyr.Sephyr;

public record CastWordTimeoutPacket() implements CustomPacketPayload {
    public static final Identifier ID = Sephyr.identifier("cast_word_timeout");

    public static final Type<CastWordTimeoutPacket> TYPE = new Type<>(ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, CastWordTimeoutPacket> CODEC = StreamCodec.unit(
            new CastWordTimeoutPacket()
    );

    @Override
    public @NonNull Type<? extends CastWordTimeoutPacket> type() {
        return TYPE;
    }
}
