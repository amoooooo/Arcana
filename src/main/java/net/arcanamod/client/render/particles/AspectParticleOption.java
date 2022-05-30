package net.arcanamod.client.render.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AspectParticleOption implements ParticleOptions {
	
	public static final Codec<AspectParticleOption> CODEC = RecordCodecBuilder.create(o ->
			o.group(ResourceLocation.CODEC.fieldOf("aspectTexture")
							.forGetter(e -> e.aspectTexture))
					.apply(o, AspectParticleOption::new));
	
	public static final ParticleOptions.Deserializer<AspectParticleOption> DESERIALIZER = new ParticleOptions.Deserializer<>() {
		public AspectParticleOption fromCommand(ParticleType<AspectParticleOption> particleType, StringReader reader) throws CommandSyntaxException {
			reader.expect(' ');
			ResourceLocation rloc = new ResourceLocation(reader.getRemaining());
			return new AspectParticleOption(rloc);
		}

		public AspectParticleOption fromNetwork(ParticleType<AspectParticleOption> particleType, FriendlyByteBuf buffer) {
			return new AspectParticleOption(buffer.readResourceLocation());
		}
	};

	ResourceLocation aspectTexture;
	ParticleType<AspectParticleOption> type;

	public AspectParticleOption(ResourceLocation aspectTexture){
		this.type = ArcanaParticles.ASPECT_PARTICLE.get();
		this.aspectTexture = aspectTexture;
	}

	public ParticleType<?> getType(){
		return type;
	}

	public void writeToNetwork(FriendlyByteBuf buffer){
		buffer.writeResourceLocation(aspectTexture);
	}

	public String writeToString(){
		return Objects.requireNonNull(ForgeRegistries.PARTICLE_TYPES.getKey(this.getType())) + " " + aspectTexture.toString();
	}
}
