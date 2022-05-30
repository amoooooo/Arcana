package net.arcanamod.systems.spell;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;

import java.util.Optional;

public interface ISpell{
	/**
	 * Cost of spell in AspectStacks.
	 * @return returns cost of spell.
	 */
	SpellCosts getSpellCosts();

	default Optional<TextComponent> getName(CompoundTag nbt){
		return Optional.empty();
	}
}
