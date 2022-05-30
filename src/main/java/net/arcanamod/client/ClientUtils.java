package net.arcanamod.client;

import net.arcanamod.client.gui.CompletePuzzleToast;
import net.arcanamod.client.gui.ResearchBookScreen;
import net.arcanamod.client.gui.ResearchEntryScreen;
import net.arcanamod.client.gui.ScribbledNoteScreen;
import net.arcanamod.event.ResearchEvent;
import net.arcanamod.systems.research.ResearchBooks;
import net.arcanamod.systems.research.ResearchEntry;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class ClientUtils {
    public static void openResearchBookUI(ResourceLocation book, Screen parentScreen, ItemStack sender){
        if(!ResearchBooks.disabled.contains(book))
            Minecraft.getInstance().setScreen(new ResearchBookScreen(ResearchBooks.books.get(book), parentScreen, sender));
        else
            Minecraft.getInstance().player.sendMessage(new TranslatableComponent("message.arcana.disabled").setStyle(Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.RED))), Util.NIL_UUID);
    }

    public static void openScribbledNotesUI(){
        Minecraft.getInstance().setScreen(new ScribbledNoteScreen(new TextComponent("")));
    }

    public static void displayPuzzleToast(@Nullable ResearchEntry entry){
        Minecraft.getInstance().getToasts().addToast(new CompletePuzzleToast(entry));
    }

    public static void onResearchChange(ResearchEvent event){
        if(Minecraft.getInstance().screen instanceof ResearchEntryScreen)
            ((ResearchEntryScreen)Minecraft.getInstance().screen).updateButtons();
    }
}
