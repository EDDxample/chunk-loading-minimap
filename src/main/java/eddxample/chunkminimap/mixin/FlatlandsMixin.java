package eddxample.chunkminimap.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FlatChunkGenerator.class)
public class FlatlandsMixin extends ChunkGenerator<FlatChunkGeneratorConfig> {

    public FlatlandsMixin(IWorld iWorld_1, BiomeSource biomeSource_1, FlatChunkGeneratorConfig chunkGeneratorConfig_1) {super(iWorld_1, biomeSource_1, chunkGeneratorConfig_1);}
    public void buildSurface(ChunkRegion chunkRegion, Chunk chunk) {}
    public int getSpawnHeight() {return 0;}
    public int getHeightOnGround(int i, int i1, Heightmap.Type type) {return 0;}
    @Shadow public void populateNoise(IWorld iWorld, Chunk chunk) {}


    @Inject(method = "populateNoise", at = @At("HEAD"), cancellable = true)
    public void f(IWorld world, Chunk c, CallbackInfo ci) {
        BlockState[] states = ((FlatChunkGeneratorConfig)this.config).getLayerBlocks();
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        Heightmap hm1 = c.getHeightmap(Heightmap.Type.OCEAN_FLOOR_WG);
        Heightmap hm2 = c.getHeightmap(Heightmap.Type.WORLD_SURFACE_WG);

        for(int y = 0; y < 4; ++y) {
            int cx = c.getPos().x, cz = c.getPos().z, i = (cx >= -12 && cx <= 12 && cz >= -12 && cz <= 12) ? 0:2;
                i += (cx + cz) % 2 == 0 ? 0:1;

            BlockState state = states[i];

            if (state != null) {
                for(int x = 0; x < 16; ++x) {
                    for(int z = 0; z < 16; ++z) {
                        c.setBlockState(mutable.set(x, y, z), state, false);
                        hm1.trackUpdate(x, y, z, state);
                        hm2.trackUpdate(x, y, z, state);
                    }
                }
            }
        }
        ci.cancel();
    }
}
