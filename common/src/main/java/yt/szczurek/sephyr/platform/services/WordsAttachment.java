package yt.szczurek.sephyr.platform.services;

import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface WordsAttachment {

    @NotNull List<String> getWords(Entity entity);

    void setWords(Entity entity, List<String> words);

    default void addWord(Entity entity, String word) {
        List<String> words = new ArrayList<>(getWords(entity));
        words.add(word);
        setWords(entity, words);
    }

    default void addWords(Entity entity, Collection<String> toAdd) {
        List<String> words = new ArrayList<>(getWords(entity));
        words.addAll(toAdd);
        setWords(entity, words);
    }

    void clearWords(Entity entity);

    boolean hasWords(Entity entity);
}