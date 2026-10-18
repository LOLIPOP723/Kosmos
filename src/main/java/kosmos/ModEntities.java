package kosmos;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(Registries.ENTITY_TYPE, Kosmos.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<RocketEntity>> ROCKET = ENTITIES.register("rocket",
            () -> EntityType.Builder.<RocketEntity>of(RocketEntity::new, MobCategory.MISC)
                    .sized(1.2f, 4.0f).clientTrackingRange(16).updateInterval(1).fireImmune().build("rocket"));

    public static final DeferredHolder<EntityType<?>, EntityType<MoonBeetle>> MOON_BEETLE = ENTITIES.register("moon_beetle",
            () -> EntityType.Builder.<MoonBeetle>of(MoonBeetle::new, MobCategory.MONSTER)
                    .sized(0.7f, 0.45f).clientTrackingRange(8).build("moon_beetle"));

    public static final DeferredHolder<EntityType<?>, EntityType<MoonColossus>> MOON_COLOSSUS = ENTITIES.register("moon_colossus",
            () -> EntityType.Builder.<MoonColossus>of(MoonColossus::new, MobCategory.MONSTER)
                    .sized(1.8f, 4.2f).clientTrackingRange(16).fireImmune().build("moon_colossus"));

    public static final DeferredHolder<EntityType<?>, EntityType<AcademyInstructor>> ACADEMY_INSTRUCTOR = ENTITIES.register("academy_instructor",
            () -> EntityType.Builder.<AcademyInstructor>of(AcademyInstructor::new, MobCategory.MISC)
                    .sized(0.6f, 1.95f).clientTrackingRange(10).build("academy_instructor"));

    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(MOON_BEETLE.get(), MoonBeetle.createAttributes().build());
        event.put(MOON_COLOSSUS.get(), MoonColossus.createAttributes().build());
        event.put(ACADEMY_INSTRUCTOR.get(), Villager.createAttributes().build());
    }

    public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        event.register(MOON_BEETLE.get(), SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkAnyLightMonsterSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.OR);
    }
}
