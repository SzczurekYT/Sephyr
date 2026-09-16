package yt.szczurek.sephyr.platform;

import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.world.entity.Entity;
import org.jspecify.annotations.NonNull;
import yt.szczurek.sephyr.SephyrMod;
import yt.szczurek.sephyr.platform.services.WordsAttachment;

import java.util.List;

public class FabricWordsAttachment implements WordsAttachment {

    @Override
    public @NonNull List<String> getWords(Entity entity) {
        AttachmentType<List<String>> type = SephyrMod.CAST_WORDS;
        return List.copyOf(entity.getAttachedOrGet(type, List::of));
    }

    @Override
    public void setWords(Entity entity, List<String> words) {
        entity.setAttached(SephyrMod.CAST_WORDS, List.copyOf(words));
    }

    @Override
    public void clearWords(Entity entity) {
        entity.removeAttached(SephyrMod.CAST_WORDS);
    }

    @Override
    public boolean hasWords(Entity entity) {
        return entity.hasAttached(SephyrMod.CAST_WORDS);
    }
}