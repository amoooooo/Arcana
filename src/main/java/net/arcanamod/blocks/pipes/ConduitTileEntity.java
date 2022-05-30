package net.arcanamod.blocks.pipes;

import net.arcanamod.aspects.Aspect;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;
/*
public class ConduitBlockEntity extends TubeBlockEntity {
	
	// conditionally redirect (types of) specks
	Aspect whitelist = null;
	Direction dir;

	public ConduitBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
		super(pWorldPosition, pBlockState);
	}

	protected Optional<Direction> redirect(AspectSpeck speck, boolean canPass){
		if(speck.pos >= .5f && !getLevel().isBlockPowered(pos) && (whitelist == null || speck.payload.getAspect() == whitelist))
			return Optional.of(dir);
		return Optional.empty();
	}
}*/