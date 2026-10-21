package yt.szczurek.sephyr.registry;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.core.BalmRegistrars;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import yt.szczurek.sephyr.attachment.CastWords;
import yt.szczurek.sephyr.spell.Spell;

import static yt.szczurek.sephyr.Sephyr.identifier;

public class SephyrRegistries {
    public static final ResourceKey<Registry<Spell>> SPELL = ResourceKey.createRegistryKey(identifier("spell"));

    public static void register(BalmRegistrars registrars) {
        registrars.registrar().createCustomRegistry(SPELL);
        registrars.registrar(Registries.SOUND_EVENT, SephyrSounds::register);
        registrars.registrar(SPELL, SephyrSpells::register);
        registrars.dataAttachmentTypes(CastWords::register);
    }
}
