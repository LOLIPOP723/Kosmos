package kosmos;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;

/**
 * Die Rakete. Mit Pilotenlizenz fliegt man von Hand (Blickrichtung = Flugrichtung),
 * ohne Lizenz uebernimmt der Autopilot. Zum Starten braucht man 2 Crewmitglieder.
 */
public class RocketEntity extends Entity {
    public static final String CREW_TAG = "kosmos_crew";

    private static final EntityDataAccessor<Integer> STATE =
            SynchedEntityData.defineId(RocketEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> ASCENDING =
            SynchedEntityData.defineId(RocketEntity.class, EntityDataSerializers.BOOLEAN);

    private static final int PARKED = 0;
    private static final int FLYING = 1;
    private static final int MAX_SEATS = 3;
    private static final double SPACE_Y = 330.0;
    private static final double ARRIVAL_Y = 280.0;

    private int hintCooldown = 0;
    private int flightTicks = 0;
    private boolean allowDismount = false;

    public RocketEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    // ---------- Daten ----------
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(STATE, PARKED);
        builder.define(ASCENDING, true);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        entityData.set(STATE, tag.getInt("State"));
        entityData.set(ASCENDING, tag.getBoolean("Ascending"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("State", entityData.get(STATE));
        tag.putBoolean("Ascending", entityData.get(ASCENDING));
    }

    private int state() {
        return entityData.get(STATE);
    }

    /** Wahr, wenn Spieler im Flug nicht aussteigen duerfen. */
    public boolean isDismountLocked() {
        return !isRemoved() && !allowDismount && state() == FLYING && hasPassengers();
    }

    // ---------- Interaktion ----------
    @Override
    public boolean isPickable() {
        return !isRemoved();
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return getPassengers().size() < MAX_SEATS;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (isInvulnerableTo(source)) {
            return false;
        }
        if (!level().isClientSide && !isRemoved() && source.getEntity() instanceof Player player
                && !hasPassengers() && state() == PARKED) {
            if (!player.getAbilities().instabuild) {
                spawnAtLocation(ModItems.ROCKET.get());
            }
            discard();
            return true;
        }
        return false;
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (player.isSecondaryUseActive()) {
            return InteractionResult.PASS;
        }
        if (level().isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (player.startRiding(this)) {
            player.displayClientMessage(Component.translatable(
                    hasLicense(player) ? "msg.kosmos.boarded_manual" : "msg.kosmos.boarded_auto"), true);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    @Override
    protected void positionRider(Entity passenger, Entity.MoveFunction callback) {
        if (!hasPassenger(passenger)) {
            return;
        }
        int index = getPassengers().indexOf(passenger);
        double[] offsets = {0.0, 0.3, -0.3};
        callback.accept(passenger, getX() + offsets[Math.max(0, Math.min(2, index))], getY() + 1.2, getZ());
    }

    // ---------- Hilfen ----------
    private Player getPilot() {
        for (Entity e : getPassengers()) {
            if (e instanceof Player p) {
                return p;
            }
        }
        return null;
    }

    static boolean hasLicense(Player player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            if (player.getInventory().getItem(i).is(ModItems.PILOT_LICENSE.get())) {
                return true;
            }
        }
        return false;
    }

    private int crewCount() {
        int n = 0;
        for (Entity e : getPassengers()) {
            if (e instanceof Villager v && v.getTags().contains(CREW_TAG)) {
                n++;
            }
        }
        return n;
    }

    private void tryBoardCrew() {
        if (tickCount % 10 != 0 || getPassengers().size() >= MAX_SEATS) {
            return;
        }
        List<Villager> candidates = level().getEntitiesOfClass(Villager.class,
                getBoundingBox().inflate(8.0, 3.0, 8.0),
                v -> v.getTags().contains(CREW_TAG) && !v.isPassenger() && !v.isBaby());
        for (Villager v : candidates) {
            if (getPassengers().size() >= MAX_SEATS) {
                break;
            }
            v.startRiding(this);
        }
    }

    // ---------- Hauptlogik ----------
    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            clientEffects();
            return;
        }

        Player pilot = getPilot();
        boolean manual = pilot != null && hasLicense(pilot);
        boolean moon = level().dimension().equals(ModDimensions.MOON);
        double gravity = moon ? 0.012 : 0.035;
        Vec3 vel = getDeltaMovement();

        if (state() == PARKED) {
            flightTicks = 0;
            if (pilot != null) {
                tryBoardCrew();
                if (pilot.zza > 0.5F) {
                    if (crewCount() >= 2) {
                        entityData.set(STATE, FLYING);
                        entityData.set(ASCENDING, true);
                        vel = new Vec3(0.0, 0.25, 0.0);
                        playSound(SoundEvents.FIREWORK_ROCKET_LAUNCH, 4.0F, 0.6F);
                    } else if (hintCooldown-- <= 0) {
                        pilot.displayClientMessage(Component.translatable("msg.kosmos.need_crew"), true);
                        hintCooldown = 40;
                    }
                }
            }
            if (state() == PARKED) {
                vel = new Vec3(0.0, onGround() ? 0.0 : vel.y - 0.04, 0.0);
            }
        }

        if (state() == FLYING) {
            flightTicks++;
            Vec3 aim = null;
            if (pilot == null) {
                vel = vel.add(0.0, -gravity, 0.0).scale(0.99);
            } else if (manual) {
                Vec3 look = pilot.getLookAngle();
                double forward = pilot.zza;
                double side = pilot.xxa;
                if (forward > 0) {
                    vel = vel.add(look.scale(0.06 * forward));
                } else if (forward < 0) {
                    vel = vel.scale(0.93);
                }
                if (side != 0) {
                    Vec3 left = new Vec3(0.0, 1.0, 0.0).cross(look);
                    if (left.lengthSqr() > 1.0E-4) {
                        vel = vel.add(left.normalize().scale(0.025 * side));
                    }
                }
                vel = vel.add(0.0, -gravity, 0.0).scale(0.985);
                aim = look;
            } else {
                boolean up = entityData.get(ASCENDING);
                double targetY = up ? 0.9 : -0.45;
                vel = new Vec3(vel.x * 0.9, vel.y + (targetY - vel.y) * 0.08, vel.z * 0.9);
            }
            double speed = vel.length();
            if (speed > 1.6) {
                vel = vel.scale(1.6 / speed);
            }
            Vec3 dir = aim != null ? aim : vel;
            if (dir.lengthSqr() > 1.0E-3) {
                Vec3 n = dir.normalize();
                setYRot((float) (Mth.atan2(-n.x, n.z) * 57.29578));
                setXRot((float) (Math.acos(Mth.clamp(n.y, -1.0, 1.0)) * 57.29578));
            }
            if (getY() >= SPACE_Y && pilot != null && (manual || entityData.get(ASCENDING))) {
                warp();
                return;
            }
        }

        setDeltaMovement(vel);
        move(MoverType.SELF, vel);

        if (state() == FLYING && flightTicks > 15 && onGround() && vel.y <= 0.02) {
            land(-vel.y, pilot);
        }
    }

    private void land(double impact, Player pilot) {
        entityData.set(STATE, PARKED);
        setDeltaMovement(Vec3.ZERO);
        setXRot(0.0F);
        boolean hard = impact > 0.75;
        if (hard) {
            for (Entity e : getPassengers()) {
                if (e instanceof LivingEntity living) {
                    living.hurt(damageSources().flyIntoWall(), (float) Math.min(8.0, (impact - 0.75) * 12.0));
                }
            }
        }
        playSound(SoundEvents.ANVIL_LAND, 1.0F, hard ? 0.6F : 1.0F);
        if (pilot != null) {
            pilot.displayClientMessage(Component.translatable(
                    hard ? "msg.kosmos.hard_landing" : "msg.kosmos.soft_landing"), true);
        }
    }

    private void warp() {
        if (!(level() instanceof ServerLevel from)) {
            return;
        }
        boolean toEarth = from.dimension().equals(ModDimensions.MOON);
        ServerLevel dest = from.getServer().getLevel(toEarth ? Level.OVERWORLD : ModDimensions.MOON);
        if (dest == null) {
            return;
        }
        Vec3 target = new Vec3(getX(), ARRIVAL_Y, getZ());
        List<Entity> riders = new ArrayList<>(getPassengers());

        allowDismount = true;
        ejectPassengers();
        allowDismount = false;

        Entity moved = changeDimension(new DimensionTransition(dest, target, Vec3.ZERO, getYRot(), 0.0F,
                DimensionTransition.DO_NOTHING));
        for (Entity rider : riders) {
            Entity r = rider.changeDimension(new DimensionTransition(dest, target, Vec3.ZERO,
                    rider.getYRot(), rider.getXRot(), DimensionTransition.DO_NOTHING));
            if (r != null && moved != null) {
                r.startRiding(moved, true);
            }
            if (r instanceof Player p) {
                p.displayClientMessage(Component.translatable(
                        toEarth ? "msg.kosmos.arrive_earth" : "msg.kosmos.arrive_moon"), true);
            }
        }
        if (moved instanceof RocketEntity newRocket) {
            newRocket.entityData.set(ASCENDING, false);
            newRocket.setXRot(0.0F);
            newRocket.setDeltaMovement(0.0, -0.1, 0.0);
        }
    }

    // ---------- Effekte (Client) ----------
    private void clientEffects() {
        if (state() != FLYING) {
            return;
        }
        float yaw = getYRot() * Mth.DEG_TO_RAD;
        float tilt = getXRot() * Mth.DEG_TO_RAD;
        double dx = -Mth.sin(yaw) * Mth.sin(tilt);
        double dy = Mth.cos(tilt);
        double dz = Mth.cos(yaw) * Mth.sin(tilt);
        for (int i = 0; i < 3; i++) {
            level().addParticle(ParticleTypes.FLAME,
                    getX() - dx * 0.4 + (random.nextDouble() - 0.5) * 0.4,
                    getY() - dy * 0.4 + (random.nextDouble() - 0.5) * 0.2,
                    getZ() - dz * 0.4 + (random.nextDouble() - 0.5) * 0.4,
                    -dx * 0.25, -dy * 0.25, -dz * 0.25);
        }
        level().addParticle(ParticleTypes.LARGE_SMOKE,
                getX() - dx * 0.9, getY() - dy * 0.9, getZ() - dz * 0.9,
                -dx * 0.1, -dy * 0.1, -dz * 0.1);
    }
}
