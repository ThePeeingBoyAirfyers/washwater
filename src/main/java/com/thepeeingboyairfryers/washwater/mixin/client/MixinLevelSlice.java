package com.thepeeingboyairfryers.washwater.mixin.client;

import com.thepeeingboyairfryers.washwater.base.common.fluids.MultiFluidValue;
import com.thepeeingboyairfryers.washwater.base.common.storage.FluidSection;
import com.thepeeingboyairfryers.washwater.base.common.storage.FluidSectionManager;
import com.thepeeingboyairfryers.washwater.ducks.ILevelSliceFluids;
import net.caffeinemc.mods.sodium.client.world.LevelSlice;
import net.caffeinemc.mods.sodium.client.world.cloned.ChunkRenderContext;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;


@Mixin(LevelSlice.class)
public abstract class MixinLevelSlice implements ILevelSliceFluids {

    @Shadow
    @Final
    private static int SECTION_ARRAY_SIZE;
    @Shadow
    @Final
    private static int SECTION_ARRAY_LENGTH;
    @Unique
    private final MultiFluidValue[][] ww€fluids = new MultiFluidValue[SECTION_ARRAY_SIZE][4096];
    @Shadow
    @Final
    private ClientLevel level;
    @Shadow
    private BoundingBox volume;
    @Shadow
    private int originBlockX;
    @Shadow
    private int originBlockY;
    @Shadow
    private int originBlockZ;

    @Shadow
    public static int getLocalSectionIndex(int sectionX, int sectionY, int sectionZ) {
        throw new AssertionError();
    }

    @Shadow
    public static int getLocalBlockIndex(int blockX, int blockY, int blockZ) {
        throw new AssertionError();
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    void ww€init(ClientLevel iLevel, CallbackInfo ci) {
        for (var fluids : ww€fluids) {
            Arrays.fill(fluids, MultiFluidValue.EMPTY);
        }
    }

    @Inject(method = "copySectionData", at = @At("HEAD"))
    void ww€copyFluidSection(ChunkRenderContext context, int sectionIndex, CallbackInfo ci) {
        int x = sectionIndex % SECTION_ARRAY_LENGTH;
        int sd = (sectionIndex - x) / SECTION_ARRAY_LENGTH;
        int z = sd % SECTION_ARRAY_LENGTH;
        int y = (sd - z) / SECTION_ARRAY_LENGTH;
        LevelChunk chunk = level.getChunk(context.getOrigin().x() + x - 1, context.getOrigin().z() + z - 1);
        ww€writeFluids(ww€fluids[sectionIndex], FluidSectionManager.getAttachmentFor(chunk).getSectionWithY(context.getOrigin().y() + y - 1));
    }

    @Unique
    private void ww€writeFluids(MultiFluidValue[] fluids, FluidSection section) {
        if (section == null) return;

        if (section.isEmpty()) {
            Arrays.fill(fluids, MultiFluidValue.EMPTY);
        } else section.fill(fluids);
    }

    @Override
    public @NotNull MultiFluidValue ww€getFluidFor(int x, int y, int z) {
        if (!this.volume.isInside(x, y, z)) {
            return MultiFluidValue.EMPTY;
        } else {
            int relBlockX = x - this.originBlockX;
            int relBlockY = y - this.originBlockY;
            int relBlockZ = z - this.originBlockZ;
            return this.ww€fluids
                    [getLocalSectionIndex(relBlockX >> 4, relBlockY >> 4, relBlockZ >> 4)]
                    [FluidSection.localPos2Short(relBlockX & 15, relBlockY & 15, relBlockZ & 15)];
        }
    }
}
