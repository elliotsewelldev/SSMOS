package SSM.Kits;

import SSM.Abilities.BoneExplosion;
import SSM.Abilities.Firefly;
import SSM.Abilities.RopedArrow;
import SSM.Attributes.BowCharge.Barrage;
import SSM.Attributes.DoubleJumps.GenericDoubleJump;
import SSM.Attributes.ItemGenerator;
import SSM.Attributes.Regeneration;
import SSM.Attributes.SquidFix;
import SSM.Kit;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

//import SSM.Attributes.ClearProjectile;

public class KitBlaze extends Kit {

    public KitBlaze() {
        super();

        this.damage = 6;
        this.armor = 6;
        this.speed = 0.22f;
        this.regeneration = 0.25;
        this.knockbackTaken = 1.50;
        this.disguise = DisguiseType.BLAZE;
        this.name = "Blaze";
        this.menuItem = new ItemStack(Material.BLAZE_ROD);
    }

    public void equipKit(Player player) {
        super.equipKit(player);

        setArmor(Material.CHAINMAIL_BOOTS, 0);
        setArmor(Material.CHAINMAIL_LEGGINGS, 1);
        setArmor(Material.CHAINMAIL_CHESTPLATE, 2);
        setArmor(Material.CHAINMAIL_HELMET, 3);

        setItem(Material.IRON_SWORD, 0);
        setItem(Material.IRON_AXE, 1, new Firefly());

        addAttribute(new Regeneration(regeneration, 1));
        addAttribute(new GenericDoubleJump(0.61, 0.8, 1));
    }

}
