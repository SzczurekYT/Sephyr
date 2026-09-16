package yt.szczurek.sephyr.spell.casting;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import yt.szczurek.sephyr.Sephyr;
import yt.szczurek.sephyr.network.AddCastWordPacket;
import yt.szczurek.sephyr.network.CastWordTimeoutPacket;
import yt.szczurek.sephyr.platform.Services;
import yt.szczurek.sephyr.spell.SpellElement;

public class ClientCastingManager {
    public static final ClientCastingManager INSTANCE = new ClientCastingManager();
    private static final Component STAR_COMPONENT = Component.literal("✨").withColor(16773205); // 0xfff055
    // Time from last word, before the spell is executed / reset
    private static final long WORD_TIMEOUT_MS = 2500;

    private long lastWordTimestamp = Long.MAX_VALUE;

    public void handleNewWord(String word) {
        Sephyr.LOG.debug("Received word from Sepple: {}", word);

        lastWordTimestamp = System.currentTimeMillis();
        displayCastWord(word);
        Services.PLATFORM.sendCustomPacketToServer(new AddCastWordPacket(word));
    }

    public void displayCastWord(String word) {
        var spellComponent = Component.literal(" " + word + " ");

        var element = SpellElement.byWord(word);
        int color = element != null ? element.color() : 0xFFFFFF;
        spellComponent.withColor(color);

        var message = STAR_COMPONENT.copy().append(spellComponent).append(STAR_COMPONENT);
        Minecraft.getInstance().gui.setOverlayMessage(message, false);
    }

    public void tick() {
        if (System.currentTimeMillis() - WORD_TIMEOUT_MS > lastWordTimestamp) {
            Services.PLATFORM.sendCustomPacketToServer(new CastWordTimeoutPacket());
            lastWordTimestamp = Long.MAX_VALUE;
        }
    }
}
