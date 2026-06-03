package com.thepeeingboyairfryers.washwater.base.common.scheduling.section;

import com.thepeeingboyairfryers.washwater.base.common.flow.CachedFluidRegion;
import com.thepeeingboyairfryers.washwater.base.common.storage.FluidSection;
import com.thepeeingboyairfryers.washwater.base.common.storage.FluidSectionManager;
import com.thepeeingboyairfryers.washwater.util.SwapPair;
import com.thepeeingboyairfryers.washwater.util.parallel.MainThreads;
import it.unimi.dsi.fastutil.shorts.ShortArraySet;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunkSection;

public class FluidTickSection extends CachedFluidRegion {
    private final int x;
    private final int y;
    private final int z;
    private final SwapPair<ShortSet> liveTicks = new SwapPair<>(new ShortArraySet(), new ShortArraySet());
    private final FluidSection[] fluidSections = new FluidSection[8];
    private final LevelChunkSection[] blockSections = new LevelChunkSection[8];
    private TickTracker tickTracker = null;
    private int age;
    private int random;

    public FluidTickSection(int iX, int iY, int iZ) {
        this.x = iX;
        this.y = iY;
        this.z = iZ;
    }

    public void tick(FluidTickingContext ctx) {
        for (int j = 0; j < 8; j++) {
            if (fluidSections[j] == null) continue;
            fluidSections[j].acquire();
            blockSections[j].acquire();
        }

        tickTracker = ctx.makeTickTracker(tickTracker, x, y, z, liveTicks.getOther());

        try {
            for (short s : liveTicks.getCurrent()) {
                int xW = FluidSection.short2localX(s) + 8 + (x << 4);
                int yW = FluidSection.short2localY(s) + 8 + (y << 4);
                int zW = FluidSection.short2localZ(s) + 8 + (z << 4);
                ctx.tickFluid(this, xW, yW, zW, getFluids(xW, yW, zW), random);
            }
        } finally {
            for (int j = 0; j < 8; j++) {
                if (blockSections[j] == null) continue;
                blockSections[j].release();
                fluidSections[j].release();
            }
        }

        liveTicks.swap();
        liveTicks.getOther().clear();

        tickTracker.apply();
    }

    public void addLiveTick(int xW, int yW, int zW) {
        liveTicks.getCurrent().add(FluidSection.localPos2Short(xW - 8, yW - 8, zW - 8));
    }

    public void removeLiveTick(int xW, int yW, int zW) {
        liveTicks.getCurrent().remove(FluidSection.localPos2Short(xW - 8, yW - 8, zW - 8));
    }

    public void addLaterTick(int xW, int yW, int zW) {
        liveTicks.getOther().add(FluidSection.localPos2Short(xW - 8, yW - 8, zW - 8));
    }

    public void removeLaterTick(int xW, int yW, int zW) {
        liveTicks.getOther().remove(FluidSection.localPos2Short(xW - 8, yW - 8, zW - 8));
    }

    public void fetchSections(Level level) {
        assert MainThreads.isServerThread();

        random = level.random.nextInt();
        tickTracker = null; // Reset

        for (int xO = 0; xO < 2; xO++) {
            for (int zO = 0; zO < 2; zO++) {
                var chunk = level.getChunk(x + xO, z + zO);
                var attachment = FluidSectionManager.getAttachmentFor(chunk);
                for (int yO = 0; yO < 2; yO++) {
                    int i = xO * 4 + yO * 2 + zO;
                    int yS = yO + y;
                    if (yS < chunk.getMinSection()) continue;
                    fluidSections[i] = attachment.getSectionWithY(yS);
                    blockSections[i] = chunk.getSection(chunk.getSectionIndexFromSectionY(yS));
                }
            }
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    @Override
    protected void toBeTicked(int xW, int yW, int zW) {
        tickTracker.toBeTicked(xW, yW, zW);
    }

    @Override
    protected void toBeUnticked(int xW, int yW, int zW) {
        tickTracker.toBeUnticked(xW, yW, zW);
    }

    @Override
    protected LevelChunkSection getBlockSection(int xS, int yS, int zS) {
        return blockSections[(xS - this.x) * 4 + (yS - this.y) * 2 + (zS - this.z)];
    }

    @Override
    protected FluidSection getFluidSection(int xS, int yS, int zS) {
        return fluidSections[(xS - this.x) * 4 + (yS - this.y) * 2 + (zS - this.z)];
    }

    public int getAge() {
        return age;
    }

    public void setAge(int iAge) {
        this.age = iAge;
    }

    public static int getPhase(int x, int y, int z) {
        return (Math.abs(x) % 2 | (Math.abs(y) % 2 << 1) | (Math.abs(z) % 2 << 2));
    }

    public static int getSectionCoord(int w) {
        return (w - 8) >> 4;
    }

    @Override
    public int hashCode() {
        return x ^ y ^ z;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
