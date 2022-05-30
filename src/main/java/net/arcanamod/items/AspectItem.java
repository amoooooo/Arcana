package net.arcanamod.items;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AspectItem extends Item {
	private final String aspectName;
	
	public AspectItem(String aspectName) {
		super(new Item.Properties());
		if(aspectName.startsWith("aspect_"))
			aspectName = aspectName.substring(7);
		this.aspectName = aspectName;
	}

	@Override
	public Component getName(ItemStack stack) {
		return new TranslatableComponent("aspect." + aspectName).withStyle(ChatFormatting.AQUA);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		tooltip.add(new TranslatableComponent("aspect." + aspectName + ".desc"));
	}
	
	// getCreatorModId may be useful to override, to show who registered the aspect.
}