package net.arcanamod.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.arcanamod.aspects.Aspect;
import net.arcanamod.aspects.AspectStack;
import net.arcanamod.aspects.AspectUtils;
import net.arcanamod.aspects.handlers.AspectHandler;
import net.arcanamod.items.MagicDeviceItem;
import net.arcanamod.items.PhialItem;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class FillAspectCommand {
	private static final SuggestionProvider<CommandSourceStack> SUGGEST_FILL_CONTAINER = (ctx, builder) -> SharedSuggestionProvider.suggestResource(Arrays.stream(AspectUtils.primalAspects).map(Aspect::toResourceLocation), builder);

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher){
		dispatcher.register(
				literal("arcana-aspect").requires(source -> source.hasPermission(2))
						.then(literal("fill")
								.then(argument("targets", EntityArgument.players())
										.then(argument("amount", IntegerArgumentType.integer())
												.then(argument("aspect", ResourceLocationArgument.id()).executes(FillAspectCommand::fill).suggests(SUGGEST_FILL_CONTAINER))))

						)
		);
	}

	public static int fill(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		// return number of players affected successfully
		AtomicInteger ret = new AtomicInteger();
		EntityArgument.getPlayers(ctx, "targets").forEach(serverPlayerEntity -> {
			ItemStack is = serverPlayerEntity.getMainHandItem();
			AspectHandler vis = AspectHandler.getFrom(is);
			ResourceLocation aspect_name = ResourceLocationArgument.getId(ctx, "aspect");
			int amount = IntegerArgumentType.getInteger(ctx, "amount");
			if(vis != null){
				if(is.getItem() instanceof MagicDeviceItem || is.getItem() instanceof PhialItem){
					Aspect targettedStack = AspectUtils.getAspectByName(aspect_name.getPath());
					if(targettedStack != null){
						for (int i = 0; i < 6; i++) {
							vis.getHolder(i).insert(new AspectStack(targettedStack, amount), false);
						}
						if(is.getTag() == null)
							is.setTag(is.getShareTag());
					}else
						serverPlayerEntity.sendMessage(new TranslatableComponent("commands.arcana.fill.invalid_aspect", aspect_name).withStyle(ChatFormatting.RED), Util.NIL_UUID);
				}else
					serverPlayerEntity.sendMessage(new TranslatableComponent("commands.arcana.fill.invalid_item", is.getItem().getRegistryName().toString()).withStyle(ChatFormatting.RED), Util.NIL_UUID);
			}else
				serverPlayerEntity.sendMessage(new TranslatableComponent("commands.arcana.fill.invalid_item", is.getItem().getRegistryName().toString()).withStyle(ChatFormatting.RED), Util.NIL_UUID);
			ret.getAndIncrement();
		});
		return ret.get();
	}
}
