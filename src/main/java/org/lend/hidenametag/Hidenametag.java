package org.lend.hidenametag;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class Hidenametag implements ModInitializer {

    public static final String MOD_ID = "HideNameTag";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("\n \n [HideNameTag] - loaded! \n By L. (lendspele) \n");

        HNTManager.loadConfig();

        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!world.isClient() && entity instanceof ServerPlayerEntity target) {
                if (!target.hasStatusEffect(StatusEffects.INVISIBILITY)) {
                    String nick = target.getGameProfile().name();

                    Text nameTag = Text.literal("-✧ ")
                            .styled(s -> s.withColor(HNTManager.getBracketColor()))
                            .append(Text.literal(nick).styled(s -> s.withColor(HNTManager.getNameColor())))
                            .append(Text.literal(" ✧-").styled(s -> s.withColor(HNTManager.getBracketColor())));

                    player.sendMessage(nameTag, true);
                }
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(literal("hidenametag")
                    .then(literal("NameColor")
                            .requires(src -> src.hasPermissionLevel(2))
                            .then(argument("hex", StringArgumentType.word())
                                    .executes(ctx -> {
                                        String hex = StringArgumentType.getString(ctx, "hex");
                                        HNTManager.setNameColor(hex);
                                        ctx.getSource().sendFeedback(() -> Text.literal("The name color has been changed to " + hex), false);
                                        return 1;
                                    })))
                    .then(literal("TextColor")
                            .requires(src -> src.hasPermissionLevel(2))
                            .then(argument("hex", StringArgumentType.word())
                                    .executes(ctx -> {
                                        String hex = StringArgumentType.getString(ctx, "hex");
                                        HNTManager.setBracketColor(hex);
                                        ctx.getSource().sendFeedback(() -> Text.literal("The text color has been changed to " + hex), false);
                                        return 1;
                                    })))
            );
        });
    }
}
