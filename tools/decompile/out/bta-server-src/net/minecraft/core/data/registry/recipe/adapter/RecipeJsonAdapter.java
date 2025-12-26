package net.minecraft.core.data.registry.recipe.adapter;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import net.minecraft.core.data.registry.recipe.RecipeEntryBase;

public interface RecipeJsonAdapter<T extends RecipeEntryBase<?, ?, ?>> extends JsonSerializer<T>, JsonDeserializer<T> {
}
