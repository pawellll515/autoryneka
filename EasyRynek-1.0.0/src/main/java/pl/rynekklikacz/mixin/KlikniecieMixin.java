package pl.rynekklikacz.mixin;

import net.minecraft.class_1713;
import net.minecraft.class_1657;
import net.minecraft.class_636;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pl.rynekklikacz.Kalibracja;

@Mixin(class_636.class)
public class KlikniecieMixin {
   @Inject(method = "method_2906", at = @At("HEAD"))
   private void rynekklikacz$przechwycKlikniecie(int syncId, int slotId, int button, class_1713 actionType, class_1657 player, CallbackInfo ci) {
      if (actionType == class_1713.field_7790) {
         Kalibracja.przechwyt(slotId, button);
      }
   }
}
