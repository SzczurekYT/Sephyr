package yt.szczurek.sephyr.spells.wind;

import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import yt.szczurek.sephyr.spells.SpellEffect;
import yt.szczurek.sephyr.spells.SpellParameters;
import yt.szczurek.sephyr.spells.SpellSequence;

public class WindLeapEffect extends SpellEffect {

    public WindLeapEffect(Identifier name) {
        super(name);
    }

    @Override
    public void execute(SpellSequence sequence, SpellParameters params) {
        Entity caster = params.castingEntity;
        caster.setDeltaMovement(params.castDirection.scale(3.0));
        if (caster instanceof ServerPlayer player) {
            player.connection.send(new ClientboundSetEntityMotionPacket(player));
        }
    }


}
