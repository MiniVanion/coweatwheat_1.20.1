package net.vanion.coweatwheat.mixin;

import net.minecraft.entity.passive.PigEntity;
import net.minecraft.registry.Registries;
import net.vanion.coweatwheat.EatFoodGoal;
import net.vanion.coweatwheat.config.FoodConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("unchecked")
@Mixin(PigEntity.class)
public abstract class PigEntityMixin {
    @Inject(method = "initGoals", at = @At("TAIL"))
    private void addEatFoodGoal(CallbackInfo info) {
        PigEntity pig = (PigEntity)(Object)this;
        ((MobEntityAccessor)this).getGoalSelector().add(2,
                new EatFoodGoal<>(pig,
                        item -> FoodConfig.getDroppedFoodIds("PigEntity")
                                .contains(Registries.ITEM.getId(item.getStack().getItem()).toString())
                )
        );
    }
}
