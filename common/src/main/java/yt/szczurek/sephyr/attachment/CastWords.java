package yt.szczurek.sephyr.attachment;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import yt.szczurek.sephyr.platform.Services;
import yt.szczurek.sephyr.spell.casting.ServerCastingManager;

import java.util.Collection;
import java.util.List;

public class CastWords {
    public static final StreamCodec<ByteBuf, List<String>> STREAM_CODEC = ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list());

    public static List<String> get(Entity entity) {
        return Services.WORDS_ATTACHMENT.getWords(entity);
    }

    public static void add(Entity entity, String word) {
        Services.WORDS_ATTACHMENT.addWord(entity, word);
        onChanged(entity);
    }

    public static void addAll(Entity entity, Collection<String> words) {
        Services.WORDS_ATTACHMENT.addWords(entity, words);
        onChanged(entity);
    }

    public static void clear(Entity entity) {
        Services.WORDS_ATTACHMENT.clearWords(entity);
        onChanged(entity);
    }

    public static boolean hasWords(Entity entity) {
        return Services.WORDS_ATTACHMENT.hasWords(entity);
    }

    private static void onChanged(Entity entity) {
        List<String> words = get(entity);

        ServerCastingManager.onChanged(entity, words);
    }
}