package net.arcanamod.systems.research.impls;

import net.arcanamod.systems.research.Requirement;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

import static net.arcanamod.Arcana.arcLoc;

public class ItemTagRequirement extends Requirement{
	
	protected TagKey<Item> tag;
	protected ResourceLocation tagName;
	
	public static final ResourceLocation TYPE = arcLoc("item_tag");
	
	public ItemTagRequirement(ResourceLocation tagName){
		this(ItemTags.create(tagName), tagName); // This may explode...
	}
	
	public ItemTagRequirement(TagKey<Item> tag, ResourceLocation tagName){
		this.tag = tag;
	}
	
	public boolean satisfied(Player player){
		return player.getInventory().clearOrCountMatchingItems(x -> x.is(tag), 0, player.getInventory()) >= (getAmount() == 0 ? 1 : getAmount());
	}
	
	public void take(Player player){
		player.getInventory().clearOrCountMatchingItems(x -> x.is(tag), getAmount(), player.getInventory());
	}
	
	public ResourceLocation type(){
		return TYPE;
	}
	
	public CompoundTag data(){
		CompoundTag compound = new CompoundTag();
		compound.putString("itemTag", tagName.toString());
		return compound;
	}
	
	public TagKey<Item> getTag(){
		return tag;
	}
	
	public ResourceLocation getTagName(){
		return tagName;
	}
}