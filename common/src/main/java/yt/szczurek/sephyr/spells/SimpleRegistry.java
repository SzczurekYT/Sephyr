package yt.szczurek.sephyr.spells;

import com.mojang.serialization.Codec;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jspecify.annotations.NonNull;
import yt.szczurek.sephyr.Sephyr;
import yt.szczurek.sephyr.platform.Services;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SimpleRegistry<T> extends SimpleJsonResourceReloadListener<T> {
    private final Map<Identifier, T> data = new HashMap<>();
    private final String id;

    public SimpleRegistry(String id, Codec<T> codec) {
        super(codec, new FileToIdConverter(id, ".json"));
        this.id = id;
    }

    @Override
    protected void apply(@NonNull Map<Identifier, T> preparations, @NonNull ResourceManager manager, @NonNull ProfilerFiller profiler) {
        data.clear();
        data.putAll(preparations);
    }

    public void register() {
        Services.PLATFORM.registerServerReloadListener(Sephyr.identifier(id), this);
    }

    public Optional<T> get(Identifier identifier) {
        return Optional.ofNullable(data.get(identifier));
    }
}