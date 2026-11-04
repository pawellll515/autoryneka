package pl.rynekklikacz.mixin;

import net.minecraft.class_465;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(class_465.class)
public interface HandledScreenAccessor {
   @Accessor("x")
   int rynekklikacz$getX();

   @Accessor("y")
   int rynekklikacz$getY();
}
