package net.arcanamod.systems.taint;

import net.minecraft.world.damagesource.DamageSource;

public class TaintDamageSource extends DamageSource {
	public static TaintDamageSource TAINT = new TaintDamageSource();

	public TaintDamageSource() {
		super("taint");
	}

	@Override
	public boolean isBypassArmor() {
		return true;
	}
}
