package kosmos;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public class ModDimensions {
    public static final ResourceKey<Level> MOON = ResourceKey.create(Registries.DIMENSION, Kosmos.id("moon"));
}
