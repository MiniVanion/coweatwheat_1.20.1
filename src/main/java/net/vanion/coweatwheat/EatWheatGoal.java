package net.vanion.coweatwheat;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.EnumSet;
import java.util.List;

public class EatWheatGoal extends Goal {
    private final CowEntity cow;
    private ItemEntity targetWheat;

    public EatWheatGoal(CowEntity cow) {
        this.cow = cow;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        List<ItemEntity> nearbyItems = cow.getWorld().getEntitiesByClass(
                ItemEntity.class,
                new Box(cow.getBlockPos()).expand(10),
                item -> item.getStack().getItem() == Items.WHEAT
        );

        if (!cow.isInLove() && !nearbyItems.isEmpty()) {
            targetWheat = nearbyItems.get(0);
            System.out.println("Debug: Found wheat at " + targetWheat.getBlockPos() + " for cow at " + cow.getBlockPos());
            return true;
        }
        return false;
    }

    @Override
    public void start() {
        if (targetWheat != null) {
            cow.getNavigation().startMovingTo(targetWheat, 1.0);
            System.out.println("Debug: Cow started moving to wheat at " + targetWheat.getBlockPos());
        }
    }

    @Override
    public void tick() {
        if (targetWheat != null && targetWheat.isAlive()) {
            double distance = cow.squaredDistanceTo(targetWheat);
            System.out.println("Debug: Cow is " + Math.sqrt(distance) + " blocks away from wheat.");

            if (distance < 2.0) {
                // Consume the wheat
                targetWheat.getStack().decrement(1);
                if (targetWheat.getStack().isEmpty()) {
                    targetWheat.discard();
                }
                System.out.println("Debug: Cow consumed wheat at " + targetWheat.getBlockPos());
                if (!cow.isInLove() && cow.getBreedingAge() == 0) {
                    // Method #1: If you just want it in love (no associated player):
                    ((AnimalEntity) cow).setLoveTicks(600);
                    World world = cow.getWorld();
                    if (!world.isClient && world instanceof ServerWorld serverWorld) {
                        // Spawn hearts for all clients
                        for (int i = 0; i < 7; i++) {
                            double dx = (world.random.nextGaussian() * 0.1D);
                            double dy = (world.random.nextGaussian() * 0.1D);
                            double dz = (world.random.nextGaussian() * 0.1D);
                            serverWorld.spawnParticles(
                                    ParticleTypes.HEART,
                                    cow.getX(),
                                    cow.getBodyY(0.5D),
                                    cow.getZ(),
                                    1,
                                    0.3D, 0.3D, 0.3D, // spread
                                    0.0D             // speed
                            );
                        }
                    }

                }
                stop();
            } else {
                cow.getNavigation().startMovingTo(targetWheat, 1.0);
            }
        }
    }

    @Override
    public boolean shouldContinue() {
        return targetWheat != null && targetWheat.isAlive();
    }

    @Override
    public void stop() {
        System.out.println("Debug: Cow stopped interacting with wheat.");
        targetWheat = null;
    }
}
