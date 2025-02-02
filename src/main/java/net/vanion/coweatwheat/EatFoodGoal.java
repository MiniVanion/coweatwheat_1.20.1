package net.vanion.coweatwheat;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.vanion.coweatwheat.config.FoodConfig;
import net.vanion.coweatwheat.mixin.CropBlockInvoker;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public class EatFoodGoal<T extends AnimalEntity> extends Goal {
    protected final T mob;
    protected ItemEntity targetFoodItem;
    protected BlockPos targetCrop;
    protected final Predicate<ItemEntity> foodPredicate;
    protected final int searchRadius = 10;
    protected final double speed = 1.0;

    public EatFoodGoal(T mob, Predicate<ItemEntity> foodPredicate) {
        this.mob = mob;
        this.foodPredicate = foodPredicate;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        // Prevent animals from eating if they are already in love.
        if (mob.isInLove()) {
            return false;
        }

        World world = mob.getWorld();

        // 1. Look for dropped food items.
        List<ItemEntity> nearbyItems = world.getEntitiesByClass(
                ItemEntity.class,
                new Box(mob.getBlockPos()).expand(searchRadius),
                foodPredicate
        );
        if (!nearbyItems.isEmpty()) {
            targetFoodItem = nearbyItems.get(0);
            return true;
        }

        // 2. Look for fully grown crop blocks if a valid crop is configured.
        String cropId = FoodConfig.getCropBlockId(mob.getClass().getSimpleName());
        Block expectedCropBlock = null;
        if (cropId != null) {
            expectedCropBlock = Registries.BLOCK.get(new Identifier(cropId));
            if (expectedCropBlock == Blocks.AIR) {
                expectedCropBlock = null;
            }
        }

        if (expectedCropBlock != null) {
            BlockPos mobPos = mob.getBlockPos();
            BlockPos.Mutable mutablePos = new BlockPos.Mutable();
            double closestDistance = Double.MAX_VALUE;

            for (int x = mobPos.getX() - searchRadius; x <= mobPos.getX() + searchRadius; x++) {
                for (int y = mobPos.getY() - 2; y <= mobPos.getY() + 2; y++) {
                    for (int z = mobPos.getZ() - searchRadius; z <= mobPos.getZ() + searchRadius; z++) {
                        mutablePos.set(x, y, z);
                        BlockState state = world.getBlockState(mutablePos);
                        // Only consider CropBlocks.
                        if (state.getBlock() instanceof CropBlock cropBlock) {
                            // Only accept blocks matching the configured crop.
                            if (!state.getBlock().equals(expectedCropBlock)) {
                                continue;
                            }
                            // Use the CropBlockInvoker to get the age property.
                            IntProperty ageProperty = ((CropBlockInvoker) cropBlock).invokeGetAgeProperty();
                            int age = state.get(ageProperty);
                            if (age == cropBlock.getMaxAge()) {
                                double distanceSq = mob.squaredDistanceTo(x + 0.5, y + 0.5, z + 0.5);
                                if (distanceSq < closestDistance) {
                                    closestDistance = distanceSq;
                                    mutablePos.toImmutable();
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void start() {
        if (targetFoodItem != null) {
            mob.getNavigation().startMovingTo(targetFoodItem, speed);
        } else if (targetCrop != null) {
            mob.getNavigation().startMovingTo(targetCrop.getX() + 0.5, targetCrop.getY(), targetCrop.getZ() + 0.5, speed);
        }
    }

    @Override
    public void tick() {
        World world = mob.getWorld();

        if (targetFoodItem != null && targetFoodItem.isAlive()) {
            double distanceSq = mob.squaredDistanceTo(targetFoodItem);
            if (distanceSq < 2.0) {
                ItemStack stack = targetFoodItem.getStack();
                stack.decrement(1);
                if (stack.isEmpty()) {
                    targetFoodItem.discard();
                }
                spawnLoveParticles();
                stop();
            } else {
                mob.getNavigation().startMovingTo(targetFoodItem, speed);
            }
        } else if (targetCrop != null) {
            double distanceSq = mob.squaredDistanceTo(targetCrop.getX() + 0.5, targetCrop.getY(), targetCrop.getZ() + 0.5);
            if (distanceSq < 2.0) {
                BlockState cropState = world.getBlockState(targetCrop);
                // Optionally, you can log the block to verify what it is.
                System.out.println("Debug: Crop at " + targetCrop + " is " + cropState.getBlock());

                // Even if the block isn’t recognized as a CropBlock, proceed.
                // If needed, you can check for specific block IDs instead.
                world.breakBlock(targetCrop, false);
                BlockPos below = targetCrop.down();
                BlockState belowState = world.getBlockState(below);
                if (belowState.getBlock() == Blocks.FARMLAND) {
                    world.setBlockState(below, Blocks.DIRT.getDefaultState());
                }
                // Always trigger love particles for crop consumption.
                spawnLoveParticles();
                stop();
            } else {
                mob.getNavigation().startMovingTo(targetCrop.getX() + 0.5, targetCrop.getY(), targetCrop.getZ() + 0.5, speed);
            }
        }
    }


    @Override
    public boolean shouldContinue() {
        if (targetFoodItem != null) {
            return targetFoodItem.isAlive();
        }
        if (targetCrop != null) {
            BlockState state = mob.getWorld().getBlockState(targetCrop);
            if (state.getBlock() instanceof CropBlock cropBlock) {
                IntProperty ageProperty = ((CropBlockInvoker) cropBlock).invokeGetAgeProperty();
                int age = state.get(ageProperty);
                return age == cropBlock.getMaxAge();
            }
        }
        return false;
    }

    @Override
    public void stop() {
        targetFoodItem = null;
        targetCrop = null;
    }

    private void spawnLoveParticles() {
        if (!mob.isInLove() && mob.getBreedingAge() == 0) {
            mob.setLoveTicks(600);
            World world = mob.getWorld();
            if (!world.isClient && world instanceof ServerWorld serverWorld) {
                for (int i = 0; i < 7; i++) {
                    serverWorld.spawnParticles(
                            ParticleTypes.HEART,
                            mob.getX(),
                            mob.getBodyY(0.5D),
                            mob.getZ(),
                            1,
                            0.3D, 0.3D, 0.3D,
                            0.0D
                    );
                }
            }
        }
    }
}
