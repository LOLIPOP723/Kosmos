package kosmos;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class CrewBadgeItem extends Item {
    public CrewBadgeItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (target instanceof Villager villager && !(villager instanceof AcademyInstructor) && !villager.isBaby()) {
            if (player.level().isClientSide) {
                return InteractionResult.SUCCESS;
            }
            if (villager.getTags().contains(RocketEntity.CREW_TAG)) {
                player.displayClientMessage(Component.translatable("msg.kosmos.already_crew"), true);
                return InteractionResult.CONSUME;
            }
            villager.addTag(RocketEntity.CREW_TAG);
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            if (player.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, villager.getX(), villager.getY() + 1.2,
                        villager.getZ(), 10, 0.4, 0.5, 0.4, 0.0);
            }
            player.displayClientMessage(Component.translatable("msg.kosmos.crew_joined"), true);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }
}
