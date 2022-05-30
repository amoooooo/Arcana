package net.arcanamod.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.arcanamod.capabilities.Researcher;
import net.arcanamod.network.Connection;
import net.arcanamod.network.PkModifyResearch;
import net.arcanamod.systems.research.ResearchBooks;
import net.arcanamod.systems.research.ResearchEntry;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.concurrent.atomic.AtomicInteger;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class ResearchCommand{
	
	private static final SuggestionProvider<CommandSourceStack> SUGGEST_RESEARCH = (ctx, builder) -> SharedSuggestionProvider.suggestResource(ResearchBooks.streamEntries().map(ResearchEntry::key), builder);
	
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher){
		dispatcher.register(
				literal("arcana-research").requires(source -> source.hasPermission(2))
				.then(argument("targets", EntityArgument.players())
						.then(literal("complete-all").executes(ResearchCommand::giveAll))
						.then(literal("reset-all").executes(ResearchCommand::resetAll))
						.then(literal("complete").then(argument("research", ResourceLocationArgument.id()).executes(context -> modify(context, Diff.complete)).suggests(SUGGEST_RESEARCH)))
						.then(literal("try-advance").then(argument("research", ResourceLocationArgument.id()).executes(context -> modify(context, Diff.advance)).suggests(SUGGEST_RESEARCH)))
						.then(literal("force-advance").then(argument("research", ResourceLocationArgument.id()).executes(context -> modify(context, Diff.forceAdvance)).suggests(SUGGEST_RESEARCH)))
						.then(literal("reset").then(argument("research", ResourceLocationArgument.id()).executes(context -> modify(context, Diff.reset)).suggests(SUGGEST_RESEARCH)))
				)
				// arcana-research reload would go here
		);
		
		// arcana-research targets give-all/reset-all | arcana-research targets give/try-advance/force-advance/reset research
	}
	
	public static int giveAll(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException{
		// return number of players affected successfully
		AtomicInteger ret = new AtomicInteger();
		EntityArgument.getPlayers(ctx, "targets").forEach(entity -> {
			ResearchBooks.streamEntries().forEach(entry -> {
				Researcher from = Researcher.getFrom(entity);
				if(from != null){
					from.completeEntry(entry);
					Connection.sendModifyResearch(PkModifyResearch.Diff.complete, entry.key(), entity);
				}
			});
			ret.getAndIncrement();
		});
		return ret.get();
	}
	
	public static int resetAll(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException{
		// return number of players affected successfully
		AtomicInteger ret = new AtomicInteger();
		EntityArgument.getPlayers(ctx, "targets").forEach(entity -> {
			ResearchBooks.streamEntries().forEach(entry -> {
				Researcher from = Researcher.getFrom(entity);
				if(from != null){
					from.resetEntry(entry);
					Connection.sendModifyResearch(PkModifyResearch.Diff.reset, entry.key(), entity);
				}
			});
			ret.getAndIncrement();
		});
		return ret.get();
	}
	
	public static int modify(CommandContext<CommandSourceStack> ctx, Diff diff) throws CommandSyntaxException{
		AtomicInteger ret = new AtomicInteger();
		EntityArgument.getPlayers(ctx, "targets").forEach(entity -> {
			ResourceLocation key = ResourceLocationArgument.getId(ctx, "research");
			ResearchEntry entry = ResearchBooks.streamEntries().filter(e -> e.key().equals(key)).findFirst().orElseThrow(() -> {
				Message noSuchEntry = new TranslatableComponent("commands.arcana.research.no_entry", key.toString());
				return new RuntimeException(new CommandSyntaxException(new SimpleCommandExceptionType(noSuchEntry), noSuchEntry));
			});
			Researcher researcher = Researcher.getFrom(entity);
			if(researcher != null){
				if(diff == Diff.complete){
					researcher.completeEntry(entry);
					Connection.sendModifyResearch(PkModifyResearch.Diff.complete, key, entity);
				}else if(diff == Diff.advance){
					if(Researcher.canAdvanceEntry(researcher, entry)){
						Researcher.takeRequirementsAndAdvanceEntry(researcher, entry);
						Connection.sendModifyResearch(PkModifyResearch.Diff.advance, key, entity);
					}
				}else if(diff == Diff.forceAdvance){
					researcher.advanceEntry(entry);
					Connection.sendModifyResearch(PkModifyResearch.Diff.advance, key, entity);
				}else{
					researcher.resetEntry(entry);
					Connection.sendModifyResearch(PkModifyResearch.Diff.reset, key, entity);
				}
				ret.getAndIncrement();
			}
		});
		return ret.get();
	}
	
	private enum Diff{
		complete, advance, forceAdvance, reset
	}
}