package net.vanion.coweatwheat.mixin;

import net.minecraft.block.CropBlock;
import net.minecraft.state.property.IntProperty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(CropBlock.class)
public interface CropBlockInvoker {
    @Invoker("getAgeProperty")
    IntProperty invokeGetAgeProperty();
}
