package com.thepeeingboyairfryers.washwater.tests;

import com.thepeeingboyairfryers.washwater.common.WashWater;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.gametest.GameTestHolder;

@GameTestHolder(WashWater.MOD_ID)
public class BucketTest {
    private BucketTest() {
        throw new IllegalStateException();
    }

    @GameTest(setupTicks = 10L)
    public static void testBucketPlacement(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.setPos(helper.absolutePos(BlockPos.ZERO.above()).getCenter());
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.BUCKET));

        try {
            helper.useBlock(BlockPos.ZERO, player, new BlockHitResult(BlockPos.ZERO.getBottomCenter(), Direction.UP, BlockPos.ZERO, false));
        } catch (Exception e) {
            helper.fail("Failed to use bucket: " + e.getMessage());
            return;
        }

        helper.assertBlockState(BlockPos.ZERO.above(), s -> s.getFluidState().is(Fluids.WATER), () -> "Bucket did not place water correctly");
        helper.succeed();
    }
}
