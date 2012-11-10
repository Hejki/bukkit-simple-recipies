package org.hejki.bukkit.simplerecipies;

import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

class Type {
    private Material material;
    private byte data;

    Type(Number materialId) {
        material = Material.getMaterial(materialId.intValue());
    }

    Type(Object definition) {
        String materialDef = definition.toString();

        if (materialDef.contains(":")) {
            String[] split = materialDef.split(":");
            materialDef = split[0];
            setData(split[1]);
        }

        if (NumberUtils.isDigits(materialDef)) {
            material = Material.getMaterial(NumberUtils.toInt(materialDef));
        } else {
            material = Material.getMaterial(materialDef);
        }
    }

    private void setData(String representation) {
        try {
            data = Byte.valueOf(representation);
        } catch (NumberFormatException e) {
            System.out.println("Cannot convert data value: " + representation + " to byte.");
        }
    }

    public Material getMaterial() {
        return material;
    }

    public byte getData() {
        return data;
    }

    public ItemStack toItemStack(int amount, short damage) {
        return new ItemStack(material, amount, damage, data);
    }

    public MaterialData toMaterialData() {
        return new MaterialData(material, data);
    }
}