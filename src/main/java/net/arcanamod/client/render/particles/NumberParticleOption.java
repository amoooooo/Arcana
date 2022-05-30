package net.arcanamod.client.render.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class NumberParticleOption implements ParticleOptions {
	
	public static final Codec<NumberParticleOption> CODEC = RecordCodecBuilder.create(o ->
			o.group(Codec.INT.fieldOf("count")
					.forGetter(e -> Integer.valueOf(e.count)))
					.apply(o, (c) -> new NumberParticleOption((char)c.intValue())));
	
	@SuppressWarnings("deprecation")
	public static final ParticleOptions.Deserializer<NumberParticleOption> DESERIALIZER = new ParticleOptions.Deserializer<>(){
		public NumberParticleOption fromCommand(ParticleType<NumberParticleOption> particleType, StringReader reader) throws CommandSyntaxException{
			reader.expect(' ');
			char c = reader.getRemaining().charAt(0);
			return new NumberParticleOption(c);
		}
		
		public NumberParticleOption fromNetwork(ParticleType<NumberParticleOption> particleType, FriendlyByteBuf buffer){
			return new NumberParticleOption(buffer.readChar());
		}
	};
	
	char count;
	
	public NumberParticleOption(char c){
		this.count = c;
	}
	
	public ParticleType<?> getType(){
		return ArcanaParticles.NUMBER_PARTICLE.get();
	}
	
	public void writeToNetwork(FriendlyByteBuf buffer){
		buffer.writeChar(count);
	}
	
	public String writeToString(){
		return Objects.requireNonNull(ForgeRegistries.PARTICLE_TYPES.getKey(this.getType())).toString() + " " + count;
	}
}