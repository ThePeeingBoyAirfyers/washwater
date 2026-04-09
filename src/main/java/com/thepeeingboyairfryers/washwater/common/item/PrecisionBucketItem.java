package com.thepeeingboyairfryers.washwater.common.item;

import com.thepeeingboyairfryers.washwater.common.WaterInfo;
import com.thepeeingboyairfryers.washwater.common.component.ModDataComponentTypes;
import com.thepeeingboyairfryers.washwater.common.fluids.FluidUtil;
import com.thepeeingboyairfryers.washwater.common.fluids.MultiFluidValue;
import com.thepeeingboyairfryers.washwater.common.util.distanceBFS;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Arrays;
import java.util.List;

public class PrecisionBucketItem extends Item {

    boolean isBucketFull;
    static int bucketRadius = 2;
    static final int bucketDiameter = 2 * bucketRadius + 1;

    public PrecisionBucketItem(Item.Properties properties) {
        super(properties);
    }

    public InteractionResult useOn(UseOnContext useOnContext) {
        Level level = useOnContext.getLevel();
        Player player = useOnContext.getPlayer();
        ItemStack itemStack = useOnContext.getItemInHand();
        BlockHitResult blockHitResult = getPlayerPOVHitResult(level, player, net.minecraft.world.level.ClipContext.Fluid.NONE);
        BlockPos targetPos = blockHitResult.getBlockPos().relative(blockHitResult.getDirection());
        if (player != null) {
            if (!player.isCrouching()) {
                precisionBucketPlace(level, targetPos, itemStack, player);
            }
            else {
                //precisionBucketPickup(level, targetPos, itemStack, player);
                smartPickup(level, targetPos, itemStack, player);
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag) {
        if (itemStack.get(ModDataComponentTypes.BUCKET_FILL_LEVEL) != null) {
            int bucketFillLevel = itemStack.get(ModDataComponentTypes.BUCKET_FILL_LEVEL);
            String toolTipText = "Bucket contains: " + bucketFillLevel + " levels " + "of fluid";
            list.add(Component.literal(toolTipText));
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
        float fraction = (float) fillLevel / (float) WaterInfo.PRECISION_BUCKET_CAPACITY;
        return (int) (13f * fraction);
    }

    public static boolean precisionBucketPlace(Level level, BlockPos targetPos, ItemStack itemStack, Player player) {
        int fillLevel = 0;
        if (itemStack.get(ModDataComponentTypes.BUCKET_FILL_LEVEL) != null) {
            fillLevel = itemStack.get(ModDataComponentTypes.BUCKET_FILL_LEVEL);
        }
        int newBucketFillLevel = 0;
        if (fillLevel > 0 && !level.isClientSide) {
            if (level.getBlockState(targetPos).isAir() || !level.getBlockState(targetPos).getFluidState().isEmpty()) {
                FluidUtil.addVolume((ServerLevel) level, targetPos, WaterInfo.WATER_TYPE, fillLevel);
                itemStack.set(ModDataComponentTypes.BUCKET_FILL_LEVEL, newBucketFillLevel);
                return true;
            }
            return false;
        }
        return true;
    }

    public static boolean precisionBucketPickup(Level level, BlockPos targetPos, ItemStack itemStack, Player player) {
        int fillLevel = 0;
        if (itemStack.get(ModDataComponentTypes.BUCKET_FILL_LEVEL) != null) {
            fillLevel = itemStack.get(ModDataComponentTypes.BUCKET_FILL_LEVEL);
        }
        if (fillLevel == WaterInfo.PRECISION_BUCKET_CAPACITY)
            return true;

        int bucketRemainingSpace = WaterInfo.PRECISION_BUCKET_CAPACITY - fillLevel;
        if (!level.isClientSide) {
            short oldVolume = FluidUtil.getVolume(level, targetPos, WaterInfo.WATER_TYPE);
            int newVolume = 0;
            int newBucketFillLevel;
            if (oldVolume > bucketRemainingSpace) {
                newVolume = oldVolume - bucketRemainingSpace;
                newBucketFillLevel = WaterInfo.PRECISION_BUCKET_CAPACITY;
            }
            else {
                newBucketFillLevel = fillLevel + oldVolume;
            }
            if (newVolume > 0) {
                FluidUtil.setVolume((ServerLevel) level, targetPos, MultiFluidValue.single(WaterInfo.WATER_TYPE, (short) newVolume));
                }
            else {
                FluidUtil.setVolume((ServerLevel) level, targetPos, MultiFluidValue.single(WaterInfo.WATER_TYPE, (short) newVolume));
            }
            itemStack.set(ModDataComponentTypes.BUCKET_FILL_LEVEL, newBucketFillLevel);
            return false;
        }
        else {
            return true;
        }
    }

/*    public static boolean bfsPickup(Level level, BlockPos centrePos, ItemStack stack, Player player) {
        boolean isFull;
        int[][] distanceMatrix = distanceBFS.runDistanceMapBFS(centrePos, level);


        for (int x = 0; x < bucketDiameter; x++) {
            for (int y = 0; y < bucketDiameter; y++) {
                if distanceMatrix[][]
            }
        }
    }*/

    public static boolean smartPickup(Level level, BlockPos centrePos, ItemStack itemStack, Player player) {
        if (!level.isClientSide) {
        /*    boolean isFull;
            isFull = precisionBucketPickup(level, centrePos, itemStack, player);
            if (isFull) {
                System.out.println("returned early");
                return true;
            }

            for (Direction dir : Direction.values()) {
                isFull = precisionBucketPickup(level, centrePos.relative(dir), itemStack, player);
                if (isFull)
                    break;
            }*/
            doBFSStuff(level, centrePos);

            return true;
        }
        return true;
    }

    public static void doBFSStuff(Level level, BlockPos pos) {
        int[][] result = distanceBFS.runDistanceMapBFS(pos, level);
        System.out.println("result matrix: " + result);
    }
}
