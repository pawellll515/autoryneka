package pl.rynekklikacz.mixin;

import net.minecraft.class_465;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(class_465.class)
public interface HandledScreenAccessor {
   @Accessor(value = "field_2776", remap = false)
   int rynekklikacz$getX();

   @Accessor(value = "field_2800", remap = false)
   int rynekklikacz$getY();
}
