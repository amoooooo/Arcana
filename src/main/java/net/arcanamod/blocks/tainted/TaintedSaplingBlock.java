package net.arcanamod.blocks.tainted;

import net.arcanamod.systems.taint.Taint;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;

public class TaintedSaplingBlock extends SaplingBlock {
	
	public TaintedSaplingBlock(Block parent){
		super(/*Taint.taintedTreeOf((SaplingBlock) parent)*/null, Block.Properties.copy(parent));
		Taint.addTaintMapping(parent, this);
	}
	
	public TaintedSaplingBlock(Block parent, AbstractTreeGrower tree, Properties properties){
		super(tree, properties);
		Taint.addTaintMapping(parent, this);
	}
}
