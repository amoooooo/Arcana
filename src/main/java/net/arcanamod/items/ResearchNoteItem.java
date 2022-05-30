package net.arcanamod.items;

import net.arcanamod.capabilities.Researcher;
import net.arcanamod.client.ClientUtils;
import net.arcanamod.systems.research.Puzzle;
import net.arcanamod.systems.research.ResearchBooks;
import net.arcanamod.systems.research.ResearchEntry;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ResearchNoteItem extends Item {
	
	private final boolean isComplete;
	
	public ResearchNoteItem(Properties properties, boolean complete){
		super(properties);
		isComplete = complete;
	}
	
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand){
		if(!isComplete)
			return super.use(world, player, hand);
		ItemStack stack = player.getItemInHand(hand);
		CompoundTag compound = stack.getTag();
		if(compound != null && compound.contains("puzzle")){
			Researcher from = Researcher.getFrom(player);
			Puzzle puzzle = ResearchBooks.puzzles.get(new ResourceLocation(compound.getString("puzzle")));
			if(!from.isPuzzleCompleted(puzzle)){
				from.completePuzzle(puzzle);
				if(!player.getAbilities().instabuild)
					stack.shrink(1);
				// If this note is associated with a research entry,
				if(compound.contains("research")){
					ResourceLocation research = new ResourceLocation(compound.getString("research"));
					ResearchEntry entry = ResearchBooks.getEntry(research);
					// and that entry only has one requirement,
					int stage = from.entryStage(entry);
					if(stage < entry.sections().size() && entry.sections().get(stage).getRequirements().size() == 1)
						// continue straight away.
						from.advanceEntry(entry);
					// display a toast
					if(world.isClientSide())
						ClientUtils.displayPuzzleToast(entry);
				}else if(world.isClientSide())
					ClientUtils.displayPuzzleToast(null);
				return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
			}
		}
		return super.use(world, player, hand);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag){
		CompoundTag compound = stack.getTag();
		if(compound != null && compound.contains("research")){
			ResearchEntry research = ResearchBooks.getEntry(new ResourceLocation(compound.getString("research")));
			if(research != null)
				tooltip.add(new TranslatableComponent(research.name()).withStyle(ChatFormatting.AQUA));
		}
	}
}