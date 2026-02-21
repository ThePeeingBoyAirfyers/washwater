package com.thepeeingboyairfryers.washwater.common.item;

import net.minecraft.world.item.Item;

public class PrecisionBucketItem extends Item {

    public PrecisionBucketItem(Properties properties) {
        super(properties);
    }

    //TODO add datacomponent
    /*

    public InteractionResult useOn(UseOnContext useOnContext) {
        Level level = useOnContext.getLevel();
        Player player = useOnContext.getPlayer();
        ItemStack itemStack = useOnContext.getItemInHand();
        BlockPos targetPos = useOnContext.getClickedPos();



        if (!itemStack.hasTag()) {
            CompoundTag tag = new CompoundTag();
            tag.putInt("washwater:bucketFillLevel", 0);
            itemStack.setTag(tag);
        }
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

    public static boolean precisionBucketPlace(Level level, BlockPos pos, ItemStack itemStack, Player player) {

        int bucketFillLevel = itemStack.getTag().getInt("washwater:bucketFillLevel");
        int newBucketFillLevel = 0;

        if (bucketFillLevel > 0 && !level.isClientSide && pos.getY() != WaterInfo.minY) {
            BlockHitResult blockHitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);
            BlockPos blockPos = blockHitResult.getBlockPos();
            Direction direction = blockHitResult.getDirection();
            BlockPos blockPos2 = blockPos.relative(direction);
            FluidManager.addVolume((ServerLevel) level, blockPos2, bucketFillLevel);
            CompoundTag tag = new CompoundTag();
            tag.putInt("washwater:bucketFillLevel", newBucketFillLevel);
            itemStack.setTag(tag);
        }
        return true;
    }
    public static boolean precisionBucketPickup(Level level, BlockPos pos, ItemStack itemStack, Player player) {
        int bucketFillLevel = itemStack.getTag().getInt("washwater:bucketFillLevel");
        int bucketRemainingSpace = WaterInfo.volumePerBlock - bucketFillLevel;
        if (!level.isClientSide && pos.getY() != WaterInfo.minY) {
            BlockHitResult blockHitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);
            BlockPos blockPos = blockHitResult.getBlockPos();
            Direction direction = blockHitResult.getDirection();
            BlockPos blockPos2 = blockPos.relative(direction);
            int oldVolume = FluidManager.getVolume(level, blockPos2);
            int newVolume = 0;
            int newBucketFillLevel;
            if (oldVolume > bucketRemainingSpace) {
                newVolume = oldVolume - bucketRemainingSpace;
                newBucketFillLevel = WaterInfo.volumePerBlock;
            }
            else {
                newBucketFillLevel = bucketFillLevel + oldVolume;
            }
            FluidManager.setVolume((ServerLevel) level, blockPos2,  newVolume);
            CompoundTag tag = new CompoundTag();
            tag.putInt("washwater:bucketFillLevel", newBucketFillLevel);
            itemStack.setTag(tag);
        }
        return true;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        if (itemStack.hasTag()) {
            int bucketFillLevel = itemStack.getTag().getInt("washwater:bucketFillLevel");
            String toolTipText = "Bucket contains: " + bucketFillLevel + "l " + "of fluid";
            list.add(new TextComponent(toolTipText));
        }
    }

    @Override
    public boolean isBarVisible(ItemStack itemStack) {
        return true;
    }

    @Override
    public int getBarColor(ItemStack itemStack) {
        return Mth.color(56, 141, 252);
    }

    @Override
    public int getBarWidth(ItemStack itemStack) {
        if (itemStack.hasTag()) {
            int fillLevel = itemStack.getTag().getInt("washwater:bucketFillLevel");
            int maxFillLevel = WaterInfo.volumePerBlock;
            float fraction = (float) fillLevel / (float) maxFillLevel;
            return (int) (13f * fraction);
        }
        else return 0;
    }
    */
}
