package com.thepeeingboyairfryers.washwater.client.fluid_rendering;

import com.thepeeingboyairfryers.washwater.common.WaterInfo;
import com.thepeeingboyairfryers.washwater.common.fluids.MultiFluidValue;
import com.thepeeingboyairfryers.washwater.duck.ILevelSliceFluids;
import net.caffeinemc.mods.sodium.api.util.NormI8;
import net.caffeinemc.mods.sodium.client.model.color.ColorProviderRegistry;
import net.caffeinemc.mods.sodium.client.model.light.LightPipelineProvider;
import net.caffeinemc.mods.sodium.client.model.quad.ModelQuad;
import net.caffeinemc.mods.sodium.client.model.quad.properties.ModelQuadFacing;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.ChunkBuildBuffers;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.buffers.ChunkModelBuilder;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.DefaultFluidRenderer;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.FluidRenderer;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.material.DefaultMaterials;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.material.Material;
import net.caffeinemc.mods.sodium.client.render.chunk.translucent_sorting.TranslucentGeometryCollector;
import net.caffeinemc.mods.sodium.client.render.chunk.vertex.builder.ChunkMeshBufferBuilder;
import net.caffeinemc.mods.sodium.client.render.chunk.vertex.format.ChunkVertexEncoder;
import net.caffeinemc.mods.sodium.client.services.PlatformBlockAccess;
import net.caffeinemc.mods.sodium.client.util.DirectionUtil;
import net.caffeinemc.mods.sodium.client.world.LevelSlice;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class WashFluidRenderer extends FluidRenderer {

    private final Logger logger = LoggerFactory.getLogger(WashFluidRenderer.class);
    private final ColorProviderRegistry colorRegistry;
    private final LightPipelineProvider lightPipelineProvider;
    private final DefaultFluidRenderer defaultFluidRenderer;
    private final ModelQuad quad = new ModelQuad();
    private final ChunkVertexEncoder.Vertex[] vertices = ChunkVertexEncoder.Vertex.uninitializedQuad();

    public WashFluidRenderer(ColorProviderRegistry iColorRegistry, LightPipelineProvider iLightPipelineProvider) {
        this.colorRegistry = iColorRegistry;
        this.lightPipelineProvider = iLightPipelineProvider;
        this.defaultFluidRenderer = new DefaultFluidRenderer(lightPipelineProvider);
    }
    
    @Override
    public void render(
            LevelSlice level,
            BlockState blockState,
            FluidState fluidState,
            BlockPos blockPos,
            BlockPos offset,
            TranslucentGeometryCollector collector,
            ChunkBuildBuffers buffers
    ) {
        Material material = DefaultMaterials.forFluidState(fluidState);
        ChunkModelBuilder builder = buffers.get(material);
        IClientFluidTypeExtensions handler = IClientFluidTypeExtensions.of(fluidState);
        ILevelSliceFluids fluids = (ILevelSliceFluids) (Object) level;

        float generalHeight = ((float) fluids.ww€getFluidFor(blockPos.getX(), blockPos.getY(), blockPos.getZ()).getTotalVolume() / WaterInfo.VOLUME_PER_BLOCK);
        float northWestHeight = generalHeight;
        float northEastHeight = generalHeight;
        float southWestHeight = generalHeight;
        float southEastHeight = generalHeight;
        float yOffset = 0.001F;

        //Bottom Face
        setVertex(0, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f);
        setVertex(1, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f);
        setVertex(2, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f);
        setVertex(3, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f);

        quad.setColor(0, 0xFFFFFFFF);
        quad.setColor(1, 0xFFFFFFFF);
        quad.setColor(2, 0xFFFFFFFF);
        quad.setColor(3, 0xFFFFFFFF);
        writeQuad(builder, collector, material, offset, ModelQuadFacing.UNASSIGNED, false);

        //Top Face
        setVertex(0, 0.0f, southWestHeight, 1.0f, 0.0f, 0.0f);
        setVertex(1, 1.0f, southEastHeight, 1.0f, 1.0f, 0.0f);
        setVertex(2, 1.0f, northEastHeight, 0.0f, 1.0f, 1.0f);
        setVertex(3, 0.0f, northWestHeight, 0.0f, 0.0f, 1.0f);

        quad.setColor(0, 0xFFFFFFFF);
        quad.setColor(1, 0xFFFFFFFF);
        quad.setColor(2, 0xFFFFFFFF);
        quad.setColor(3, 0xFFFFFFFF);
        writeQuad(builder, collector, material, offset, ModelQuadFacing.UNASSIGNED, false);

        for(Direction dir : DirectionUtil.HORIZONTAL_DIRECTIONS) {


            float c1;
            float c2;
            float x1;
            float z1;
            float x2;
            float z2;
            switch (dir) {
                case NORTH:
                    c1 = northWestHeight;
                    c2 = northEastHeight;
                    x1 = 0.0F;
                    x2 = 1.0F;
                    z1 = 0.001F;
                    z2 = z1;
                    break;
                case SOUTH:
                    c1 = southEastHeight;
                    c2 = southWestHeight;
                    x1 = 1.0F;
                    x2 = 0.0F;
                    z1 = 0.999F;
                    z2 = z1;
                    break;
                case WEST:
                    c1 = southWestHeight;
                    c2 = northWestHeight;
                    x1 = 0.001F;
                    x2 = x1;
                    z1 = 1.0F;
                    z2 = 0.0F;
                    break;
                case EAST:
                    c1 = northEastHeight;
                    c2 = northWestHeight;
                    x1 = 0.999F;
                    x2 = x1;
                    z1 = 0.0F;
                    z2 = 1.0F;
                    break;
                default:
                    continue;
            }

            float u1 = 1f;
            float u2 = 1f;
            float u3 = 1f;
            float v1 = 1f;
            float v2 = 1f;
            float v3 = 1f;


            setVertex(0, x2, c2, z2, u2, v2);
            setVertex(1, x2, yOffset, z2, u2, v3);
            setVertex(2, x1, yOffset, z1, u1, v3);
            setVertex(3, x1, c1, z1, u1, v1);
            writeQuad(builder, collector, material, offset, ModelQuadFacing.UNASSIGNED, false);

        }


        try {
            logger.info("Rendering block at {} with {}", blockPos, fluids.ww€getFluidFor(blockPos.getX(), blockPos.getY(), blockPos.getZ()).getTotalVolume());
        } catch (Exception e) {
            logger.error("Exception while rendering,", e);
        }
    }

    private void debugRender(ChunkModelBuilder builder, MultiFluidValue value, TranslucentGeometryCollector collector, Material material, BlockPos offset) {
        setVertex(0, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
        setVertex(1, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f);
        setVertex(2, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f);
        setVertex(3, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f);



        writeQuad(builder, collector, material, offset, ModelQuadFacing.UNASSIGNED, false);
    }

    private void writeQuad(ChunkModelBuilder builder, TranslucentGeometryCollector collector, Material material, BlockPos offset, ModelQuadFacing facing, boolean flip) {
        ChunkVertexEncoder.Vertex[] iVertices = this.vertices;

        for (int i = 0; i < 4; ++i) {
            ChunkVertexEncoder.Vertex out = iVertices[flip ? 3 - i + 1 & 3 : i];
            out.x = (float) offset.getX() + quad.getX(i);
            out.y = (float) offset.getY() + quad.getY(i);
            out.z = (float) offset.getZ() + quad.getZ(i);
            out.u = quad.getTexU(i);
            out.v = quad.getTexV(i);
        }

        TextureAtlasSprite sprite = quad.getSprite();
        if (sprite != null) {
            builder.addSprite(sprite);
        }

        if (material.isTranslucent() && collector != null) {
            int normal;
            if (facing.isAligned()) {
                normal = facing.getPackedAlignedNormal();
            } else {
                normal = quad.getFaceNormal();
            }

            if (flip) {
                normal = NormI8.flipPacked(normal);
            }

            collector.appendQuad(normal, iVertices, facing);
        }

        ChunkMeshBufferBuilder vertexBuffer = builder.getVertexBuffer(facing);
        vertexBuffer.push(iVertices, material);
    }

    private void setVertex(int i, float x, float y, float z, float u, float v) {
        quad.setX(i, x);
        quad.setY(i, y);
        quad.setZ(i, z);
        quad.setTexU(i, u);
        quad.setTexV(i, v);
    }

}
