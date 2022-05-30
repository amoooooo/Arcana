package net.arcanamod.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.arcanamod.world.AuraView;
import net.arcanamod.world.ServerAuraView;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class TaintCommand{
	
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher){
		// arcana-taint get <position>
		// arcana-taint add <position> <amount>
		// arcana-taint set <position> <amount>
		// simple, right?
		dispatcher.register(
				literal("arcana-taint").requires(source -> source.hasPermission(2))
						.then(literal("get")
								.then(argument("position", Vec3Argument.vec3())
										.executes(TaintCommand::get)
								)
						)
						.then(literal("add")
								.then(argument("position", Vec3Argument.vec3())
										.then(argument("amount", IntegerArgumentType.integer())
												.executes(TaintCommand::add)
										)
								)
						)
						.then(literal("set")
								.then(argument("position", Vec3Argument.vec3())
										.then(argument("amount", IntegerArgumentType.integer())
												.executes(TaintCommand::set)
										)
								)
						)
		);
	}
	
	public static int get(CommandContext<CommandSourceStack> ctx){
		AuraView view = new ServerAuraView(ctx.getSource().getLevel());
		float get = view.getFluxAt(Vec3Argument.getCoordinates(ctx, "position").getBlockPos(ctx.getSource()));
		if(get == -1){
			ctx.getSource().sendFailure(new TranslatableComponent("commands.arcana.taint.error"));
			return 0;
		}
		ctx.getSource().sendSuccess(new TranslatableComponent("commands.arcana.taint.taint_level", get), true);
		return 1;
	}
	
	public static int add(CommandContext<CommandSourceStack> ctx){
		AuraView view = new ServerAuraView(ctx.getSource().getLevel());
		BlockPos position = Vec3Argument.getCoordinates(ctx, "position").getBlockPos(ctx.getSource());
		int amount = IntegerArgumentType.getInteger(ctx, "amount");
		float get = view.addFluxAt(position, amount);
		if(get == -1){
			ctx.getSource().sendFailure(new TranslatableComponent("commands.arcana.taint.error"));
			return 0;
		}
		ctx.getSource().sendSuccess(new TranslatableComponent("commands.arcana.taint.added", amount, get, amount + get), true);
		return 1;
	}
	
	public static int set(CommandContext<CommandSourceStack> ctx){
		AuraView view = new ServerAuraView(ctx.getSource().getLevel());
		BlockPos position = Vec3Argument.getCoordinates(ctx, "position").getBlockPos(ctx.getSource());
		int amount = IntegerArgumentType.getInteger(ctx, "amount");
		float get = view.setFluxAt(position, amount);
		if(get == -1){
			ctx.getSource().sendFailure(new TranslatableComponent("commands.arcana.taint.error"));
			return 0;
		}
		ctx.getSource().sendSuccess(new TranslatableComponent("commands.arcana.taint.set", amount, get), true);
		return 1;
	}
}