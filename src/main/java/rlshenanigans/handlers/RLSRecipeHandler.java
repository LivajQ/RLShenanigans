package rlshenanigans.handlers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class RLSRecipeHandler {
    
    public static void bypassCraftTweaker(ResourceLocation id, ResourceLocation file) {
        if (ForgeRegistries.RECIPES.containsKey(id)) {
            System.out.println("Recipe already registered: " + id);
            return;
        }
        
        try {
            InputStream stream = Minecraft.getMinecraft().getResourceManager().getResource(file).getInputStream();
            JsonObject json = new JsonParser().parse(new InputStreamReader(stream)).getAsJsonObject();
            
            JsonContext context = new JsonContext(file.getNamespace());
            IRecipe recipe = CraftingHelper.getRecipe(json, context);
            recipe.setRegistryName(id);
            ForgeRegistries.RECIPES.register(recipe);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load recipe: " + id, e);
        }
    }
}