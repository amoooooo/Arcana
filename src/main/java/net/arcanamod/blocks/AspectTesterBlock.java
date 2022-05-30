package net.arcanamod.blocks;

import net.arcanamod.blocks.tiles.AspectTesterBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.List;

public class AspectTesterBlock extends Block implements EntityBlock {
	public AspectTesterBlock(Block.Properties properties) {
		super(properties);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		tooltip.add(new TextComponent("DEV: Only for testing new AspectHandler.").setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
		tooltip.add(new TextComponent("Can crash the game!").setStyle(Style.EMPTY.withColor(0xFF0000)));
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
	}

//	@Override
//	public boolean hasTileEntity(BlockState state) {
//		return true;
//	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
		return new AspectTesterBlockEntity(pos, state);
	}
}