package kosmos;

import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Kosmos.MODID);

    public static final DeferredBlock<Block> COSMIUM_ORE = BLOCKS.registerBlock("cosmium_ore",
            props -> new DropExperienceBlock(UniformInt.of(2, 5), props),
            BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_CYAN)
                    .strength(3.5f, 3.5f).requiresCorrectToolForDrops().sound(SoundType.STONE));

    public static final DeferredBlock<Block> COSMIUM_BLOCK = BLOCKS.registerSimpleBlock("cosmium_block",
            BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_CYAN)
                    .strength(5.0f, 6.0f).requiresCorrectToolForDrops().sound(SoundType.METAL));

    public static final DeferredBlock<Block> LUNAR_ROCK = BLOCKS.registerSimpleBlock("lunar_rock",
            BlockBehaviour.Properties.of().mapColor(MapColor.STONE)
                    .strength(1.5f, 6.0f).requiresCorrectToolForDrops().sound(SoundType.STONE));

    public static final DeferredBlock<Block> LUNAR_BRICKS = BLOCKS.registerSimpleBlock("lunar_bricks",
            BlockBehaviour.Properties.of().mapColor(MapColor.STONE)
                    .strength(2.0f, 6.0f).requiresCorrectToolForDrops().sound(SoundType.STONE));

    public static final DeferredBlock<Block> HULL_PLATING = BLOCKS.registerSimpleBlock("hull_plating",
            BlockBehaviour.Properties.of().mapColor(MapColor.SNOW)
                    .strength(4.0f, 8.0f).requiresCorrectToolForDrops().sound(SoundType.COPPER));

    public static final DeferredBlock<Block> STARLIGHT_CRYSTAL = BLOCKS.registerSimpleBlock("starlight_crystal",
            BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE)
                    .strength(1.5f).lightLevel(state -> 12).sound(SoundType.AMETHYST));
}
