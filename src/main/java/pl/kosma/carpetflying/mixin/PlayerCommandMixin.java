package pl.kosma.carpetflying.mixin;
import carpet.commands.PlayerCommand;
import carpet.utils.Messenger;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pl.kosma.carpetflying.CarpetFlyingSettings;

@Mixin(PlayerCommand.class)
public class PlayerCommandMixin {
    @Inject(method = "cantSpawn", at = @At("HEAD"), cancellable = true, remap = false)
    private static void cantSpawn(CommandContext<ServerCommandSource> context, CallbackInfoReturnable<Boolean> cir) {
        if (CarpetFlyingSettings.disablePlayerSpawnCommand) {
            Messenger.m(context.getSource(), "r Player spawning has been disabled.");
            cir.setReturnValue(true);
        }
    }
}
