package eddxample.chunkminimap.mixin;

import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ThreadedAnvilChunkStorage.class)
public interface ChunkAccessorMixin {
    @Accessor("getCurrentChunkHolder") ChunkHolder getCurrentChunkHolder(long l);
}
