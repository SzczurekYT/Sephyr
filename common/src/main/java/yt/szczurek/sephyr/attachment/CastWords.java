package yt.szczurek.sephyr.attachment;

import com.google.common.collect.ImmutableList;
import io.netty.buffer.ByteBuf;
import net.blay09.mods.balm.platform.attachment.BalmDataAttachmentTypeRegistrar;
import net.blay09.mods.balm.platform.attachment.DataAttachmentLookup;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import org.jspecify.annotations.NonNull;
import yt.szczurek.sephyr.spell.casting.ServerCastingManager;

import java.util.Collection;
import java.util.function.Function;

public class CastWords {
    public static final StreamCodec<ByteBuf, ImmutableList<String>> STREAM_CODEC = ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()).map(ImmutableList::copyOf, Function.identity());

    private static DataAttachmentLookup<ImmutableList<String>> LOOKUP;

    public static void register(BalmDataAttachmentTypeRegistrar registrar) {
        LOOKUP = registrar.<ImmutableList<String>>register("cast_words",
                builder -> builder.initializer(ImmutableList::of)
                        .networkSynchronized(STREAM_CODEC)
        ).asLookup();
    }

    public static @NonNull ImmutableList<String> get(Entity entity) {
        return LOOKUP.getOrCreate(entity);
    }

    public static void setWords(Entity entity, ImmutableList<String> words) {
        LOOKUP.update(entity, words);
    }

    public static void add(Entity entity, String word) {
        ImmutableList<String> words = new ImmutableList.Builder<String>()
                .addAll(get(entity))
                .add(word)
                .build();
        setWords(entity, words);
        onChanged(words, entity);
    }

    void addWords(Entity entity, Collection<String> toAdd) {
        ImmutableList<String> words = new ImmutableList.Builder<String>()
                .addAll(get(entity))
                .addAll(toAdd)
                .build();
        setWords(entity, words);
        onChanged(words, entity);
    }

    public static void clear(Entity entity) {
        LOOKUP.remove(entity);
        onChanged(ImmutableList.of(), entity);
    }

    public boolean hasWords(Entity entity) {
       return LOOKUP.has(entity);
    }

    private static void onChanged(ImmutableList<String> words, Entity entity) {
        ServerCastingManager.onChanged(entity, words);
    }
}