package eddxample.chunkminimap.mixin;

import eddxample.chunkminimap.Minimap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//@Mixin(ServerPlayNetworkHandler.class)
//public class ExitWorldMixin {
//    @Inject(method = "onDisconnected", at = @At("HEAD"))
//    public void onDisconnected(Text t, CallbackInfo ci) {
//        Minimap.closeMinimap();
//    }
//}
@Mixin(ClientWorld.class)
public class ExitWorldMixin {
    @Inject(method = "disconnect", at = @At("HEAD"))
    public void onDisconnected(CallbackInfo ci) {
        Minimap.closeMinimap();
    }
}
