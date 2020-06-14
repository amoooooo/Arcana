package net.arcanamod.aspects;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

public interface IAspectHandler extends INBTSerializable<CompoundNBT> {

	/**
	 * Returns the number of aspects storage units ("cells") available
	 * @return The number of cells available
	 */
	int getHoldersAmount();

	/**
	 * Gets List of IAspectHolders
	 * @return List of IAspectHolders
	 */
	List<IAspectHolder> getHolders();

	/**
	 * Gets IAspectHolder by index.
	 * @param index index of holder.
	 * @return IAspectHolder.
	 */
	IAspectHolder getHolder(int index);

	void createCell(IAspectHolder cell);

	void deleteCell(IAspectHolder cell);

	void setCellSizes();

	/**
	 * Inserts AspectStack that contains Aspect and Amount.
	 * @param holder index of a holder.
	 * @param resource AspectStack to insert.
	 * @param simulate If true, the amount of vis is not actually changed.
	 * @return Inserted amount
	 */
	int insert(int holder, AspectStack resource, boolean simulate);

	/**
	 * Inserts amount of existing AspectStack inside.
	 * @param holder index of a holder.
	 * @param maxInsert amount to insert.
	 * @param simulate If true, the amount of vis is not actually changed.
	 * @return Inserted amount
	 */
	int insert(int holder, int maxInsert, boolean simulate);

	/**
	 * Drains AspectStack that contains Aspect and Amount.
	 * @param holder index of a holder.
	 * @param resource AspectStack to drain.
	 * @param simulate If true, the amount of vis is not actually changed.
	 * @return Drained amount
	 */
	int drain(int holder, AspectStack resource, boolean simulate);

	/**
	 * Drains amount of existing AspectStack inside.
	 * @param holder index of a holder.
	 * @param maxDrain amount to drain.
	 * @param simulate If true, the amount of vis is not actually changed.
	 * @return Drained amount
	 */
	int drain(int holder, int maxDrain, boolean simulate);

	IAspectHolder findAspectInHolders(Aspect aspect);

	int findIndexFromAspectInHolders(Aspect aspect);

	CompoundNBT serializeNBT();

	void deserializeNBT(CompoundNBT data);

	static Optional<IAspectHandler> getOptional(@Nonnull ICapabilityProvider holder){
		return Optional.of(holder.getCapability(AspectHandlerCapability.ASPECT_HANDLER, null).orElse(null));
	}

	static IAspectHandler getFrom(@Nonnull ICapabilityProvider holder){
		return holder.getCapability(AspectHandlerCapability.ASPECT_HANDLER, null).orElse(null);
	}
}
