package com.github.tartaricacid.touhoulittlemaid.block;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.init.MaidItems;
import com.github.tartaricacid.touhoulittlemaid.tileentity.TileEntityAltar;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

/**
 * @author TartaricAcid
 * @date 2019/8/31 21:57
 **/
public class BlockAltar extends Block implements ITileEntityProvider {
    public BlockAltar() {
        super(Material.ROCK);
        setTranslationKey(TouhouLittleMaid.MOD_ID + "." + "altar");
        setHardness(1.0f);
        setRegistryName("altar");
        setCreativeTab(MaidItems.TABS);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(@Nonnull World worldIn, int meta) {
        return new TileEntityAltar();
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity instanceof TileEntityAltar && hand == EnumHand.MAIN_HAND) {
            TileEntityAltar altar = (TileEntityAltar) tileEntity;
            if (playerIn.isSneaking()) {
                applyTakeOutLogic(worldIn, pos, altar);
            } else {
                applyPlaceLogic(altar, playerIn);
            }
            altar.refresh();
            return true;
        }
        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
    }

    private void applyTakeOutLogic(World world, BlockPos pos, TileEntityAltar altar) {
        if (altar.isCanPlaceItem()) {
            if (altar.handler.getStackInSlot(0) != ItemStack.EMPTY) {
                Block.spawnAsEntity(world, pos.add(0, 1, 0), altar.handler.extractItem(0, 1, false));
            }
        }
    }

    private void applyPlaceLogic(TileEntityAltar altar, EntityPlayer playerIn) {
        if (altar.isCanPlaceItem()) {
            if (altar.handler.getStackInSlot(0) == ItemStack.EMPTY && playerIn.getHeldItemMainhand() != ItemStack.EMPTY) {
                altar.handler.setStackInSlot(0, ItemHandlerHelper.copyStackWithSize(playerIn.getHeldItemMainhand(), 1));
                if (!playerIn.isCreative()) {
                    playerIn.getHeldItemMainhand().shrink(1);
                }
            }
        }
    }

    @Override
    public void breakBlock(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        if (!worldIn.isRemote) {
            TileEntity tileEntity = worldIn.getTileEntity(pos);
            if (tileEntity instanceof TileEntityAltar && ((TileEntityAltar) tileEntity).handler.getStackInSlot(0) != ItemStack.EMPTY) {
                Block.spawnAsEntity(worldIn, pos.add(0, 1, 0), ((TileEntityAltar) tileEntity).handler.getStackInSlot(0));
            }
        }
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
        if (!worldIn.isRemote) {
            TileEntity tileEntity = worldIn.getTileEntity(pos);
            if (tileEntity instanceof TileEntityAltar) {
                restoreStorageBlock(worldIn, pos, ((TileEntityAltar) tileEntity).getBlockPosList());
                if (!player.isCreative()) {
                    dropStorageBlock(worldIn, pos, (TileEntityAltar) tileEntity);
                }
            }
        }
    }

    private void restoreStorageBlock(World worldIn, BlockPos currentPos, List<BlockPos> posList) {
        for (BlockPos storagePos : posList) {
            if (!storagePos.equals(currentPos)) {
                TileEntity tileEntity = worldIn.getTileEntity(storagePos);
                if (tileEntity instanceof TileEntityAltar) {
                    worldIn.setBlockState(storagePos, ((TileEntityAltar) tileEntity).getBlockState());
                }
            }
        }
    }

    private void dropStorageBlock(World worldIn, BlockPos pos, TileEntityAltar tileEntityAltar) {
        Block block = tileEntityAltar.getBlockState().getBlock();
        Block.spawnAsEntity(worldIn, pos, new ItemStack(Item.getItemFromBlock(block),
                1, block.getMetaFromState(tileEntityAltar.getBlockState())));
    }

    @Nonnull
    @Override
    public ItemStack getPickBlock(@Nonnull IBlockState state, RayTraceResult target, @Nonnull World world, @Nonnull BlockPos pos, EntityPlayer player) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityAltar) {
            IBlockState blockState = ((TileEntityAltar) tileEntity).getBlockState();
            return blockState.getBlock().getItem(world, pos, blockState);
        }
        return super.getPickBlock(state, target, world, pos, player);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityAltar) {
            addBlockDestroyEffects(world, pos, ((TileEntityAltar) tileEntity).getBlockState(), manager);
        }
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean addHitEffects(IBlockState state, World world, RayTraceResult target, ParticleManager manager) {
        TileEntity tileEntity = world.getTileEntity(target.getBlockPos());
        if (tileEntity instanceof TileEntityAltar) {
            addBlockHitEffects(world, target.getBlockPos(), target.sideHit, ((TileEntityAltar) tileEntity).getBlockState(), manager);
        }
        return true;
    }

    /**
     * 来自原版修改过的方法
     */
    @SideOnly(Side.CLIENT)
    private void addBlockDestroyEffects(World world, BlockPos pos, IBlockState state, ParticleManager manager) {
        if (!state.getBlock().isAir(state, world, pos) && !state.getBlock().addDestroyEffects(world, pos, manager)) {
            for (int j = 0; j < 4; ++j) {
                for (int k = 0; k < 4; ++k) {
                    for (int l = 0; l < 4; ++l) {
                        double d0 = ((double) j + 0.5D) / 4.0D;
                        double d1 = ((double) k + 0.5D) / 4.0D;
                        double d2 = ((double) l + 0.5D) / 4.0D;
                        manager.spawnEffectParticle(EnumParticleTypes.BLOCK_CRACK.getParticleID(),
                                (double) pos.getX() + d0, (double) pos.getY() + d1, (double) pos.getZ() + d2,
                                d0 - 0.5D, d1 - 0.5D, d2 - 0.5D, Block.getStateId(state));
                    }
                }
            }
        }
    }

    /**
     * 来自原版修改过的方法
     */
    @SideOnly(Side.CLIENT)
    private void addBlockHitEffects(World world, BlockPos pos, EnumFacing side, IBlockState iblockstate, ParticleManager manager) {
        Random rand = Block.RANDOM;
        if (iblockstate.getRenderType() != EnumBlockRenderType.INVISIBLE) {
            int i = pos.getX();
            int j = pos.getY();
            int k = pos.getZ();
            AxisAlignedBB axisalignedbb = iblockstate.getBoundingBox(world, pos);
            double d0 = (double) i + rand.nextDouble() * (axisalignedbb.maxX - axisalignedbb.minX - 0.20000000298023224D)
                    + 0.10000000149011612D + axisalignedbb.minX;
            double d1 = (double) j + rand.nextDouble() * (axisalignedbb.maxY - axisalignedbb.minY - 0.20000000298023224D)
                    + 0.10000000149011612D + axisalignedbb.minY;
            double d2 = (double) k + rand.nextDouble() * (axisalignedbb.maxZ - axisalignedbb.minZ - 0.20000000298023224D)
                    + 0.10000000149011612D + axisalignedbb.minZ;
            if (side == EnumFacing.DOWN) {
                d1 = (double) j + axisalignedbb.minY - 0.10000000149011612D;
            }
            if (side == EnumFacing.UP) {
                d1 = (double) j + axisalignedbb.maxY + 0.10000000149011612D;
            }
            if (side == EnumFacing.NORTH) {
                d2 = (double) k + axisalignedbb.minZ - 0.10000000149011612D;
            }
            if (side == EnumFacing.SOUTH) {
                d2 = (double) k + axisalignedbb.maxZ + 0.10000000149011612D;
            }
            if (side == EnumFacing.WEST) {
                d0 = (double) i + axisalignedbb.minX - 0.10000000149011612D;
            }
            if (side == EnumFacing.EAST) {
                d0 = (double) i + axisalignedbb.maxX + 0.10000000149011612D;
            }
            manager.spawnEffectParticle(EnumParticleTypes.BLOCK_CRACK.getParticleID(),
                    d0, d1, d2, 0.0D, 0.0D, 0.0D, Block.getStateId(iblockstate));
        }
    }

    @SideOnly(Side.CLIENT)
    public boolean hasCustomBreakingProgress(IBlockState state) {
        return false;
    }

    @Nonnull
    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }

    @Override
    public boolean isBlockNormalCube(IBlockState blockState) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState blockState) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }
}
