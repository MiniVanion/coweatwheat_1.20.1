package net.vanion.coweatwheat.mixin;

import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.registry.Registries;
import net.vanion.coweatwheat.EatFoodGoal;
import net.vanion.coweatwheat.config.FoodConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("unchecked")
@Mixin(ChickenEntity.class)
public abstract class ChickenEntityMixin {
    @Inject(method = "initGoals", at = @At("TAIL"))
    private void addEatFoodGoal(CallbackInfo info) {
        ChickenEntity chicken = (ChickenEntity)(Object)this;
        ((MobEntityAccessor)this).getGoalSelector().add(2,
                new EatFoodGoal<>(chicken,
                        item -> FoodConfig.getDroppedFoodIds("ChickenEntity")
                                .contains(Registries.ITEM.getId(item.getStack().getItem()).toString())
                )
        );
    }
}

