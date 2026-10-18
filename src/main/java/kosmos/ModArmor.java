package kosmos;

import java.util.EnumMap;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModArmor {
    public static final DeferredRegister<ArmorMaterial> MATERIALS =
            DeferredRegister.create(Registries.ARMOR_MATERIAL, Kosmos.MODID);

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> SPACE_SUIT = MATERIALS.register("space_suit",
            () -> new ArmorMaterial(
                    Util.make(new EnumMap<ArmorItem.Type, Integer>(ArmorItem.Type.class), map -> {
                        map.put(ArmorItem.Type.BOOTS, 2);
                        map.put(ArmorItem.Type.LEGGINGS, 5);
                        map.put(ArmorItem.Type.CHESTPLATE, 6);
                        map.put(ArmorItem.Type.HELMET, 2);
                        map.put(ArmorItem.Type.BODY, 5);
                    }),
                    12,
                    SoundEvents.ARMOR_EQUIP_IRON,
                    () -> Ingredient.of(ModItems.COSMIUM_INGOT.get()),
                    List.of(new ArmorMaterial.Layer(Kosmos.id("space_suit"))),
                    1.0F,
                    0.0F));
}
