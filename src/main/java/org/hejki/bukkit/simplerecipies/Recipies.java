package org.hejki.bukkit.simplerecipies;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Plugin for load and register recipies.
 *
 * @author Petr Hejkal
 */
public class Recipies extends JavaPlugin {
    @Override
    public void onEnable() {
        saveDefaultConfig();

        List<Map<String, Object>> shapeless = (List<Map<String, Object>>) getConfig().getList("recipies");
        for (Map<String, Object> recipeConf : shapeless) {
            Object result = recipeConf.get("result");
            Integer amount = (Integer) recipeConf.get("amount");
            short damage = 0;
            List<String> ingredients = (List<String>) recipeConf.get("ingredients");

            if (result == null) {
                log("Recipe definition does not contain result type definition, this recipe was skipped.");
                continue;
            }

            if (ingredients == null || ingredients.isEmpty()) {
                log("Recipe definition does not contain ingredients, this recipe was skipped.");
                continue;
            }

            if (amount == null) {
                amount = 1;
            }

            int commaCount = StringUtils.countMatches(ingredients.get(0), ",");
            if (commaCount > 0) {
                // shaped recipe
                ShapedRecipe recipe = new ShapedRecipe(new Type(result).toItemStack(amount, damage));
                Map<Character, MaterialData> ingredientMap = new HashMap<Character, MaterialData>();
                List<String> shapes = new ArrayList<String>(3);

                char character = 'a';
                for (int i = 0; i < ingredients.size(); i++) {
                    String[] materials = ingredients.get(i).split(",");
                    StringBuilder shape = new StringBuilder(3);

                    for (String material : materials) {
                        Type type = new Type(material);
                        if (type.getMaterial() == Material.AIR) {
                            shape.append(" ");
                        } else {
                            shape.append(character);
                            ingredientMap.put(character, type.toMaterialData());
                            character++;
                        }
                    }
                    shapes.add(shape.toString());
                }

                recipe.shape(shapes.toArray(new String[0]));
                for (Map.Entry<Character, MaterialData> materialData : ingredientMap.entrySet()) {
                    recipe.setIngredient(materialData.getKey(), materialData.getValue());
                }

                getServer().addRecipe(recipe);
            } else {
                // shapeless recipe
                ShapelessRecipe recipe = new ShapelessRecipe(new Type(result).toItemStack(amount, damage));
                for (String ingredient : ingredients) {
                    recipe.addIngredient(new Type(ingredient).toMaterialData());
                }
                getServer().addRecipe(recipe);
            }

            log("Recipe for creating " + result + " was initialized.");
        }

        List<Map<String, Object>> furnaceRecipies = (List<Map<String, Object>>) getConfig().getList("furnace");
        for (Map<String, Object> furnaceRecipe : furnaceRecipies) {
            Type result = new Type(furnaceRecipe.get("result"));
            Type input = new Type(furnaceRecipe.get("input"));

            getServer().addRecipe(new FurnaceRecipe(result.toItemStack(1, (short) 0), input.toMaterialData()));
            log("Furnace recipe for creating " + result + " from " + input + " was initialized.");
        }


        super.onEnable();
    }

    public void log(String message) {
        System.out.println("[Recipies] " + message);
    }
}
