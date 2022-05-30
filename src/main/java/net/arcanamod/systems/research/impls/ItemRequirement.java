package net.arcanamod.systems.research.impls;

import net.arcanamod.systems.research.Requirement;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import static net.arcanamod.Arcana.arcLoc;

public class ItemRequirement extends Requirement{
	
	// perhaps support Tag in the future? will be required for enchantments in the future at least.
	protected Item item;
	protected ItemStack stack;
	
	public static final ResourceLocation TYPE = arcLoc("item");
	
	public ItemRequirement(Item item){
		this.item = item;
	}
	
	public boolean satisfied(Player player){
		return player.getInventory().clearOrCountMatchingItems(x -> x.getItem() == item, 0, player.getInventory()) >= (getAmount() == 0 ? 1 : getAmount());
	}
	
	public void take(Player player){
		player.getInventory().clearOrCountMatchingItems(x -> x.getItem() == item, getAmount(), player.getInventory());
	}
	
	public ResourceLocation type(){
		return TYPE;
	}
	
	public CompoundTag data(){
		CompoundTag compound = new CompoundTag();
		compound.putString("itemType", String.valueOf(ForgeRegistries.ITEMS.getKey(item)));
		return compound;
	}
	
	public Item getItem(){
		return item;
	}
	
	public ItemStack getStack(){
		if(stack == null)
			return stack = new ItemStack(getItem());
		return stack;
	}
}