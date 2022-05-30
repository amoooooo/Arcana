package net.arcanamod.client.render.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.arcanamod.aspects.Aspect;
import net.arcanamod.aspects.AspectUtils;
import net.arcanamod.util.Codecs;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AspectHelixParticleOption implements ParticleOptions {
	
	public static final Codec<AspectHelixParticleOption> CODEC = RecordCodecBuilder.create(o ->
			o.group(Codecs.ASPECT_CODEC.fieldOf("aspect")
							.forGetter(e -> e.aspect),
					Codec.INT.fieldOf("life")
							.forGetter(e -> e.life),
					Codec.FLOAT.fieldOf("time")
							.forGetter(e -> e.time),
					Codecs.VECTOR_3D_CODEC.fieldOf("direction")
							.forGetter(e -> e.direction))
					.apply(o, AspectHelixParticleOption::new));
	
	public static final ParticleOptions.Deserializer<AspectHelixParticleOption> DESERIALIZER = new Deserializer<>(){
		public AspectHelixParticleOption fromCommand(ParticleType<AspectHelixParticleOption> particleType, StringReader reader) throws CommandSyntaxException{
			reader.expect(' ');
			Aspect aspect = AspectUtils.getAspectByName(reader.readStringUntil(' '));
			int life = reader.readInt();
			reader.expect(' ');
			float time = reader.readFloat();
			reader.expect(' ');
			reader.expect('[');
			double x = reader.readDouble();
			reader.expect(',');
			reader.skipWhitespace();
			double y = reader.readDouble();
			reader.expect(',');
			reader.skipWhitespace();
			double z = reader.readDouble();
			reader.expect(']');
			return new AspectHelixParticleOption(aspect, life, time, new Vec3(x, y, z));
		}
		
		public AspectHelixParticleOption fromNetwork(ParticleType<AspectHelixParticleOption> particleType, FriendlyByteBuf buffer){
			return new AspectHelixParticleOption(AspectUtils.getAspectByName(buffer.readUtf()), buffer.readInt(), buffer.readFloat(), new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()));
		}
	};
	
	private final Aspect aspect;
	private final int life;
	private final float time;
	private final Vec3 direction;
	
	public AspectHelixParticleOption(@Nullable Aspect aspect, int life, float time, Vec3 direction){
		this.aspect = aspect;
		this.life = life;
		this.time = time;
		this.direction = direction;
	}
	
	@Nullable
	public Aspect getAspect(){
		return aspect;
	}
	
	public int getLife(){
		return life;
	}
	
	public float getTime(){
		return time;
	}
	
	public Vec3 getDirection(){
		return direction;
	}
	
	public ParticleType<?> getType(){
		return ArcanaParticles.ASPECT_HELIX_PARTICLE.get();
	}
	
	public void writeToNetwork(FriendlyByteBuf buffer){
		buffer.writeUtf(aspect != null ? aspect.name() : "null");
		buffer.writeInt(life);
		buffer.writeFloat(time);
		buffer.writeDouble(direction.x);
		buffer.writeDouble(direction.y);
		buffer.writeDouble(direction.z);
	}
	
	public String writeToString(){
		// expose proper parameters at some point
		return ForgeRegistries.PARTICLE_TYPES.getKey(getType()).toString();
	}
}