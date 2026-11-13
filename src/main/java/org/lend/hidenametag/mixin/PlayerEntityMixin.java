package org.lend.hidenametag.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class PlayerEntityMixin {

    @Inject(at = @At("HEAD"), method = "initInventoryMenu")
    private void onPlayerJoinServer(CallbackInfo ci) {
        ServerPlayer player = (ServerPlayer) (Object) this;

        if (!(player.level() instanceof ServerLevel world)) return;

        String playerName = player.getName().getString();
        PlayerTeam currentTeam = world.getScoreboard().getPlayersTeam(playerName);

        PlayerTeam hideTeam = world.getScoreboard().getPlayerTeam("HideNameTag");
        if (hideTeam == null) {
            hideTeam = world.getScoreboard().addPlayerTeam("HideNameTag");
            hideTeam.setNameTagVisibility(Team.Visibility.NEVER);
            hideTeam.setSeeFriendlyInvisibles(false);
        }

        if (currentTeam == null || !currentTeam.getName().equals("HideNameTag")) {
            world.getScoreboard().addPlayerToTeam(playerName, hideTeam);
        }
    }
}
