package com.thepeeingboyairfryers.washwater.common.item;

import com.thepeeingboyairfryers.washwater.common.WaterInfo;
import com.thepeeingboyairfryers.washwater.common.component.ModDataComponentTypes;
import com.thepeeingboyairfryers.washwater.common.fluids.FluidUtil;
import com.thepeeingboyairfryers.washwater.common.fluids.MultiFluidValue;
import com.thepeeingboyairfryers.washwater.common.nbtUtil.DataComponentUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidType;

import java.util.List;

public class PrecisionBucketItem extends Item {

    static int maxFillLevel = 1000;

    public PrecisionBucketItem(Item.Properties properties) {
        super(properties);
    }

    public InteractionResult useOn(UseOnContext useOnContext) {
        Level level = useOnContext.getLevel();
        Player player = useOnContext.getPlayer();
        ItemStack itemStack = useOnContext.getItemInHand();
        BlockPos targetPos = useOnContext.getClickedPos();

        //DataComponentUtils.getOrCreateComponent(ModDataComponentTypes.BUCKET_FILL_LEVEL, itemStack);
        //itemStack.set(ModDataComponentTypes.BUCKET_FILL_LEVEL, 0);

        if (player != null) {
            if (!player.isCrouching()) {
                precisionBucketPlace(level, targetPos, itemStack, player);
            }
            else {
                precisionBucketPickup(level, targetPos, itemStack, player);
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag) {
        if (false) {
/*            int bucketFillLevel = DataComponentUtils.getOrCreateComponent(ModDataComponentTypes.BUCKET_FILL_LEVEL, itemStack);
            String toolTipText = "Bucket contains: " + bucketFillLevel + "levels " + "of fluid";
            list.add(Component.literal(toolTipText));*/
        }
        else {
            String toolTipText = "Bucket contains: " + 0 + " levels " + "of fluid";
            list.add(Component.literal(toolTipText));
        }
    }

    @Override
    public boolean isBarVisible(ItemStack itemStack) {
        return true;
    }

    @Override
    public int getBarColor(ItemStack itemStack) {
        return Mth.color(0.22f, 0.55f, 0.99f);
    }

    @Override
    public int getBarWidth(ItemStack itemStack) {
        int fillLevel = 0;
        if (itemStack.get(ModDataComponentTypes.BUCKET_FILL_LEVEL) != null) {
            fillLevel = itemStack.get(ModDataComponentTypes.BUCKET_FILL_LEVEL);
        }
        float fraction = (float) fillLevel / (float) maxFillLevel;
        return (int) (13f * fraction);
    }

    public static boolean precisionBucketPlace(Level level, BlockPos pos, ItemStack itemStack, Player player) {
        int fillLevel = 0;
        if (itemStack.get(ModDataComponentTypes.BUCKET_FILL_LEVEL) != null) {
            fillLevel = itemStack.get(ModDataComponentTypes.BUCKET_FILL_LEVEL);
        }
        int newBucketFillLevel = 0;

        if (fillLevel > 0 && !level.isClientSide) {
            BlockHitResult blockHitResult = getPlayerPOVHitResult(level, player, net.minecraft.world.level.ClipContext.Fluid.NONE);
            BlockPos blockPos = blockHitResult.getBlockPos();
            Direction direction = blockHitResult.getDirection();
            BlockPos blockPos2 = blockPos.relative(direction);
            //NonCachedWater.addWater(bucketFillLevel, blockPos2, level);
            FluidUtil.addVolume((ServerLevel) level, blockPos2, WaterInfo.WATER_TYPE, fillLevel);
            itemStack.set(ModDataComponentTypes.BUCKET_FILL_LEVEL, newBucketFillLevel);
        }
        return true;
    }

    protected static BlockHitResult getPlayerEntityPOVHitResult(Level level, Player player, ClipContext.Fluid fluid) {
        float f = player.getXRot();
        float g = player.getYRot();
        Vec3 vec3 = player.getEyePosition();
        float h = Mth.cos(-g * 0.017453292F - 3.1415927F);
        float i = Mth.sin(-g * 0.017453292F - 3.1415927F);
        float j = -Mth.cos(-f * 0.017453292F);
        float k = Mth.sin(-f * 0.017453292F);
        float l = i * j;
        float n = h * j;
        double d = 5.0;
        Vec3 vec32 = vec3.add((double)l * 5.0, (double)k * 5.0, (double)n * 5.0);
        return level.clip(new ClipContext(vec3, vec32, ClipContext.Block.OUTLINE, fluid, player));
    }

    public static boolean precisionBucketPickup(Level level, BlockPos pos, ItemStack itemStack, Player player) {
        int fillLevel = 0;
        if (itemStack.get(ModDataComponentTypes.BUCKET_FILL_LEVEL) != null) {
            fillLevel = itemStack.get(ModDataComponentTypes.BUCKET_FILL_LEVEL);
        }

        int bucketRemainingSpace = maxFillLevel - fillLevel;
        if (!level.isClientSide) {
            BlockHitResult blockHitResult = getPlayerPOVHitResult(level, player, net.minecraft.world.level.ClipContext.Fluid.NONE);
            BlockPos blockPos = blockHitResult.getBlockPos();
            Direction direction = blockHitResult.getDirection();
            BlockPos blockPos2 = blockPos.relative(direction);
            short oldVolume = FluidUtil.getVolume(level, blockPos2, WaterInfo.WATER_TYPE);

            System.out.println("oldvol: " + oldVolume);
            int newVolume = 0;
            int newBucketFillLevel;
            if (oldVolume > bucketRemainingSpace) {
                newVolume = oldVolume - bucketRemainingSpace;
                newBucketFillLevel = maxFillLevel;
            }
            else {
                newBucketFillLevel = fillLevel + oldVolume;
            }
            if (newVolume > 0) {
                //level.setBlock(blockPos2, Fluids.WATER.getFlowing(newVolume, false).createLegacyBlock(), 11);
                MultiFluidValue iValue = MultiFluidValue.single(WaterInfo.WATER_TYPE, (short) newVolume);
                FluidUtil.setVolume((ServerLevel) level, blockPos2, iValue);
                }
            else {
                //level.setBlock(blockPos2, Blocks.AIR.defaultBlockState(), 11);
                MultiFluidValue iValue = MultiFluidValue.single(WaterInfo.WATER_TYPE, (short) newVolume);
                FluidUtil.setVolume((ServerLevel) level, blockPos2, iValue);
            }

            System.out.println("set bucket to " + newBucketFillLevel);
            itemStack.set(ModDataComponentTypes.BUCKET_FILL_LEVEL, newBucketFillLevel);
        }
        return true;
    }
}
