package pl.rynekklikacz.mixin;

import net.minecraft.class_2649;
import net.minecraft.class_2653;
import net.minecraft.class_3944;
import net.minecraft.class_634;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pl.rynekklikacz.Klikacz;

@Mixin(class_634.class)
public class PaczkiMixin {
   @Inject(method = "onInventory", at = @At("RETURN"))
   private void rynekklikacz$inventory(class_2649 packet, CallbackInfo ci) {
      Klikacz.aktualizacja(true);
   }

   @Inject(method = "onScreenHandlerSlotUpdate", at = @At("RETURN"))
   private void rynekklikacz$slot(class_2653 packet, CallbackInfo ci) {
      Klikacz.aktualizacja(true);
   }

   @Inject(method = "onOpenScreen", at = @At("RETURN"))
   private void rynekklikacz$open(class_3944 packet, CallbackInfo ci) {
      Klikacz.aktualizacja(false);
   }
}
