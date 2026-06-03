package com.thepeeingboyairfryers.washwater.gameplay.common.features.bucket;

import com.thepeeingboyairfryers.washwater.base.common.WaterInfo;
import com.thepeeingboyairfryers.washwater.collections.WWDataComponentTypes;
import com.thepeeingboyairfryers.washwater.base.common.fluids.FluidUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.SoundActions;

import javax.annotation.Nullable;
import java.util.List;

public class PrecisionBucketItem extends Item {

    public PrecisionBucketItem(Item.Properties properties) {
        super(properties);
    }

    public InteractionResult useOn(UseOnContext useOnContext) {
        Level level = useOnContext.getLevel();
        Player player = useOnContext.getPlayer();

        if (player == null)
            return InteractionResult.FAIL;

        ItemStack itemStack = useOnContext.getItemInHand();
        BlockHitResult blockHitResult = getPlayerPOVHitResult(level, player, net.minecraft.world.level.ClipContext.Fluid.NONE);
        BlockPos targetPos = blockHitResult.getBlockPos().relative(blockHitResult.getDirection());
        if (!player.isCrouching()) {
            if (precisionBucketPlace(level, targetPos, itemStack, player)) {
                playEmptySound(player, level, targetPos);
                return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide()).getResult();
            }

        } else {
            smartPickup(level, targetPos, itemStack, player);
        }

        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag) {
        Integer bucketFillLevel = itemStack.get(WWDataComponentTypes.BUCKET_FILL_LEVEL);

        if (bucketFillLevel != null) {
            String toolTipText = "Bucket contains: " + bucketFillLevel + " levels " + "of fluid";
            list.add(Component.literal(toolTipText));
        } else {
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
        if (itemStack.get(WWDataComponentTypes.BUCKET_FILL_LEVEL) != null) {
            fillLevel = itemStack.get(WWDataComponentTypes.BUCKET_FILL_LEVEL);
        }
        float fraction = (float) fillLevel / (float) WaterInfo.PRECISION_BUCKET_CAPACITY;
        return (int) (13f * fraction);
    }

    public static boolean precisionBucketPlace(Level level, BlockPos targetPos, ItemStack itemStack, Player player) {
        int fillLevel = 0;
        if (itemStack.get(WWDataComponentTypes.BUCKET_FILL_LEVEL) != null) {
            fillLevel = itemStack.get(WWDataComponentTypes.BUCKET_FILL_LEVEL);
        }
        if (fillLevel == 0)
            return false;
        int newBucketFillLevel = 0;
            if (level.getBlockState(targetPos).isAir() || !level.getBlockState(targetPos).getFluidState().isEmpty()) {
                if (FluidUtil.canAddVolume(level, targetPos, WaterInfo.WATER_TYPE, fillLevel) == fillLevel) {
                    if (!level.isClientSide)
                        FluidUtil.addVolume(level, targetPos, WaterInfo.WATER_TYPE, fillLevel);
                    itemStack.set(WWDataComponentTypes.BUCKET_FILL_LEVEL, newBucketFillLevel);
                    return true;
                }
            }
        return false;
    }

    public static boolean smartPickup(Level level, BlockPos centrePos, ItemStack itemStack, Player player) {
        if (!level.isClientSide) {
            BucketBfs.runBucketBFS(level, centrePos, itemStack, player);
            return true;
        }
        return true;
    }

    protected void playEmptySound(@Nullable Player player, LevelAccessor level, BlockPos pos) {
        SoundEvent soundevent = WaterInfo.WATER_TYPE.getSound(player, level, pos, SoundActions.BUCKET_EMPTY);
        if (soundevent == null) {
            soundevent = SoundEvents.BUCKET_EMPTY;
        }

        level.playSound(player, pos, soundevent, SoundSource.BLOCKS, 1.0F, 1.0F);
        level.gameEvent(player, GameEvent.FLUID_PLACE, pos);
    }

}
