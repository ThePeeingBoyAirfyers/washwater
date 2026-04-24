package com.thepeeingboyairfryers.washwater.mixin.common;


import com.thepeeingboyairfryers.washwater.common.flow.WaterPushing;
import com.thepeeingboyairfryers.washwater.common.fluids.FluidUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public class MixinBlockItem {



/*    @Redirect(
            method = "place",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/Block;setPlacedBy(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;)V"
            )
    )
    private void redirectBlockPlacement(Block instance, Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        System.out.println("Tu as mis un bloc");
        if (FluidUtil.hasFluid(level, pos)) {
            System.out.println("oui");
            WaterPushing.displaceFluids((ServerLevel) level, pos);
        }

    }*/

/*    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;setPlacedBy(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;)V"), method = "place", cancellable = true)
    private void redirectBlockPlacement(BlockPlaceContext context, CallbackInfoReturnable<InteractionResult> cir) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        System.out.println("Tu as mis un bloc");
        if (!level.isClientSide()) {
            if (FluidUtil.getAllVolume(level, pos) != 0) {
                System.out.println("oui");
                if (WaterPushing.displaceFluids((ServerLevel) level, pos))
                    cir.setReturnValue(InteractionResult.PASS);
                else {
                    cir.setReturnValue(InteractionResult.FAIL);
                }
            }
        }

    }*/
/*    @Inject(at = @At(value = "HEAD"), method = "place", cancellable = true)
    private void redirectBlockPlacement(BlockPlaceContext context, CallbackInfoReturnable<InteractionResult> cir) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        System.out.println("Tu as mis un bloc");
        if (!level.isClientSide()) {
            if (FluidUtil.hasFluid(level, pos)) {
                System.out.println("oui");
                WaterPushing.displaceFluids((ServerLevel) level, pos);
            }
        }
    }*/
/*    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;setPlacedBy(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;)V"), method = "place", cancellable = true)
    private void pushWaterAway(BlockPlaceContext context, CallbackInfoReturnable<InteractionResult> cir) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        System.out.println("Tu as mis un bloc");
        if (!level.isClientSide()) {
            System.out.println("Nous sommes dans le tick du server");
            if (FluidUtil.hasFluid(level, pos)) {
                System.out.println("Il y avait d'eau");
                WaterPushing.displaceFluids((ServerLevel) level, pos);
            }
        }

    }*/


    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/BlockItem;placeBlock(Lnet/minecraft/world/item/context/BlockPlaceContext;Lnet/minecraft/world/level/block/state/BlockState;)Z"), method = "place", cancellable = true)
    private void pushWaterAway(BlockPlaceContext context, CallbackInfoReturnable<InteractionResult> cir) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        System.out.println("Tu as mis un bloc");
        if (true) {
            if (FluidUtil.hasFluid(level, pos)) {
                boolean success = true;
                System.out.println("oui");
                if (WaterPushing.checkIfCanDisplaceFluids(level, pos)) {
                    System.out.println("can displace");
                    if (!level.isClientSide)
                        WaterPushing.displaceFluids((ServerLevel) level, pos);

                }
                else {
                    cir.setReturnValue(InteractionResult.FAIL);
                    cir.cancel();
                }
/*                if (!WaterPushing.displaceFluids((ServerLevel) level, pos)) {

                }*/


            }
        }


    }




}
