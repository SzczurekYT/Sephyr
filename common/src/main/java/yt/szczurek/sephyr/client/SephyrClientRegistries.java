package yt.szczurek.sephyr.client;

import net.blay09.mods.balm.client.BalmClientRegistrars;
import net.blay09.mods.balm.client.renderer.entity.BalmEntityRendererRegistrar;
import net.minecraft.client.renderer.entity.NoopRenderer;
import org.jspecify.annotations.NonNull;
import yt.szczurek.sephyr.registry.SephyrEntities;

import java.util.function.Consumer;

public class SephyrClientRegistries {
    public static void register(BalmClientRegistrars registrars) {
        registrars.entityRenderers(registerEntityRenderers());
    }

    private static @NonNull Consumer<BalmEntityRendererRegistrar> registerEntityRenderers() {
        return registrar -> registrar.register(SephyrEntities.SPELL, NoopRenderer::new);
    }
}
