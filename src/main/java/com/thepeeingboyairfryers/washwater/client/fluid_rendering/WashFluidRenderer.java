package com.thepeeingboyairfryers.washwater.client.fluid_rendering;

import com.thepeeingboyairfryers.washwater.common.fluids.FluidManager;
import com.thepeeingboyairfryers.washwater.common.fluids.MultiFluidValue;
import com.thepeeingboyairfryers.washwater.duck.ILevelSliceFluids;
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
import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.FluidRenderer;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.material.DefaultMaterials;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.material.Material;
import net.caffeinemc.mods.sodium.client.render.chunk.translucent_sorting.TranslucentGeometryCollector;
import net.caffeinemc.mods.sodium.client.render.chunk.vertex.builder.ChunkMeshBufferBuilder;
import net.caffeinemc.mods.sodium.client.render.chunk.vertex.format.ChunkVertexEncoder;
import net.caffeinemc.mods.sodium.client.world.LevelSlice;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.client.textures.FluidSpriteCache;


public class WashFluidRenderer extends FluidRenderer {

    private final WaterSurfaceHandler handler = new WaterSurfaceHandler();
    private final QuadLightData quadLightData = new QuadLightData();
    private final ModelQuad quad = new ModelQuad();
    private final ChunkVertexEncoder.Vertex[] vertices = ChunkVertexEncoder.Vertex.uninitializedQuad();
    private final BlockPos.MutableBlockPos scratchPos = new BlockPos.MutableBlockPos();
    private final ColorProviderRegistry colorRegistry;
    private final LightPipelineProvider lightPipelineProvider;
    private ChunkModelBuilder builder;
    private TranslucentGeometryCollector collector;

    public WashFluidRenderer(ColorProviderRegistry iColorRegistry, LightPipelineProvider iLightPipelineProvider) {
        this.colorRegistry = iColorRegistry;
        this.lightPipelineProvider = iLightPipelineProvider;
    }

    @Override
    public void render(
            LevelSlice level,
            BlockState blockState,
            FluidState fluidState,
            BlockPos blockPos,
            BlockPos offset,
            TranslucentGeometryCollector iCollector,
            ChunkBuildBuffers buffers
    ) {
        ILevelSliceFluids fluids = (ILevelSliceFluids) (Object) level;
        MultiFluidValue value = fluids.ww€getFluidFor(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        if (value.size() != 1) throw new IllegalArgumentException();
        MultiFluidValue.Entry entry = value.iterator().next();
        fluidState = FluidManager.dropinFluidState(entry.fluidType());

        Material material = DefaultMaterials.forFluidState(fluidState);
        this.builder = buffers.get(material);
        this.collector = iCollector;
        handler.configure(level, blockPos, blockState, fluidState);
        TextureAtlasSprite[] sprites = FluidSpriteCache.getFluidSprites(level, blockPos, fluidState);

        quad.setSprite(sprites[0]);

        for (Direction dir : Direction.values()) {
            if (handler.configureFace(dir, quad)) {
                writeQuad(material, blockPos, offset, dir, level, fluidState);
            }
        }
    }

    private void writeQuad(Material material, BlockPos realPos, BlockPos offset, Direction facing, LevelSlice level, FluidState fluidState) {
        ChunkVertexEncoder.Vertex[] iVertices = this.vertices;
        var lighter = lightPipelineProvider.getLighter(LightMode.SMOOTH);
        var quadFacing = ModelQuadFacing.fromDirection(facing);
        int normal = quadFacing.getPackedAlignedNormal();
        TextureAtlasSprite sprite = quad.getSprite();
        quad.setFaceNormal(normal);
        lighter.calculate(quad, realPos, quadLightData, null, facing, false, false);

        int[] quadColors = new int[4];
        ColorProvider<FluidState> colorProvider = colorRegistry.getColorProvider(fluidState.getType());
        colorProvider.getColors(level, realPos, scratchPos.set(realPos), fluidState, quad, quadColors);

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

}
