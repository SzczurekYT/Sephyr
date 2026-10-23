package yt.szczurek.sephyr.registry;

import net.blay09.mods.balm.core.BalmRegistrars;
import net.blay09.mods.balm.core.CustomRegistryBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import yt.szczurek.sephyr.attachment.CastWords;
import yt.szczurek.sephyr.spell.SpellDefinition;

import static yt.szczurek.sephyr.Sephyr.identifier;

public class SephyrRegistries {
    public static final ResourceKey<Registry<SpellDefinition<?>>> SPELL_REGISTRY_KEY = ResourceKey.createRegistryKey(identifier("spell"));
    public static Registry<SpellDefinition<?>> SPELL;

    public static void register(BalmRegistrars registrars) {
        SPELL = registrars.registrar().createCustomRegistry(SPELL_REGISTRY_KEY, CustomRegistryBuilder::sync);

        registrars.registrar(Registries.SOUND_EVENT, SephyrSounds::register);
        registrars.registrar(SPELL_REGISTRY_KEY, SephyrSpells::register);
        registrars.dataAttachmentTypes(CastWords::register);
        registrars.entityTypes(SephyrEntities::register);
    }
}
