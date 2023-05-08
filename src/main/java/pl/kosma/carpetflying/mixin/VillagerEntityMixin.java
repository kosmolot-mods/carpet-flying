package pl.kosma.carpetflying.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.village.TradeOfferList;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pl.kosma.carpetflying.CarpetFlyingSettings;

@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin extends MerchantEntity {
    public VillagerEntityMixin(EntityType<? extends MerchantEntity> entityType, World world) {
        super(entityType, world);
    }

    @Shadow
    public abstract int getExperience();

    @Shadow public abstract void setOffers(TradeOfferList offers);

    @Inject(method = "beginTradeWith", at=@At("HEAD"))
    private void beginTradeWith(PlayerEntity customer, CallbackInfo ci) {
        if (CarpetFlyingSettings.cycleVillagerTrades) {
            // Don't reroll villagers that already have been traded with.
            if (this.getExperience() > 0)
                return;
            // Reset trades - the rest of the logic will recreate them.
            this.setOffers(null);
        }
    }
}