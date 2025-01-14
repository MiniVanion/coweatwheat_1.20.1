package net.vanion.coweatwheat.mixin;

import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.passive.CowEntity;
import net.vanion.coweatwheat.EatWheatGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CowEntity.class)
public abstract class CowEntityMixin {

    // We do NOT Shadow here because the field doesn't exist in CowEntity.

    @Inject(method = "initGoals", at = @At("TAIL"))
    private void addEatWheatGoal(CallbackInfo info) {
        // Accessing the real goalSelector field through the accessor:
        ((MobEntityAccessor) this).getGoalSelector().add(2, new EatWheatGoal((CowEntity)(Object) this));
    }
}