package pl.kosma.carpetflying.mixin;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.util.math.ChunkPos;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pl.kosma.carpetflying.CarpetFlyingSettings;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

@Mixin(ThreadedAnvilChunkStorage.class)
public abstract class ThreadedAnvilChunkStorageMixin implements ThreadedAnvilChunkStorageAccessor {
    /** The original (globally configured) view distance. */
    @Shadow
    int watchDistance;

    /** The custom (per player) view distance. */
    final private Map<ServerPlayerEntity, Integer> perPlayerViewDistance = new WeakHashMap<>();

    public int getViewDistanceForPlayer(ServerPlayerEntity player) {
        if (CarpetFlyingSettings.perPlayerViewDistance) {
            return perPlayerViewDistance.getOrDefault(player, this.watchDistance);
        } else {
            return this.watchDistance;
        }
    }

    /*
     * Save the per-player render distance whenever a player is registered to TACS.
     * Remove the value whenever the player is unregistered.
     */

    @Inject(method = "handlePlayerAddedOrRemoved", at = @At(value = "HEAD"))
    void handlePlayerAddedOrRemoved_HEAD(ServerPlayerEntity player, boolean added, CallbackInfo ci) {
        if (added)
            perPlayerViewDistance.put(player, 4 /* TODO: player.getViewDistance */);
    }

    @Inject(method = "handlePlayerAddedOrRemoved", at = @At(value = "TAIL"))
    void handlePlayerAddedOrRemoved_TAIL(ServerPlayerEntity player, boolean added, CallbackInfo ci) {
        if (!added)
            perPlayerViewDistance.remove(player);
    }

    /*
     * Redirect all read access to this.watchDistance to our logic.
     */

    @Redirect(
            method = "handlePlayerAddedOrRemoved",
            at = @At(value = "FIELD", target = "Lnet/minecraft/server/world/ThreadedAnvilChunkStorage;watchDistance:I", opcode = Opcodes.GETFIELD),
            require = 5, allow = 5
    )
    int handlePlayerAddedOrRemoved_watchDistance(ThreadedAnvilChunkStorage instance, ServerPlayerEntity player) {
        return getViewDistanceForPlayer(player);
    }

    @Redirect(
            method = "updatePosition",
            at = @At(value = "FIELD", target = "Lnet/minecraft/server/world/ThreadedAnvilChunkStorage;watchDistance:I", opcode = Opcodes.GETFIELD),
            require = 5, allow = 5
    )
    int updatePosition_watchDistance(ThreadedAnvilChunkStorage instance, ServerPlayerEntity player) {
        return getViewDistanceForPlayer(player);
    }

    @Redirect(
            method = "getPlayersWatchingChunk(Lnet/minecraft/util/math/ChunkPos;Z)Ljava/util/List;",
            at = @At(value = "FIELD", target = "Lnet/minecraft/server/world/ThreadedAnvilChunkStorage;watchDistance:I", opcode = Opcodes.GETFIELD),
            require = 2, allow = 2
    )
    int getPlayersWatchingChunk_watchDistance(ThreadedAnvilChunkStorage instance) {
        return getViewDistanceForPlayer(getPlayersWatchingChunk_currentPlayer.get());
    }

    /*
     * Kludge: getPlayersWatchingChunk has a "for" loop iterating over players.
     * Save the iterator value to TLS so that the function above can access it.
     *
     * Kudos to LlamaLad7 for teaching me how to do this. Check out his library
     * which can accomplish this better and without having to jump through hoops:
     * https://github.com/LlamaLad7/MixinExtras
     */

    @Unique
    final private static ThreadLocal<ServerPlayerEntity> getPlayersWatchingChunk_currentPlayer = new ThreadLocal<>();

    @ModifyVariable(method = "getPlayersWatchingChunk(Lnet/minecraft/util/math/ChunkPos;Z)Ljava/util/List;", at = @At("STORE"), ordinal = 0)
    ServerPlayerEntity getPlayersWatchingChunk_for_player(ServerPlayerEntity player) {
        getPlayersWatchingChunk_currentPlayer.set(player);
        return player;
    }

    @Inject(method = "getPlayersWatchingChunk(Lnet/minecraft/util/math/ChunkPos;Z)Ljava/util/List;", at = @At(value = "TAIL"))
    void getPlayersWatchingChunk_TAIL(ChunkPos chunkPos, boolean onlyOnWatchDistanceEdge, CallbackInfoReturnable<List<ServerPlayerEntity>> cir) {
        getPlayersWatchingChunk_currentPlayer.set(null);
    }

    @Mixin(ThreadedAnvilChunkStorage.EntityTracker.class)
    static class EntityTrackerMixin {
        @Redirect(
                method = "updateTrackedStatus(Lnet/minecraft/server/network/ServerPlayerEntity;)V",
                at = @At(value = "FIELD", target = "Lnet/minecraft/server/world/ThreadedAnvilChunkStorage;watchDistance:I", opcode = Opcodes.GETFIELD),
                require = 1, allow = 1
        )
        int updateTrackedStatus_watchDistance(ThreadedAnvilChunkStorage instance, ServerPlayerEntity player) {
            return ((ThreadedAnvilChunkStorageAccessor) instance).getViewDistanceForPlayer(player);
        }
    }
}
