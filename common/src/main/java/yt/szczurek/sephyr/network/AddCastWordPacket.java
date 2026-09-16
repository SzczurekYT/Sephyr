package yt.szczurek.sephyr.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;
import yt.szczurek.sephyr.Sephyr;

public record AddCastWordPacket(String word) implements CustomPacketPayload {
    public static final Identifier ID = Sephyr.identifier("add_cast_word");

    public static final CustomPacketPayload.Type<AddCastWordPacket> TYPE = new CustomPacketPayload.Type<>(ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, AddCastWordPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            AddCastWordPacket::word,
            AddCastWordPacket::new
    );

    @Override
    public @NonNull Type<? extends AddCastWordPacket> type() {
        return TYPE;
    }
}
