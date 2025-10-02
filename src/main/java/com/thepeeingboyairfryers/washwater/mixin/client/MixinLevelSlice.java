package com.thepeeingboyairfryers.washwater.mixin.client;

import com.thepeeingboyairfryers.washwater.common.storage.FluidSection;
import com.thepeeingboyairfryers.washwater.common.storage.FluidSectionManager;
import com.thepeeingboyairfryers.washwater.duck.ILevelSliceFluidSections;
import it.unimi.dsi.fastutil.longs.Long2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.caffeinemc.mods.sodium.client.world.LevelSlice;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LevelSlice.class)
public class MixinLevelSlice implements ILevelSliceFluidSections {

    @Unique
    private final Long2ObjectMap<FluidSection> sections = new Long2ObjectAVLTreeMap<>();

    @Shadow
    @Final
    private ClientLevel level;

    @Override
    public FluidSection ww€getSectionFor(int x, int y, int z) {
        return FluidSectionManager.getIfAbsent(level, sections, x, y, z);
    }
}
