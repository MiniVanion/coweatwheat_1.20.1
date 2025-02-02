package net.vanion.coweatwheat.mixin;

import net.minecraft.entity.passive.CowEntity;
import net.minecraft.registry.Registries;
import net.vanion.coweatwheat.EatFoodGoal;
import net.vanion.coweatwheat.config.FoodConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("unchecked")
@Mixin(CowEntity.class)
public abstract class CowEntityMixin {
    @Inject(method = "initGoals", at = @At("TAIL"))
    private void addEatFoodGoal(CallbackInfo info) {
        CowEntity cow = (CowEntity)(Object)this;
        ((MobEntityAccessor)this).getGoalSelector().add(2,
                new EatFoodGoal<>(cow,
                        item -> FoodConfig.getDroppedFoodIds("CowEntity")
                                .contains(Registries.ITEM.getId(item.getStack().getItem()).toString())
                )
        );
    }
}
