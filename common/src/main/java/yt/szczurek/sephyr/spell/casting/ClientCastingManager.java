package yt.szczurek.sephyr.spell.casting;

import net.blay09.mods.balm.Balm;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import yt.szczurek.sephyr.Sephyr;
import yt.szczurek.sephyr.attachment.CastWords;
import yt.szczurek.sephyr.network.AddCastWordPacket;
import yt.szczurek.sephyr.network.CastWordTimeoutPacket;
import yt.szczurek.sephyr.spell.SpellElement;

import java.util.stream.Collectors;

public class ClientCastingManager {
    public static final ClientCastingManager INSTANCE = new ClientCastingManager();
    // Time from last word, before the spell is executed / reset
    private static final long WORD_TIMEOUT_MS = 2000;

    private long lastWordTimestamp = Long.MAX_VALUE;

    public void handleNewWord(String word) {
        Sephyr.LOG.debug("Received word from Sepple: {}", word);

        lastWordTimestamp = System.currentTimeMillis();
        Balm.networking().sendToServer(new AddCastWordPacket(word));
    }

    public void tick() {
        if (System.currentTimeMillis() - WORD_TIMEOUT_MS > lastWordTimestamp) {
            Balm.networking().sendToServer(new CastWordTimeoutPacket());
            lastWordTimestamp = Long.MAX_VALUE;
        }
    }
}
