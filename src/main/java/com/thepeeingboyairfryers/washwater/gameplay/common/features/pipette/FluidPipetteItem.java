package com.thepeeingboyairfryers.washwater.gameplay.common.features.pipette;

import com.thepeeingboyairfryers.washwater.base.common.WaterInfo;
import com.thepeeingboyairfryers.washwater.base.common.fluids.FluidUtil;
import com.thepeeingboyairfryers.washwater.base.common.fluids.MultiFluidValue;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

public class FluidPipetteItem extends Item {


    public FluidPipetteItem(Properties properties) {
        super(properties);
    }

    public static boolean creativePipettePlace(Level level, BlockPos pos, ItemStack itemStack, Player player) {
        if (!level.isClientSide && pos.getY() != WaterInfo.MIN_Y) {
            BlockHitResult blockHitResult = getPlayerPOVHitResult(level, player, net.minecraft.world.level.ClipContext.Fluid.NONE);
            BlockPos blockPos = blockHitResult.getBlockPos();
            Direction direction = blockHitResult.getDirection();
            BlockPos blockPos2 = blockPos.relative(direction);
            FluidUtil.addVolume((ServerLevel) level, blockPos2, FluidUtil.WATER_TYPE, 1);
        }
        return true;
    }

    public static boolean creativePipettePickup(Level level, BlockPos pos, ItemStack itemStack, Player player) {
        if (!level.isClientSide) {
            BlockHitResult blockHitResult = getPlayerPOVHitResult(level, player, net.minecraft.world.level.ClipContext.Fluid.NONE);
            BlockPos blockPos = blockHitResult.getBlockPos();
            Direction direction = blockHitResult.getDirection();
            BlockPos blockPos2 = blockPos.relative(direction);
            int oldVolume = FluidUtil.getVolume(level, blockPos2, WaterInfo.WATER_TYPE);
            int newVolume = (oldVolume < 1) ? oldVolume : oldVolume - 1;
            FluidUtil.setVolume((ServerLevel) level, blockPos2, MultiFluidValue.single(WaterInfo.WATER_TYPE, (short) newVolume));
        }
        return true;
    }

    public InteractionResult useOn(UseOnContext useOnContext) {
        Level level = useOnContext.getLevel();
        Player player = useOnContext.getPlayer();
        if (player == null) return InteractionResult.FAIL;

        ItemStack itemStack = useOnContext.getItemInHand();
        BlockPos targetPos = useOnContext.getClickedPos();

        if (!player.isCrouching()) {
            creativePipettePlace(level, targetPos, itemStack, player);
        } else {
            creativePipettePickup(level, targetPos, itemStack, player);
        }

        return InteractionResult.PASS;
    }

}
