package br.com.brforgers.mods.disfabric.mixins;

import br.com.brforgers.mods.disfabric.events.ServerChatCallback;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class MixinServerPlayNetworkHandler {
    @Shadow public ServerPlayerEntity player;

    @Inject(method = "onGameMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;method_31277(Ljava/lang/String;Ljava/util/function/Consumer;)V"), cancellable = true)
    private void onGameMessage(ChatMessageC2SPacket packet, CallbackInfo ci) {
        String string = StringUtils.normalizeSpace(packet.getChatMessage());
        Text text = new TranslatableText("chat.type.text", this.player.getDisplayName(), string);
        ServerChatCallback.EVENT.invoker().onServerChat(this.player, string, text);
    }
}
