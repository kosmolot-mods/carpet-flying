package pl.kosma.carpetflying.mixin;

import carpet.patches.EntityPlayerMPFake;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import pl.kosma.carpetflying.CarpetFlyingSettings;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    @Redirect(method = "getDisplayName", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getName()Lnet/minecraft/text/Text;"))
    public Text getDisplayName(PlayerEntity playerEntity) {
        String playerName = playerEntity.getName().getString();
        if (CarpetFlyingSettings.displayNameCarpetBot) {
            if (playerEntity instanceof EntityPlayerMPFake) {
                playerName += " [bot]";
            }
        }
        if (CarpetFlyingSettings.displayNameVanillaTweaksAFK) {
            AbstractTeam team = playerEntity.getScoreboardTeam();
            if (team != null && team.getName().equals("afkDis.afk"))
                playerName += " [afk]";
        }
        return Text.literal(playerName);
    }
}
