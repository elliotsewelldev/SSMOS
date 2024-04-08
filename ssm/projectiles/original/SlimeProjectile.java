package ssm.projectiles.original;

import net.minecraft.server.v1_8_R3.EntitySlime;
import net.minecraft.server.v1_8_R3.PathfinderGoal;
import net.minecraft.server.v1_8_R3.PathfinderGoalAvoidTarget;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftSlime;
import ssm.events.SmashDamageEvent;
import ssm.projectiles.SmashProjectile;
import ssm.utilities.DamageUtil;
import ssm.utilities.Utils;
import ssm.utilities.VelocityUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.SlimeSplitEvent;

public class SlimeProjectile extends SmashProjectile {

    private double charge = 0;
    private int clear_slime_task = -1;
    private long last_slime_hit_time = 0;

    public SlimeProjectile(Player firer, String name, double charge) {
        super(firer, name);
        this.damage = 3;
        this.hitbox_size = 0.75;
        this.knockback_mult = 3;
        this.charge = charge;
    }

    @Override
    public void launchProjectile() {
        super.launchProjectile();
        clear_slime_task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                if(projectile == null || !(projectile instanceof Slime)) {
                    Bukkit.getScheduler().cancelTask(clear_slime_task);
                    return;
                }
                Slime slime = (Slime) projectile;
                if (slime.getTicksLived() > 120) {
                    slime.setTicksLived(1);

                    int entity_amount = 6 + 6 * slime.getSize();
                    Utils.itemEffect(slime.getLocation(), entity_amount, 0.2 + 0.1 * slime.getSize(),
                            null, 1f, 1f, Material.SLIME_BALL, (byte) 0, 15);

                    if (slime.getSize() <= 1) {
                        slime.remove();
                    }
                    else {
                        slime.setSize(slime.getSize() - 1);
                    }
                }
            }
        }, 0, 20L);
    }

    @Override
    protected Entity createProjectileEntity() {
        return null;
    }

    @Override
    protected void doVelocity() {
        VelocityUtil.setVelocity(projectile, firer.getLocation().getDirection(),
                1 + charge / 2d, false, 0, 0.2, 10, true);
    }

    @Override
    protected void doEffect() {
        return;
    }

    @Override
    protected boolean onExpire() {
        return false;
    }

    @Override
    protected boolean onHitLivingEntity(LivingEntity hit) {
        Slime slime = (Slime) projectile;
        double final_damage = damage + slime.getSize() * 3;
        SmashDamageEvent smashDamageEvent = new SmashDamageEvent(hit, firer, final_damage);
        smashDamageEvent.multiplyKnockback(knockback_mult);
        smashDamageEvent.setIgnoreDamageDelay(true);
        smashDamageEvent.setReason(name);
        Bukkit.getPluginManager().callEvent(smashDamageEvent);
        playHitSound();
        last_slime_hit_time = System.currentTimeMillis();
        // Cancel hit detection
        this.cancel();
        return false;
    }

    @Override
    protected boolean onHitBlock(Block hit) {
        // Cancel hit detection
        this.cancel();
        return false;
    }

    @Override
    protected boolean onIdle() {
        return false;
    }

    @EventHandler
    public void onSlimeDeathSplit(SlimeSplitEvent e) {
        if(e.getEntity().equals(projectile)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onSlimeTarget(EntityTargetEvent e) {
        if(firer == null || projectile == null) {
            return;
        }
        if(!projectile.equals(e.getEntity())) {
            return;
        }
        if(e.getTarget() == null) {
            return;
        }
        if(e.getTarget() instanceof Player && !DamageUtil.canDamage((Player) e.getTarget(), firer) || e.getTarget().equals(firer)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onSmashDamage(SmashDamageEvent e) {
        if(projectile == null || e.getDamager() == null) {
            return;
        }
        if(!e.getDamager().equals(projectile)) {
            return;
        }
        if(System.currentTimeMillis() - last_slime_hit_time < 500) {
            e.setCancelled(true);
            return;
        }
        last_slime_hit_time = System.currentTimeMillis();
        if(e.getDamagee().equals(firer)) {
            e.setCancelled(true);
            return;
        }
        if(!DamageUtil.canDamage(e.getDamagee(), firer)) {
            e.setCancelled(true);
            return;
        }
        Slime slime = (Slime) e.getDamager();
        e.setDamage(2 * slime.getSize());
        e.multiplyKnockback(2);
        firer.setLevel((int) e.getDamage());
        //firer.sendMessage("Slime Damage: " + e.getDamage());
    }

}
