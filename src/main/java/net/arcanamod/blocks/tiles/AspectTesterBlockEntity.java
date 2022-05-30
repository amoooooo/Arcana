package net.arcanamod.blocks.tiles;

import net.arcanamod.aspects.AspectStack;
import net.arcanamod.aspects.Aspects;
import net.arcanamod.aspects.handlers.AspectBattery;
import net.arcanamod.aspects.handlers.AspectCell;
import net.arcanamod.aspects.handlers.AspectHandlerCapability;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;

public class AspectTesterBlockEntity extends BlockEntity {
	
	public AspectBattery battery = new AspectBattery(/*100,100*/);
	
	public AspectTesterBlockEntity(BlockPos pWorldPosition, BlockState pBlockState){
		super(ArcanaTiles.ASPECT_TESTER.get(), pWorldPosition, pBlockState);
		init();
	}
	
	private void init(){
		AspectCell cell = new AspectCell(100);
		cell.insert(new AspectStack(Aspects.EXCHANGE, 16), false);
		battery.getHolders().add(cell);
	}
	
	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap){
		if(cap.equals(AspectHandlerCapability.ASPECT_HANDLER))
			return battery.getCapability(cap).cast();
		return null;
	}
}
