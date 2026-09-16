package yt.szczurek.sephyr.platform;

import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.attachment.AttachmentType;
import org.jspecify.annotations.NonNull;
import yt.szczurek.sephyr.SephyrMod;
import yt.szczurek.sephyr.platform.services.WordsAttachment;

import java.util.List;

public class NeoForgeWordsAttachment implements WordsAttachment {

    @Override
    public @NonNull List<String> getWords(Entity entity) {
        AttachmentType<List<String>> type = SephyrMod.CAST_WORDS.get();
        List<String> existing = entity.getExistingDataOrNull(type);
        return existing == null ? List.of() : List.copyOf(existing);
    }

    @Override
    public void setWords(Entity entity, List<String> words) {
        entity.setData(SephyrMod.CAST_WORDS.get(), List.copyOf(words));
    }

    @Override
    public void clearWords(Entity entity) {
        entity.removeData(SephyrMod.CAST_WORDS.get());
    }

    @Override
    public boolean hasWords(Entity entity) {
        return entity.hasData(SephyrMod.CAST_WORDS.get());
    }
}