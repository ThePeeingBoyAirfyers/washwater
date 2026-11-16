package com.thepeeingboyairfryers.washwater.mixin.common;


import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.fluids.FluidType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;

import static com.thepeeingboyairfryers.washwater.mixin.accessors.EntityAccessor.*;

@Mixin(Entity.class)
public abstract class MixinEntity {

    @Shadow
    RandomSource random;

    @Shadow
    public abstract boolean fireImmune();

    @Shadow
    public abstract void igniteForSeconds(float seconds);

    @Shadow
    public abstract boolean hurt(DamageSource source, float amount);

    @Shadow
    public abstract DamageSources damageSources();

    @Shadow
    public abstract void playSound(SoundEvent sound, float volume, float pitch);

    @Redirect(method = "baseTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;lavaHurt()V")
    )
    private void lavaHurt(Entity entityInstance) {
        if (fireImmune()) return;
        FluidState lavaState = entityInstance.level().getFluidState(entityInstance.getOnPos().above());

        float lavaRatio = lavaState.getAmount() / 8f;
        if (lavaRatio < 0.5f) {
            lavaRatio = Math.max(lavaRatio, 0.25f);
        }
        else lavaRatio = 1f;

        igniteForSeconds(15.0F * lavaRatio);
        if (hurt(damageSources().lava(), 4.0F * lavaRatio)) {
            playSound(SoundEvents.GENERIC_BURN, 0.4F, 2.0F + this.random.nextFloat() * 0.4F);
        }

    }
}



