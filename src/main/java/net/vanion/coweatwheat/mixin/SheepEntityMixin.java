package net.vanion.coweatwheat.mixin;

import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.registry.Registries;
import net.vanion.coweatwheat.EatFoodGoal;
import net.vanion.coweatwheat.config.FoodConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SheepEntity.class)
public abstract class SheepEntityMixin {
    @Inject(method = "initGoals", at = @At("TAIL"))
    private void addEatFoodGoal(CallbackInfo info) {
        ((MobEntityAccessor) this).getGoalSelector().add(2,
                new EatFoodGoal<>((SheepEntity)(Object) this,
                        item -> FoodConfig.getDroppedFoodIds("SheepEntity")
                                .contains(Registries.ITEM.getId(item.getStack().getItem()).toString())
                )
        );
    }
}
