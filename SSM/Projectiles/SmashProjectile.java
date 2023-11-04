package SSM.Projectiles;

import SSM.SSM;
import SSM.Utilities.DamageUtil;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public abstract class SmashProjectile extends BukkitRunnable implements Listener {

    protected static JavaPlugin plugin = SSM.getInstance();
    protected Player firer;
    protected String name;
    protected Entity projectile;
    protected double damage;
    protected double hitbox_mult;
    protected double knockback_mult;
    protected long expiration_ticks = 300;

    public SmashProjectile(Player firer, String name) {
        this.firer = firer;
        this.name = name;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void launchProjectile() {
        setProjectileEntity(createProjectileEntity());
        this.doVelocity();
        this.runTaskTimer(plugin, 0L, 0L);
    }

    @Override
    public void run() {
        if(projectile == null) {
            destroy();
            return;
        }
        if(projectile.getTicksLived() > getExpirationTicks()) {
            if(onExpire()) {
                destroy();
                return;
            }
        }
        // Check if we hit an entity first
        LivingEntity target = checkClosestTarget();
        if(target != null) {
            if(onHitLivingEntity(target)) {
                firer.playSound(firer.getLocation(), Sound.ORB_PICKUP, 1.0f, 1.25f);
                destroy();
                return;
            }
        }
        // Check if we hit a block next
        Block block = checkHitBlock();
        if(block != null) {
            if(onHitBlock(block)) {
                destroy();
                return;
            }
        }
        // Check if we're idle next
        if(checkIdle()) {
            if(onIdle()) {
                destroy();
                return;
            }
        }
        doEffect();
    }

    public void destroy() {
        projectile.remove();
        this.cancel();
    }

    protected LivingEntity checkClosestTarget() {
        net.minecraft.server.v1_8_R3.World world = ((CraftWorld) projectile.getWorld()).getHandle();
        net.minecraft.server.v1_8_R3.Entity entity = ((CraftEntity) projectile).getHandle();
        // Do a raytrace to see what our real position is going to be
        Vec3D vec_old = new Vec3D(entity.locX, entity.locY, entity.locZ);
        Vec3D vec_new = new Vec3D(entity.locX + entity.motX, entity.locY + entity.motY, entity.locZ + entity.motZ);
        MovingObjectPosition final_position = entity.world.rayTrace(vec_old, vec_new, false, true, false);
        vec_old = new Vec3D(entity.locX, entity.locY, entity.locZ);
        vec_new = new Vec3D(entity.locX + entity.motX, entity.locY + entity.motY, entity.locZ + entity.motZ);
        if(final_position != null) {
            vec_new = new Vec3D(final_position.pos.a, final_position.pos.b, final_position.pos.c);
        }
        LivingEntity target = null;
        double closest_distance = 100;
        List<net.minecraft.server.v1_8_R3.Entity> entities = world.getEntities(entity,
                entity.getBoundingBox().a(entity.motX, entity.motY, entity.motZ).grow(
                        hitbox_mult, hitbox_mult, hitbox_mult));
        for(net.minecraft.server.v1_8_R3.Entity check : entities) {
            if(!(check instanceof net.minecraft.server.v1_8_R3.EntityLiving)) {
                continue;
            }
            LivingEntity living = (LivingEntity) ((EntityLiving) check).getBukkitEntity();
            if (living.equals(firer) || living.equals(projectile)) {
                continue;
            }
            if(DamageUtil.isIntangible(living)) {
                continue;
            }
            // I have a feeling this is buggy somehow?
            /*AxisAlignedBB axisAlignedBB = entity.getBoundingBox().grow(1F, 1F, 1F);
            MovingObjectPosition collisionPosition = axisAlignedBB.a(vec_old, vec_new);
            if(collisionPosition == null) {
                continue;
            }
            double distance = vec_old.distanceSquared(collisionPosition.pos);*/
            double distance = living.getLocation().distance(projectile.getLocation());
            if(distance < closest_distance) {
                closest_distance = distance;
                target = living;
            }
        }
        return target;
    }

    protected Block checkHitBlock() {
        net.minecraft.server.v1_8_R3.World world = ((CraftWorld) projectile.getWorld()).getHandle();
        net.minecraft.server.v1_8_R3.Entity entity = ((CraftEntity) projectile).getHandle();
        // Do a raytrace to see what our real position is going to be
        Vec3D vec_old = new Vec3D(entity.locX, entity.locY, entity.locZ);
        Vec3D vec_new = new Vec3D(entity.locX + entity.motX, entity.locY + entity.motY, entity.locZ + entity.motZ);
        MovingObjectPosition final_position = entity.world.rayTrace(vec_old, vec_new, false, true, false);
        if(final_position == null) {
            return null;
        }
        Block block = projectile.getWorld().getBlockAt(final_position.a().getX(),
                final_position.a().getY(), final_position.a().getZ());
        if(block.isLiquid() || !block.getType().isSolid()) {
            return null;
        }
        // Set our motion to stop on the block we are hitting
        entity.motX = ((float) (final_position.pos.a - entity.locX));
        entity.motY = ((float) (final_position.pos.b - entity.locY));
        entity.motZ = ((float) (final_position.pos.c - entity.locZ));
        // Get the magnitude of the motion vector
        float f2 = MathHelper.sqrt(entity.motX * entity.motX + entity.motY * entity.motY + entity.motZ * entity.motZ);
        entity.locX -= entity.motX / f2 * 0.0500000007450581D;
        entity.locY -= entity.motY / f2 * 0.0500000007450581D;
        entity.locZ -= entity.motZ / f2 * 0.0500000007450581D;
        return block;
    }

    protected boolean checkIdle() {
        if(projectile.isDead() || !projectile.isValid()) {
            return true;
        }
        Block check_block = projectile.getLocation().getBlock().getRelative(BlockFace.DOWN);
        if(projectile.getVelocity().length() < 0.2 && (projectile.isOnGround() || check_block.getType().isSolid())) {
            return true;
        }
        return false;
    }

    public void setProjectileEntity(Entity projectile) {
        if(projectile == null) {
            return;
        }
        this.projectile = projectile;
        if (projectile instanceof Item) {
            Item item = (Item) projectile;
            item.setPickupDelay(1000000);
        }
    }

    protected abstract Entity createProjectileEntity();

    // Called once when the projectile is fired
    protected abstract void doVelocity();

    // Visual effects that apply every tick
    protected abstract void doEffect();

    // Returns true to call destroy after
    protected abstract boolean onExpire();

    // Returns true to call destroy after
    protected abstract boolean onHitLivingEntity(LivingEntity hit);

    // Returns true to call destroy after
    protected abstract boolean onHitBlock(Block hit);

    // Returns true to call destroy after
    protected abstract boolean onIdle();

    public long getExpirationTicks() {
        return 300;
    }

    public String getName() {
        return name;
    }

}