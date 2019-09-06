package eddxample.chunkminimap.mixin;


import net.minecraft.util.math.ChunkPos;
import net.minecraft.server.QueueingWorldGenerationProgressListener;
import net.minecraft.world.chunk.ChunkStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(QueueingWorldGenerationProgressListener.class)
public class ExampleMixin {

	@Inject(method = "method_17674(Lnet/minecraft/util/math/ChunkPos;Lnet/minecraft/world/chunk/ChunkStatus;)V", at = @At("HEAD"))
	public void setChunkStatus(ChunkPos chunk, ChunkStatus status, CallbackInfo ci) {
		System.out.print("test");
	}
}
