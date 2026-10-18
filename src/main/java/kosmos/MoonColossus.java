package kosmos;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * Der Mondkoloss: Endboss von Megamutz.
 * Hat eine Bossleiste, einen Bodenstampfer und ruft in Phase 2 und 3 Mondkaefer zu Hilfe.
 */
public class MoonColossus extends Monster {
    private final ServerBossEvent bossEvent = new ServerBossEvent(
            Component.translatable("entity.kosmos.moon_colossus"),
            BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.PROGRESS);
    private int slamCooldown = 120;
    private boolean phase2 = false;
    private boolean phase3 = false;

    public MoonColossus(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 50;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 220.0)
                .add(Attributes.ATTACK_DAMAGE, 9.0)
                .add(Attributes.MOVEMENT_SPEED, 0.23)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(Attributes.FOLLOW_RANGE, 48.0)
                .add(Attributes.ARMOR, 6.0);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(1, new FloatGoal(this));
        goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0, true));
        goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 0.6));
        goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 24.0F));
        goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public void setCustomName(@Nullable Component name) {
        super.setCustomName(name);
        bossEvent.setName(getDisplayName());
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        bossEvent.removePlayer(player);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        bossEvent.setProgress(getHealth() / getMaxHealth());

        if (getTarget() != null && --slamCooldown <= 0) {
            slam();
            slamCooldown = 100 + random.nextInt(60);
        }
        float ratio = getHealth() / getMaxHealth();
        if (!phase2 && ratio < 0.66F) {
            phase2 = true;
            summonBeetles(2);
        }
        if (!phase3 && ratio < 0.33F) {
            phase3 = true;
            summonBeetles(3);
        }
    }

    private void slam() {
        for (Player p : level().getEntitiesOfClass(Player.class, getBoundingBox().inflate(7.0, 3.0, 7.0))) {
            if (p.isCreative() || p.isSpectator()) {
                continue;
            }
            p.hurt(damageSources().mobAttack(this), 5.0F);
            Vec3 dir = p.position().subtract(position());
            dir = new Vec3(dir.x, 0.0, dir.z);
            if (dir.lengthSqr() > 1.0E-4) {
                dir = dir.normalize();
            }
            p.push(dir.x * 0.8, 0.9, dir.z * 0.8);
            p.hurtMarked = true;
        }
        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.EXPLOSION, getX(), getY() + 0.2, getZ(), 6, 2.0, 0.2, 2.0, 0.0);
        }
        playSound(SoundEvents.IRON_GOLEM_ATTACK, 2.0F, 0.5F);
    }

    private void summonBeetles(int count) {
        for (int i = 0; i < count; i++) {
            MoonBeetle beetle = ModEntities.MOON_BEETLE.get().create(level());
            if (beetle != null) {
                beetle.moveTo(getX() + (random.nextDouble() - 0.5) * 6.0, getY(),
                        getZ() + (random.nextDouble() - 0.5) * 6.0, random.nextFloat() * 360.0F, 0.0F);
                level().addFreshEntity(beetle);
            }
        }
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.IRON_GOLEM_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.IRON_GOLEM_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        playSound(SoundEvents.IRON_GOLEM_STEP, 1.0F, 0.7F);
    }
}
