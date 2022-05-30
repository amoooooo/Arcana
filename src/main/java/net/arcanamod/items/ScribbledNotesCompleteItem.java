package net.arcanamod.items;

import net.arcanamod.capabilities.Researcher;
import net.arcanamod.systems.research.ResearchBooks;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.ParametersAreNonnullByDefault;

import static net.arcanamod.Arcana.arcLoc;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ScribbledNotesCompleteItem extends Item {
    private static final ResourceLocation ROOT = arcLoc("root");
    
    public ScribbledNotesCompleteItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    // gives players the arcanum on right click
    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        if(hand == InteractionHand.MAIN_HAND)
            player.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        else
            player.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
        player.addItem(new ItemStack(ArcanaItems.ARCANUM.get()));
        Researcher.getFrom(player).advanceEntry(ResearchBooks.getEntry(ROOT));
        return super.use(world, player, hand);
    }
}