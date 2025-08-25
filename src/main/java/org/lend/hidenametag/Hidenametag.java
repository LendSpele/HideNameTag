package org.lend.hidenametag;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.ActionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Hidenametag implements ModInitializer {

    public static final String MOD_ID = "HideNameTag";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {

        LOGGER.info("\n \n [HideNameTag] - loaded! \n \n By L. (lendspele) 0/\n");

        net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            handler.player.setCustomNameVisible(false);
        });

        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!world.isClient && entity instanceof ServerPlayerEntity target) {

                if (!target.hasStatusEffect(StatusEffects.INVISIBILITY)) {
                    String nick = target.getGameProfile().getName();

                    Text NameTag = Text.literal("-{ " + nick + " }-")
                            .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x084882))); // HEX

                    player.sendMessage(NameTag, true);
                }
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        });
    }
}