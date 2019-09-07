package eddxample.chunkminimap.mixin;

import com.mojang.datafixers.DataFixer;
import eddxample.chunkminimap.Minimap;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.world.VersionedChunkStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.io.File;

@Mixin(ThreadedAnvilChunkStorage.class)
public abstract class ChunkAccessorMixin extends VersionedChunkStorage implements Minimap.IChunker {

    public ChunkAccessorMixin(File file_1, DataFixer dataFixer_1) { super(file_1, dataFixer_1); }


    @Shadow @Final final Long2ObjectLinkedOpenHashMap<ChunkHolder> currentChunkHolders =  new Long2ObjectLinkedOpenHashMap();
    @Shadow ChunkHolder getCurrentChunkHolder(long l) { return currentChunkHolders.get(l); }



    @Shadow @Final final Long2ObjectLinkedOpenHashMap<ChunkHolder> chunkHolders =  new Long2ObjectLinkedOpenHashMap();
    @Shadow ChunkHolder getChunkHolder(long l) { return chunkHolders.get(l); }

    public ChunkHolder getIt(long l) {
        if (true) return getCurrentChunkHolder(l);
        return getChunkHolder(l);
    }


}
