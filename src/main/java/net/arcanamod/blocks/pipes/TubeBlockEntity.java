package net.arcanamod.blocks.pipes;

import net.arcanamod.aspects.AspectStack;
import net.arcanamod.aspects.handlers.AspectHandler;
import net.arcanamod.blocks.tiles.ArcanaTiles;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TubeBlockEntity extends BlockEntity {
	
	protected static final int MAX_SPECKS = 1000;
	
	List<AspectSpeck> specks = new ArrayList<>();
	
	public TubeBlockEntity(BlockPos pWorldPosition, BlockState pBlockState){
		this(ArcanaTiles.ASPECT_TUBE_TE.get(), pWorldPosition, pBlockState);
	}
	
	public TubeBlockEntity(BlockEntityType<?> type, BlockPos pWorldPosition, BlockState pBlockState){
		super(type, pWorldPosition, pBlockState);
	}
	
	public void tick(){
		// Move every speck along by (speed / 20f).
		// If there is a connection in the speck's direction, keep moving it until its position exceeds 0.5f.
			// When it does, pass it to pipes or insert it into AspectHandlers.
		// If not, keep moving it until its position exceeds SIZE.
			// Then make it move in the direction of a connection.
			// Prefer down, then random horizontals, then up.
		// If a speck exceeds SIZE perpendicularly to their direction (how?), bring it back to the centre.
		// If there's too many specks (1000?), explode.
		if(specks.size() > MAX_SPECKS){
			specks.clear();
			// also explode or smth
		}
		List<AspectSpeck> toRemove = new ArrayList<>();
		for(AspectSpeck speck : specks){
			Direction dir = speck.direction;
			speck.pos += speck.speed / 20f;
			speck.stuck = false;
			BlockState state = getLevel().getBlockState(worldPosition);
			boolean connected = connectedTo(dir);
			float max = connected ? 1 : .5f;
			Optional<Direction> forcedDir = redirect(speck, connected);
			if(forcedDir.isPresent() && speck.pos >= .5f && speck.pos <= max){
				speck.direction = forcedDir.get();
				setChanged();
			}else if(speck.pos > max){
				setChanged();
				// transfer, pass, or bounce
				BlockPos dest = worldPosition.relative(dir);
				BlockEntity te = level.getBlockEntity(dest);
				if(te instanceof TubeBlockEntity && connected){
					TubeBlockEntity tube = (TubeBlockEntity) te;
					if(tube.enabled()){
						toRemove.add(speck);
						tube.addSpeck(speck);
						speck.pos = speck.pos % 1;
					}
				}else if(AspectHandler.getOptional(te).isPresent() && connected){
					float inserted = AspectHandler.getFrom(te).insert(speck.payload);
					if(inserted >= speck.payload.getAmount())
						toRemove.add(speck);
					else{
						speck.payload = new AspectStack(speck.payload.getAspect(), speck.payload.getAmount() - inserted);
						speck.direction = speck.direction.getOpposite();
						speck.pos = 1 - speck.pos;
						if(speck.payload.getAmount() < 0.5) // remove specks that can't output
							toRemove.add(speck);
					}
				}else if(!forcedDir.isPresent()){ // random bounce
					if(connectedTo(Direction.DOWN) && dir != Direction.UP)
						speck.direction = Direction.DOWN;
					else if(connectedTo(Direction.NORTH) || connectedTo(Direction.SOUTH) || connectedTo(Direction.EAST) || connectedTo(Direction.WEST)){
						List<Direction> directions = new ArrayList<>();
						if(connectedTo(Direction.NORTH)) directions.add(Direction.NORTH);
						if(connectedTo(Direction.SOUTH)) directions.add(Direction.SOUTH);
						if(connectedTo(Direction.EAST)) directions.add(Direction.EAST);
						if(connectedTo(Direction.WEST)) directions.add(Direction.WEST);
						if(directions.size() > 1) directions.remove(dir.getOpposite()); // don't bounce back if possible
						speck.direction = directions.get(getLevel().random.nextInt(directions.size()));
					}else if(connectedTo(Direction.UP))
						speck.direction = Direction.UP;
				}else // forced direction
					if(connectedTo(forcedDir.get()))
						speck.direction = forcedDir.get();
				
				if(!toRemove.contains(speck) && speck.direction == dir){
					// We can't output or redirect it
					speck.pos = 0.5f;
					speck.stuck = true;
				}
			}
		}
		specks.removeAll(toRemove);
	}
	
	protected boolean connectedTo(Direction dir){
		BlockState state = getLevel().getBlockState(worldPosition);
		if(!state.getValue(PipeBlock.PROPERTY_BY_DIRECTION.get(dir)))
			return false;
		BlockEntity target = level.getBlockEntity(worldPosition.relative(dir));
		if(target instanceof TubeBlockEntity){
			TubeBlockEntity tube = (TubeBlockEntity) target;
			return tube.enabled();
		}else if(target != null){
			AspectHandler vis = AspectHandler.getFrom(target);
			// add up the available space (capacity - amount) of all holders
			// voiding holders are always considered to have 1 space available - we only check == 0 anyways
			return vis == null || vis.getHolders().stream().mapToDouble(holder -> holder.voids() ? 1 : holder.getCapacity() - holder.getStack().getAmount()).sum() != 0;
		}
		return true;
	}
	
	protected Optional<Direction> redirect(AspectSpeck speck, boolean canPass){
		return Optional.empty();
	}
	
	public void addSpeck(AspectSpeck speck){
		// don't add specks that can't transfer
		if(speck.payload.getAmount() >= 0.5)
			specks.add(speck);
	}
	
	public List<AspectSpeck> getSpecks(){
		return specks;
	}
	
	public boolean enabled(){
		return true;
	}
	
	public void saveAdditional(CompoundTag compound){
		super.saveAdditional(compound);
		CompoundTag tag = new CompoundTag();
		ListTag specks = new ListTag();
		for(AspectSpeck speck : this.specks)
			specks.add(speck.toNbt());
		tag.put("specks", specks);
	}
	
	public void load(CompoundTag nbt){
		super.load(nbt);
		ListTag specksList = nbt.getList("specks", Tag.TAG_COMPOUND);
		specks.clear();
		for(Tag speckInbt : specksList){
			CompoundTag speckTag = (CompoundTag)speckInbt;
			specks.add(AspectSpeck.fromNbt(speckTag));
		}
	}
	
	public CompoundTag getUpdateTag(){
		return new CompoundTag();
	}
}