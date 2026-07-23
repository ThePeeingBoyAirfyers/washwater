package com.thepeeingboyairfryers.washwater.base.common.scheduling;

import com.thepeeingboyairfryers.washwater.base.common.flow.CachedFluidRegion;
import com.thepeeingboyairfryers.washwater.base.common.scheduling.impl.LocalBitPosSet;
import com.thepeeingboyairfryers.washwater.base.common.scheduling.impl.UpdateBlockTask;
import com.thepeeingboyairfryers.washwater.base.common.storage.FluidSection;
import com.thepeeingboyairfryers.washwater.base.common.storage.FluidSectionManager;
import com.thepeeingboyairfryers.washwater.util.SwapPair;
import com.thepeeingboyairfryers.washwater.util.parallel.MainThreads;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.status.ChunkStatus;

public class FluidTickSection extends CachedFluidRegion {
    private final int x;
    private final int y;
    private final int z;
    private final SwapPair<LocalPosSet> liveTicks = new SwapPair<>(new LocalBitPosSet(), new LocalBitPosSet());
    private final FluidSection[] fluidSections = new FluidSection[8];
    private final LevelChunkSection[] blockSections = new LevelChunkSection[8];
    private boolean needsRefresh = false;
    private byte loadedChunks = 0;
    private TickTracker tickTracker = null;
    private FluidTickingContext currentCtx = null;
    private int age;
    private int random;

    public FluidTickSection(int iX, int iY, int iZ) {
        this.x = iX;
        this.y = iY;
        this.z = iZ;
    }

    public static int getPhase(int x, int y, int z) {
        return (Math.abs(x) % 2 | (Math.abs(y) % 2 << 1) | (Math.abs(z) % 2 << 2));
    }

    public static int getSectionCoord(int w) {
        return (w - 8) >> 4;
    }

    public void tick(FluidTickingContext ctx) {
        for (int j = 0; j < 8; j++) {
            if (fluidSections[j] == null) continue;
            fluidSections[j].acquire();
            blockSections[j].acquire();
        }

        currentCtx = ctx;
        tickTracker = ctx.makeTickTracker(tickTracker, x, y, z, liveTicks.getOther());
        resetCache();

        try {
            liveTicks.getCurrent().forEach((xL, yL, zL) -> {
                int xW = xL + 8 + (x << 4);
                int yW = yL + 8 + (y << 4);
                int zW = zL + 8 + (z << 4);
                ctx.tickFluid(this, xW, yW, zW, getFluids(xW, yW, zW), random);
            });
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
        currentCtx = null;
    }

    public void addLiveTick(int xW, int yW, int zW) {
        liveTicks.getCurrent().add(xW - 8, yW - 8, zW - 8);
    }

    public void removeLiveTick(int xW, int yW, int zW) {
        liveTicks.getCurrent().remove(xW - 8, yW - 8, zW - 8);
    }

    public void addLaterTick(int xW, int yW, int zW) {
        liveTicks.getOther().add(xW - 8, yW - 8, zW - 8);
    }

    public void removeLaterTick(int xW, int yW, int zW) {
        liveTicks.getOther().remove(xW - 8, yW - 8, zW - 8);
    }

    public void fetchSections(Level level) {
        assert MainThreads.isServerThread();

        // Reset
        random = level.random.nextInt();
        tickTracker = null;
        for (int i = 0; i < 8; i++) {
            fluidSections[i] = null;
            blockSections[i] = null;
        }

        needsRefresh = false;
        loadedChunks = 0;

        // Populate
        for (int xO = 0; xO < 2; xO++) {
            for (int zO = 0; zO < 2; zO++) {
                var chunk = level.getChunk(x + xO, z + zO, ChunkStatus.FULL, false);
                if (chunk == null) {
                    needsRefresh = true;
                    continue;
                }

                var attachment = FluidSectionManager.getAttachmentFor(chunk);
                for (int yO = 0; yO < 2; yO++) {
                    int i = xO * 4 + yO * 2 + zO;
                    int yS = yO + y;
                    loadedChunks |= (byte) (1 << i);

                    if (yS < chunk.getMinSection()) continue;
                    fluidSections[i] = attachment.getSectionWithY(yS);
                    blockSections[i] = chunk.getSection(chunk.getSectionIndexFromSectionY(yS));
                }
            }
        }
    }

    private boolean isChunkUnloaded(int xS, int yS, int zS) {
        int i = (xS - this.x) * 4 + (yS - this.y) * 2 + (zS - this.z);
        return ((1 << i) & loadedChunks) == 0;
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
        if (isChunkUnloaded(xS, yS, zS)) {
            needsRefresh = true;
            throw new IllegalStateException("Fetching a null section? x: " + xS + " y: " + yS + " z: " + zS);
        }

        return blockSections[(xS - this.x) * 4 + (yS - this.y) * 2 + (zS - this.z)];
    }

    @Override
    protected FluidSection getFluidSection(int xS, int yS, int zS) {
        if (isChunkUnloaded(xS, yS, zS)) {
            needsRefresh = true;
            throw new IllegalStateException("Fetching a null section? x: " + xS + " y: " + yS + " z: " + zS);
        }

        return fluidSections[(xS - this.x) * 4 + (yS - this.y) * 2 + (zS - this.z)];
    }

    @Override
    public void setState(int xB, int yB, int zB, BlockState state) {
        var section = getBlockSection(xB >> 4, yB >> 4, zB >> 4);
        BlockState prev = section.getBlockState(xB & 15, yB & 15, zB & 15);
        section.setBlockState(xB & 15, yB & 15, zB & 15, state, false);

        currentCtx.queueTask(new UpdateBlockTask(xB, yB, zB, prev, state));
    }

    public int getAge() {
        return age;
    }

    public void setAge(int iAge) {
        this.age = iAge;
    }

    @Override
    public int hashCode() {
        return x ^ y ^ z;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    public boolean needsRefetch() {
        return needsRefresh;
    }

    public boolean canTick() {
        return loadedChunks == -1;
    }
}
