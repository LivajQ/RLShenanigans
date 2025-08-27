package rlshenanigans.handlers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.io.InputStream;
import java.io.InputStreamReader;

public class RLSRecipeHandler {
    
    public static void init() {
        bypassCraftTweaker(
                new ResourceLocation("lycanitesmobs", "saddle_elemental"),
                "/assets/rlshenanigans/recipes/saddle_elemental.json"
        );
    }
    
    private static void bypassCraftTweaker(ResourceLocation id, String path) {
        if (ForgeRegistries.RECIPES.containsKey(id)) {
            System.err.println("Recipe already registered: " + id);
            return;
        }
        
        InputStream stream = RLSRecipeHandler.class.getResourceAsStream(path);
        if (stream == null) {
            System.err.println("Recipe file not found: " + path);
            return;
        }
        
        JsonObject json = new JsonParser().parse(new InputStreamReader(stream)).getAsJsonObject();
        JsonContext context = new JsonContext("rlshenanigans");
        IRecipe recipe = CraftingHelper.getRecipe(json, context);
        recipe.setRegistryName(id);
        ForgeRegistries.RECIPES.register(recipe);
    }
}