package net.arcanamod.client.render.particles;

import com.mojang.serialization.Codec;
import net.arcanamod.Arcana;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nonnull;
import java.util.function.Function;

public class ArcanaParticles{
	
	public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Arcana.MODID);
	
	public static final RegistryObject<ParticleType<NodeParticleOption>> NODE_PARTICLE = PARTICLE_TYPES.register("node_particle", () -> create(NodeParticleOption.DESERIALIZER, __ -> NodeParticleOption.CODEC));
	public static final RegistryObject<ParticleType<AspectParticleOption>> ASPECT_PARTICLE = PARTICLE_TYPES.register("aspect_particle", () -> create(AspectParticleOption.DESERIALIZER, __ -> AspectParticleOption.CODEC));
	public static final RegistryObject<ParticleType<AspectHelixParticleOption>> ASPECT_HELIX_PARTICLE = PARTICLE_TYPES.register("aspect_helix_particle", () -> create(AspectHelixParticleOption.DESERIALIZER, __ -> AspectHelixParticleOption.CODEC));
	public static final RegistryObject<ParticleType<NumberParticleOption>> NUMBER_PARTICLE = PARTICLE_TYPES.register("number_particle", () -> create(NumberParticleOption.DESERIALIZER, __ -> NumberParticleOption.CODEC));
	public static final RegistryObject<ParticleType<BlockParticleOption>> HUNGRY_NODE_BLOCK_PARTICLE = PARTICLE_TYPES.register("hungry_node_block_particle", () -> create(BlockParticleOption.DESERIALIZER, BlockParticleOption::codec));
	public static final RegistryObject<ParticleType<BlockParticleOption>> HUNGRY_NODE_DISC_PARTICLE = PARTICLE_TYPES.register("hungry_node_disc_particle", () -> create(BlockParticleOption.DESERIALIZER, BlockParticleOption::codec));
	
	@SuppressWarnings("deprecation")
	private static <T extends ParticleOptions> ParticleType<T> create(ParticleOptions.Deserializer<T> deserializer, final Function<ParticleType<T>, Codec<T>> codec){
		return new ParticleType<>(true, deserializer){
			@Nonnull
			public Codec<T> codec(){
				return codec.apply(this);
			}
		};
	}
}