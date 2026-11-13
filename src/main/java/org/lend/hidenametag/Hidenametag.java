package org.lend.hidenametag;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Hidenametag implements ModInitializer {

    public static final String MOD_ID = "HideNameTag";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("\n \n [HideNameTag] - loaded! \n By L. (lendspele) \n");

        HNTManager.loadConfig();

        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!world.isClientSide() && entity instanceof ServerPlayer target) {
                if (!target.hasEffect(MobEffects.INVISIBILITY)) {
                    String nick = target.getGameProfile().name();

                    Component nameTag = Component.literal("-✧ ")
                            .withStyle(s -> s.withColor(HNTManager.getBracketColor()))
                            .append(Component.literal(nick).withStyle(s -> s.withColor(HNTManager.getNameColor())))
                            .append(Component.literal(" ✧-").withStyle(s -> s.withColor(HNTManager.getBracketColor())));

                    player.sendOverlayMessage(nameTag);
                }
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(Commands.literal("hidenametag")
                    .then(Commands.literal("NameColor")
                            .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                            .then(Commands.argument("hex", StringArgumentType.word())
                                    .executes(ctx -> {
                                        String hex = StringArgumentType.getString(ctx, "hex");
                                        HNTManager.setNameColor(hex);
                                        ctx.getSource().sendSuccess(() -> Component.literal("The name color has been changed to " + hex), false);
                                        return 1;
                                    })))
                    .then(Commands.literal("TextColor")
                            .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                            .then(Commands.argument("hex", StringArgumentType.word())
                                    .executes(ctx -> {
                                        String hex = StringArgumentType.getString(ctx, "hex");
                                        HNTManager.setBracketColor(hex);
                                        ctx.getSource().sendSuccess(() -> Component.literal("The text color has been changed to " + hex), false);
                                        return 1;
                                    })))
            );
        });
    }
}
