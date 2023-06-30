package pl.kosma.carpetflying.mixin;

import net.minecraft.server.network.ServerPlayerEntity;

public interface ThreadedAnvilChunkStorageAccessor {
    int getViewDistanceForPlayer(ServerPlayerEntity player);
}
