package kosmos;

import net.minecraft.network.chat.Component;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.ItemHandlerHelper;

/** Kommandant Vega, Ausbilder der Raumfahrtakademie. */
public class AcademyInstructor extends Villager {
    private static final int FEE = 3;

    public AcademyInstructor(EntityType<? extends Villager> type, Level level) {
        super(type, level);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS;
        }
        if (level().isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (RocketEntity.hasLicense(player)) {
            say(player, "have_1");
            say(player, "have_2");
            return InteractionResult.CONSUME;
        }
        if (count(player, ModItems.COSMIUM_INGOT.get()) >= FEE) {
            remove(player, ModItems.COSMIUM_INGOT.get(), FEE);
            ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(ModItems.PILOT_LICENSE.get()));
            ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(ModItems.SPACE_HELMET.get()));
            ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(ModItems.CREW_BADGE.get(), 2));
            say(player, "pass_1");
            say(player, "pass_2");
            say(player, "pass_3");
            say(player, "pass_4");
        } else {
            say(player, "intro_1");
            say(player, "intro_2");
            say(player, "intro_3");
            say(player, "intro_4");
            say(player, "intro_5");
        }
        return InteractionResult.CONSUME;
    }

    private static void say(Player player, String key) {
        player.displayClientMessage(Component.translatable("dialog.kosmos.instructor." + key), false);
    }

    private static int count(Player player, Item item) {
        int n = 0;
        Inventory inv = player.getInventory();
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack s = inv.getItem(i);
            if (s.is(item)) {
                n += s.getCount();
            }
        }
        return n;
    }

    private static void remove(Player player, Item item, int amount) {
        Inventory inv = player.getInventory();
        int left = amount;
        for (int i = 0; i < inv.getContainerSize() && left > 0; i++) {
            ItemStack s = inv.getItem(i);
            if (s.is(item)) {
                int take = Math.min(left, s.getCount());
                s.shrink(take);
                left -= take;
            }
        }
    }
}
