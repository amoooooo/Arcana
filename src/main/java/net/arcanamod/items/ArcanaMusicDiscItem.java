package net.arcanamod.items;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.RecordItem;

public class ArcanaMusicDiscItem extends RecordItem {
    public ArcanaMusicDiscItem(int comparator, SoundEvent sound, Properties builder) {
        super(comparator, () -> sound, builder);
    }
}
