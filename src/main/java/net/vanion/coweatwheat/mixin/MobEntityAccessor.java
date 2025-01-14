package net.vanion.coweatwheat.mixin;

import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.CowEntity;
import net.vanion.coweatwheat.EatWheatGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobEntity.class)
public interface MobEntityAccessor {
    @Accessor("goalSelector")     // "goalSelector" is the *real* field name in MobEntity
    GoalSelector getGoalSelector();
}

