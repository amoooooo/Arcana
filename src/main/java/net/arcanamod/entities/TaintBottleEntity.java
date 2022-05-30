package net.arcanamod.entities;

import net.arcanamod.blocks.DelegatingBlock;
import net.arcanamod.items.ArcanaItems;
import net.arcanamod.systems.taint.Taint;
import net.arcanamod.world.AuraView;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.ParametersAreNonnullByDefault;

import static net.arcanamod.systems.taint.Taint.UNTAINTED;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class TaintBottleEntity extends ThrowableItemProjectile {
	
	public TaintBottleEntity(EntityType<? extends ThrowableItemProjectile> type, Level world){
		super(type, world);
	}
	
	protected Item getDefaultItem(){
		return ArcanaItems.TAINT_IN_A_BOTTLE.get();
	}
	
	public TaintBottleEntity(LivingEntity thrower, Level world){
		super(ArcanaEntities.TAINT_BOTTLE.get(), thrower, world);
	}
	
	protected void onHit(HitResult result){
		if(!level.isClientSide()){
			// pick some blocks and taint them
			// aim to taint 6 blocks within a 5x3x5 area, fail after 12 attempts
			int tainted = 0;
			BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
			for(int tries = 0; tries < 12 && tainted < 6; tries++){
				pos.set(this.getOnPos()).move(level.random.nextInt(5) - 2, level.random.nextInt(3) - 1, level.random.nextInt(5) - 2);
				BlockState state = level.getBlockState(pos);
				if(!state.isAir() && !Taint.isTainted(state.getBlock()) && !Taint.isBlockProtectedByPureNode(level, pos)){
					Block to = Taint.getTaintedOfBlock(state.getBlock());
					if(to != null){
						level.setBlockAndUpdate(pos, DelegatingBlock.switchBlock(state, to).setValue(UNTAINTED, false));
						tainted++;
					}
				}
			}
			// add some flux too
			AuraView.SIDED_FACTORY.apply(level).addFluxAt(getOnPos(), level.random.nextInt(3) + 3 + (6 - tainted));
			// add some particles
			level.levelEvent(2007, new BlockPos(this.getOnPos()), 0xa200ff);
			// and die
			this.remove(RemovalReason.DISCARDED);
		}
	}
	
	@Override
	public Packet<?> getAddEntityPacket(){
		return NetworkHooks.getEntitySpawningPacket(this);
	}
}