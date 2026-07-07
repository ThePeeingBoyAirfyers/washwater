package com.thepeeingboyairfryers.washwater.base.client;

import com.thepeeingboyairfryers.washwater.ducks.ILevelSliceFluids;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.IQuadTransformer;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LilyPadBakedModel extends BakedModelWrapper<BakedModel> {
    private static final ModelProperty<Float> HEIGHT = new ModelProperty<>();

    public LilyPadBakedModel(BakedModel originalModel) {
        super(originalModel);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData extraData, @Nullable RenderType renderType) {
        var quads = super.getQuads(state, side, rand, extraData, renderType);
        Float nHeight = extraData.get(HEIGHT);
        if (nHeight == null) return quads;

        float height = nHeight - 1f;
        var result = new ArrayList<BakedQuad>(quads.size());

        for (BakedQuad quad : quads) {
            int[] from = quad.getVertices();
            int[] to = Arrays.copyOf(from, from.length);
            for (int i = 0; i < 4; i++) {
                int offset = i * IQuadTransformer.STRIDE + IQuadTransformer.POSITION;
                float y = Float.intBitsToFloat(from[offset + 1]);
                to[offset + 1] = Float.floatToRawIntBits(y + height);
            }

            result.add(new BakedQuad(
                    to,
                    quad.getTintIndex(),
                    quad.getDirection(),
                    quad.getSprite(),
                    quad.isShade(),
                    quad.hasAmbientOcclusion())
            );
        }

        return result;
    }

    @Override
    public ModelData getModelData(BlockAndTintGetter level, BlockPos pos, BlockState state, ModelData modelData) {
        var normalResult = super.getModelData(level, pos, state, modelData);
        if (level instanceof ILevelSliceFluids fluids) {
            float height = ((float) fluids.ww€getFluidFor(pos.getX(), pos.getY() - 1, pos.getZ()).getTotalVolume()) / 1000f;
            return normalResult.derive().with(HEIGHT, height).build();
        } else return normalResult;
    }

    public static void modifyBakedModelsEvent(ModelEvent.ModifyBakingResult event) {
        var lilyKey = ModelResourceLocation.vanilla("lily_pad", "");
        event.getModels().compute(lilyKey, (k, lilyModel) -> new LilyPadBakedModel(lilyModel));
    }
}
