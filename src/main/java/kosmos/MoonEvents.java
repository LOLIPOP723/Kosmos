package kosmos;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityMountEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/** Mond-Regeln: wenig Schwerkraft, kein Sauerstoff ohne Raumhelm. Dazu: Kein Aussteigen im Flug. */
@EventBusSubscriber(modid = Kosmos.MODID)
public class MoonEvents {
    private static final ResourceLocation MOON_GRAVITY = Kosmos.id("moon_gravity");
    private static final ResourceLocation MOON_SAFE_FALL = Kosmos.id("moon_safe_fall");

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) {
            return;
        }
        boolean onMoon = player.level().dimension().equals(ModDimensions.MOON);

        AttributeInstance gravity = player.getAttribute(Attributes.GRAVITY);
        if (gravity != null) {
            boolean has = gravity.hasModifier(MOON_GRAVITY);
            if (onMoon && !has) {
                gravity.addTransientModifier(new AttributeModifier(MOON_GRAVITY, -0.7,
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
            } else if (!onMoon && has) {
                gravity.removeModifier(MOON_GRAVITY);
            }
        }
        AttributeInstance safeFall = player.getAttribute(Attributes.SAFE_FALL_DISTANCE);
        if (safeFall != null) {
            boolean has = safeFall.hasModifier(MOON_SAFE_FALL);
            if (onMoon && !has) {
                safeFall.addTransientModifier(new AttributeModifier(MOON_SAFE_FALL, 12.0,
                        AttributeModifier.Operation.ADD_VALUE));
            } else if (!onMoon && has) {
                safeFall.removeModifier(MOON_SAFE_FALL);
            }
        }

        if (onMoon && player.tickCount % 40 == 0 && !player.isCreative() && !player.isSpectator()
                && !(player.getVehicle() instanceof RocketEntity)
                && !player.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.SPACE_HELMET.get())) {
            player.hurt(player.damageSources().drown(), 1.0F);
            player.displayClientMessage(Component.translatable("msg.kosmos.no_oxygen"), true);
        }
    }

    @SubscribeEvent
    public static void onMount(EntityMountEvent event) {
        if (event.isDismounting()
                && event.getEntityBeingMounted() instanceof RocketEntity rocket
                && event.getEntityMounting() instanceof Player player
                && rocket.isDismountLocked()) {
            event.setCanceled(true);
            if (!player.level().isClientSide) {
                player.displayClientMessage(Component.translatable("msg.kosmos.cannot_leave"), true);
            }
        }
    }
}
