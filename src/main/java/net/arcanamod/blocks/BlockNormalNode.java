package net.arcanamod.blocks;

import net.arcanamod.aspects.Aspects;
import net.arcanamod.blocks.bases.GroupedBlock;
import net.arcanamod.blocks.tiles.NodeTileEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockNormalNode extends GroupedBlock{
	
	private static final AxisAlignedBB BOUNDING_BOX = new AxisAlignedBB(0.25d, 0.25d, 0.25d, 0.75d, 0.75d, 0.75d);
	
	public boolean isOn = false;
	
	public BlockNormalNode(Properties properties, ItemGroup group){
		super(properties, group);
	}
	
	public void hitboxOff(){
		isOn = false;
	}
	
	public void hitboxOn(){
		isOn = true;
	}
	
	@Nullable
	//@Override
	public TileEntity createNewTileEntity(World worldIn){
		NodeTileEntity entity = new NodeTileEntity(null);
		
		Random rand = worldIn.rand;
		
		for(int i = 0; i < rand.nextInt((5 - 2) + 1) + 5; i++){
			int randomAspect = rand.nextInt(6);
			entity.storedAspects.putIfAbsent(Aspects.primalAspects[randomAspect], rand.nextInt((80 - 30) + 1) + 30);
		}
		
		entity.markDirty();
		return entity;
	}
	
	//@Override
	//public AxisAlignedBB getBoundingBox(BlockState state, IBlockAccess source, BlockPos pos){
	//	return isOn ? BOUNDING_BOX : new AxisAlignedBB(0, 0, 0, 0, 0, 0);
	//}
	
	@Override
	public BlockRenderType getRenderType(BlockState state){
		return BlockRenderType.INVISIBLE;
	}
	
	//@Override
	//public BlockRenderLayer getBlockLayer(){
	//	return BlockRenderLayer.CUTOUT;
	//}
	
	//@Override
	public boolean isOpaqueCube(BlockState state){
		return false;
	}
	
	/*@SideOnly(Side.CLIENT)
	@Override
	public float getAmbientOcclusionLightValue(BlockState state){
		return 1.0f;
	}*/
	
	/*@Override
	public boolean isFullCube(BlockState state){
		return false;
	}*/
	
	/*@Override
	public boolean hasTileEntity(BlockState state){
		return true;
	}
	
	@Override
	public void addCollisionBoxToList(BlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState){
		addCollisionBoxToList(pos, entityBox, collidingBoxes, new AxisAlignedBB(0, 0, 0, 0, 0, 0));
	}
	
	@Nullable
	@Override
	public AxisAlignedBB getCollisionBoundingBox(BlockState blockState, IBlockAccess worldIn, BlockPos pos){
		return NULL_AABB;
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, BlockState state, PlayerEntity playerIn, Hand hand, Direction facing, float hitX, float hitY, float hitZ){
		if(worldIn.isRemote){
			return false;
		}
		if(hand != Hand.MAIN_HAND){
			return false;
		}
		ItemStack itemActivated = playerIn.getHeldItem(hand);
        *//*CoreType core = WandUtil.getCore(itemActivated);
        CapType cap = WandUtil.getCap(itemActivated);*//*
		// although other items can store aspects, only wands can draw them directly from nodes ATM
		if(itemActivated.getItem() instanceof ItemWand){
			TileEntity entity = worldIn.getTileEntity(pos);
			if(entity instanceof NodeTileEntity){
				NodeTileEntity tileEntity = (NodeTileEntity)entity;
                *//*NBTTagList aspectList = itemActivated.getTagCompound().getTagList("aspects", Constants.NBT.TAG_COMPOUND);
                NBTTagList newAspects = new NBTTagList();*//*
				for(Aspect coreAspect : Aspects.primalAspects){
					if(tileEntity.storedAspects.containsKey(coreAspect)){
                        *//*if(aspectList.hasNoTags()){
                            tileEntity.storedAspects.replace(coreAspect, tileEntity.storedAspects.get(coreAspect) - 1);
                            NBTTagCompound compound = new NBTTagCompound();
                            compound.setString("type", coreAspect.toString());
                            compound.setInteger("amount", 1);
                            newAspects.appendTag(compound);
                        }else{
                            for(NBTBase base : aspectList){
                                if(base instanceof NBTTagCompound){
                                    NBTTagCompound compound = (NBTTagCompound)base;
                                    if(coreAspect.toString().equals(compound.getString("type"))){
                                        tileEntity.storedAspects.replace(coreAspect, tileEntity.storedAspects.get(coreAspect) - 1);
                                        if(compound.getInteger("amount") < core.getMaxVis()){
                                            compound.setInteger("amount", compound.getInteger("amount") + 1);
                                            newAspects.appendTag(compound);
                                        }
                                    }
                                }
                            }
                        }
                        NBTTagCompound newTag = itemActivated.getTagCompound();
                        newTag.setTag("aspects", newAspects);
                        itemActivated.setTagCompound(newTag);*//*
						// always transfer one for now -- this should work with more though
						final int draw = 1;
						tileEntity.storedAspects.replace(coreAspect, tileEntity.storedAspects.get(coreAspect) - (VisHandler.getFrom(itemActivated).insert(coreAspect, draw, false) - draw));
						tileEntity.markDirty();
						
					}
				}
			}
		}
		return true;
	}
	
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, BlockState state, BlockPos pos, Direction face){
		return BlockFaceShape.UNDEFINED;
	}*/
}