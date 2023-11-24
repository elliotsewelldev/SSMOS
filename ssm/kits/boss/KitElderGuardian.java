package ssm.kits.boss;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import ssm.abilities.boss.WhirlpoolShotgun;
import ssm.abilities.original.TargetLaser;
import ssm.abilities.original.WaterSplash;
import ssm.abilities.original.WhirlpoolAxe;
import ssm.attributes.*;
import ssm.attributes.doublejumps.GenericDoubleJump;
import ssm.kits.Kit;
import ssm.managers.DisguiseManager;
import ssm.managers.disguises.ElderGuardianDisguise;
import ssm.managers.disguises.GuardianDisguise;

public class KitElderGuardian extends Kit {

    public KitElderGuardian() {
        super();
        this.damage = 10;
        this.armor = 12;
        this.regeneration = 0;
        this.knockback = 0.75;
        this.name = "Elder Guardian";
        this.menuItem = Material.PRISMARINE_SHARD;
        this.podium_mob_type = EntityType.GUARDIAN;
    }

    @Override
    public void initializeKit() {
        setArmorSlot(Material.DIAMOND_BOOTS, 0);
        setArmorSlot(Material.DIAMOND_LEGGINGS, 1);
        setArmorSlot(Material.DIAMOND_CHESTPLATE, 2);
        setArmorSlot(Material.DIAMOND_HELMET, 3);

        setAbility(new WhirlpoolShotgun(), 0);
        setAbility(new WaterSplash(), 1);
        setAbility(new TargetLaser(), 2);

        addAttribute(new Regeneration(regeneration));
        addAttribute(new Hunger());
        addAttribute(new Compass());
        addAttribute(new GenericDoubleJump(0.9, 0.9, 1, Sound.GHAST_FIREBALL));
        addAttribute(new NoVoidDeath(2.0, true));
        addAttribute(new NoTrueDamage(3.0));

        DisguiseManager.addDisguise(owner, new ElderGuardianDisguise(owner));
    }

    @Override
    public void setPreviewHotbar() {
        setItem(new ItemStack(Material.IRON_AXE), 0);
        setItem(new ItemStack(Material.IRON_SWORD), 1);
        setItem(new ItemStack(Material.IRON_PICKAXE), 2);
        setItem(new ItemStack(Material.NETHER_STAR), 4);
    }

    @Override
    public void setGameHotbar() {
        setItem(new ItemStack(Material.IRON_AXE), 0);
        setItem(new ItemStack(Material.IRON_SWORD), 1);
        setItem(new ItemStack(Material.IRON_PICKAXE), 2);
        setItem(Compass.COMPASS_ITEM, 3);
    }

}
