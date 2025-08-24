package com.iafenvoy.iceandfire.item.block;

import com.iafenvoy.iceandfire.item.block.entity.DreadPortalBlockEntity;
import com.iafenvoy.iceandfire.item.block.util.DreadBlock;
import com.iafenvoy.iceandfire.registry.IafParticles;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class DreadPortalBlock extends BlockWithEntity implements DreadBlock {
    private static final MapCodec<? extends BlockWithEntity> CODEC = createCodec(s -> new DreadPortalBlock());

    public DreadPortalBlock() {
        super(Settings.create().mapColor(MapColor.CLEAR).pistonBehavior(PistonBehavior.BLOCK).nonOpaque().dynamicBounds().strength(-1, 100000).luminance((state) -> 1).ticksRandomly());
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random rand) {
        BlockEntity tileentity = world.getBlockEntity(pos);

        if (tileentity instanceof DreadPortalBlockEntity) {
            int i = 3;
            for (int j = 0; j < i; ++j) {
                double d0 = (float) pos.getX() + rand.nextFloat();
                double d1 = (float) pos.getY() + rand.nextFloat();
                double d2 = (float) pos.getZ() + rand.nextFloat();
                double d3 = ((double) rand.nextFloat() - 0.5D) * 0.25D;
                double d4 = ((double) rand.nextFloat()) * -0.25D;
                double d5 = ((double) rand.nextFloat() - 0.5D) * 0.25D;
                int k = rand.nextInt(2) * 2 - 1;
                world.addParticle(IafParticles.DREAD_PORTAL.get(), d0, d1, d2, d3, d4, d5);
            }
        }
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.cuboid(0, 0, 0, 0, 0, 0);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new DreadPortalBlockEntity(pos, state);
    }
}