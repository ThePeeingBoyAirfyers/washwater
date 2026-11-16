package com.thepeeingboyairfryers.washwater.base.client.fluid_rendering.impl;

import com.thepeeingboyairfryers.washwater.ducks.ILevelSliceFluids;
import net.caffeinemc.mods.sodium.api.util.ColorARGB;
import net.caffeinemc.mods.sodium.client.model.color.ColorProvider;
import net.caffeinemc.mods.sodium.client.model.color.ColorProviderRegistry;
import net.caffeinemc.mods.sodium.client.model.light.LightMode;
import net.caffeinemc.mods.sodium.client.model.light.LightPipelineProvider;
import net.caffeinemc.mods.sodium.client.model.light.data.QuadLightData;
import net.caffeinemc.mods.sodium.client.model.quad.ModelQuad;
import net.caffeinemc.mods.sodium.client.model.quad.properties.ModelQuadFacing;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.ChunkBuildBuffers;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.buffers.ChunkModelBuilder;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.material.DefaultMaterials;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.material.Material;
import net.caffeinemc.mods.sodium.client.render.chunk.translucent_sorting.TranslucentGeometryCollector;
import net.caffeinemc.mods.sodium.client.render.chunk.vertex.builder.ChunkMeshBufferBuilder;
import net.caffeinemc.mods.sodium.client.render.chunk.vertex.format.ChunkVertexEncoder;
import net.caffeinemc.mods.sodium.client.world.LevelSlice;
import net.caffeinemc.mods.sodium.neoforge.render.ForgeColorProviders;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.textures.FluidSpriteCache;

public class FluidRenderingState {
    private final QuadLightData quadLightData = new QuadLightData();
    private final ModelQuad quad = new ModelQuad();
    private final ChunkVertexEncoder.Vertex[] vertices = ChunkVertexEncoder.Vertex.uninitializedQuad();
    private final BlockPos.MutableBlockPos scratchPos = new BlockPos.MutableBlockPos();
    private final BlockPos.MutableBlockPos scratchPos2 = new BlockPos.MutableBlockPos();
    private final ColorProviderRegistry colorRegistry;
    private final LightPipelineProvider lightPipelineProvider;
    private final int[] quadColors = new int[4];
    private Material material;
    private LevelSlice level;
    private int x, y, z;
    private BlockState blockState;
    private FluidState fluidState;
    private ColorProvider<FluidState> colorProvider;
    private ChunkModelBuilder builder;
    private TranslucentGeometryCollector collector;
    private TextureAtlasSprite[] sprites;
    private BlockPos offset;

    public FluidRenderingState(ColorProviderRegistry iColorRegistry, LightPipelineProvider iLightPipelineProvider) {
        this.colorRegistry = iColorRegistry;
        this.lightPipelineProvider = iLightPipelineProvider;
    }

    @SuppressWarnings("checkstyle:ParameterNumber")
    public void configure(
            int iX, int iY, int iZ,
            BlockPos iOffset,
            LevelSlice iLevel,
            BlockState iBlockState,
            FluidState iFluidState,
            TranslucentGeometryCollector iCollector,
            ChunkBuildBuffers buffers
    ) {
        this.x = iX;
        this.y = iY;
        this.z = iZ;
        this.level = iLevel;
        this.blockState = iBlockState;
        this.fluidState = iFluidState;
        this.offset = iOffset;

        material = DefaultMaterials.forFluidState(fluidState);
        builder = buffers.get(material);
        collector = iCollector;
        colorProvider = colorRegistry.getColorProvider(fluidState.getType());
        if (colorProvider == null)
            colorProvider = ForgeColorProviders.adapt(IClientFluidTypeExtensions.of(fluidState));

        sprites = FluidSpriteCache.getFluidSprites(level, getBlockPos(), fluidState);
    }

    public void writeQuad(Direction facing) {
        ChunkVertexEncoder.Vertex[] iVertices = this.vertices;
        TextureAtlasSprite sprite = quad.getSprite();

        var lighter = lightPipelineProvider.getLighter(LightMode.SMOOTH);
        var quadFacing = ModelQuadFacing.fromDirection(facing);
        int normal = quadFacing.getPackedAlignedNormal();

        quad.setFaceNormal(normal);

        lighter.calculate(quad,  scratchPos.set(x, y, z), quadLightData, null, facing, false, false);
        colorProvider.getColors(level, scratchPos.set(x, y, z), scratchPos2.set(x, y, z), fluidState, quad, quadColors);

        for (int i = 0; i < 4; ++i) {
            ChunkVertexEncoder.Vertex out = iVertices[i];
            out.x = (float) offset.getX() + quad.getX(i);
            out.y = (float) offset.getY() + quad.getY(i);
            out.z = (float) offset.getZ() + quad.getZ(i);
            out.color = ColorARGB.toABGR(quadColors[i]);
            out.ao = Integer.MAX_VALUE; //this.brightness[i];
            if (sprite == null) {
                out.u = quad.getTexU(i);
                out.v = quad.getTexV(i);
            } else {
                out.u = sprite.getU(quad.getTexU(i));
                out.v = sprite.getV(quad.getTexV(i));
            }
            out.light = this.quadLightData.lm[i];
        }

        if (sprite != null)
            builder.addSprite(sprite);

        if (material.isTranslucent() && collector != null)
            collector.appendQuad(normal, iVertices, quadFacing);

        ChunkMeshBufferBuilder vertexBuffer = builder.getVertexBuffer(quadFacing);
        vertexBuffer.push(iVertices, material);
        quad.setFlags(0);
    }

    public TextureAtlasSprite[] getSprites() {
        return sprites;
    }

    public ColorProviderRegistry getColorRegistry() {
        return colorRegistry;
    }

    public int[] getQuadColors() {
        return quadColors;
    }

    public LightPipelineProvider getLightPipelineProvider() {
        return lightPipelineProvider;
    }

    public ModelQuad getQuad() {
        return quad;
    }

    public BlockPos useScratchPos(int iX, int iY, int iZ) {
        return scratchPos.set(iX, iY, iZ);
    }

    public BlockPos getBlockPos() {
        return scratchPos2.set(x, y, z);
    }

    public QuadLightData getQuadLightData() {
        return quadLightData;
    }

    public ChunkVertexEncoder.Vertex[] getVertices() {
        return vertices;
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

    public BlockState getBlockState() {
        return blockState;
    }

    public FluidState getFluidState() {
        return fluidState;
    }

    public LevelSlice getLevel() {
        return level;
    }

    public ILevelSliceFluids getFluids() {
        return (ILevelSliceFluids) (Object) level;
    }
}
