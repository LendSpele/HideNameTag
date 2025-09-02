package org.lend.hidenametag.mixin;

import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class PlayerEntityMixin {

    @Inject(at = @At("HEAD"), method = "onSpawn")
    private void onPlayerJoinServer(CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

        if (!(player.getWorld() instanceof ServerWorld world)) return;

        Team playersTeam = world.getScoreboard().getTeam("HideNameTag");
        if (playersTeam == null) {
            playersTeam = world.getScoreboard().addTeam("HideNameTag");
            playersTeam.setNameTagVisibilityRule(AbstractTeam.VisibilityRule.NEVER); // скрываем ник
            playersTeam.setShowFriendlyInvisibles(false);
        }

        // добавляем игрока в команду, если он ещё не состоит в ней
        String playerName = player.getName().getString();
        Team currentTeam = world.getScoreboard().getScoreHolderTeam(playerName);
        if (currentTeam == null || !currentTeam.getName().equals("HideNameTag")) {
            world.getScoreboard().addScoreHolderToTeam(playerName, playersTeam);
        }
    }
}
