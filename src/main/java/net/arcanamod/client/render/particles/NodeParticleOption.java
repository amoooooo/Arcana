package net.arcanamod.client.render.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.arcanamod.util.Codecs;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.UUID;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class NodeParticleOption implements ParticleOptions {
	
	public static final Codec<NodeParticleOption> CODEC = RecordCodecBuilder.create(o ->
			o.group(Codecs.UUID_CODEC.fieldOf("node")
						.forGetter(e -> e.node),
					ResourceLocation.CODEC.fieldOf("nodeTexture")
						.forGetter(e -> e.nodeTexture))
				.apply(o, NodeParticleOption::new));
	
	public static final ParticleOptions.Deserializer<NodeParticleOption> DESERIALIZER = new ParticleOptions.Deserializer<>() {
		public NodeParticleOption fromCommand(ParticleType<NodeParticleOption> particleType, StringReader reader) throws CommandSyntaxException{
			reader.expect(' ');
			UUID uuid = UUID.fromString(reader.readStringUntil(' '));
			ResourceLocation rloc = new ResourceLocation(reader.getRemaining());
			return new NodeParticleOption(uuid, rloc);
		}
		
		public NodeParticleOption fromNetwork(ParticleType<NodeParticleOption> particleType, FriendlyByteBuf buffer) {
			return new NodeParticleOption(buffer.readUUID(), buffer.readResourceLocation());
		}
	};
	
	UUID node;
	ResourceLocation nodeTexture;
	ParticleType<NodeParticleOption> type;
	
	public NodeParticleOption(UUID node, ResourceLocation nodeTexture){
		this.node = node;
		this.type = ArcanaParticles.NODE_PARTICLE.get();
		this.nodeTexture = nodeTexture;
	}
	
	public ParticleType<?> getType(){
		return type;
	}
	
	public void writeToNetwork(FriendlyByteBuf buffer){
		buffer.writeUUID(node);
		buffer.writeResourceLocation(nodeTexture);
	}
	
	public String writeToString(){
		return Objects.requireNonNull(ForgeRegistries.PARTICLE_TYPES.getKey(this.getType())) + " " + node.toString() + " " + nodeTexture.toString();
	}
}