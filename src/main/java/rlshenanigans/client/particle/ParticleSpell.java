package rlshenanigans.client.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rlshenanigans.item.ItemSpellBase;

@SideOnly(Side.CLIENT)
public class ParticleSpell extends Particle {
    
    public ParticleSpell(ItemSpellBase spell, World world, int textureIndex, double x, double y, double z, double motionX, double motionY, double motionZ) {
        super(world, x, y, z, motionX, motionY, motionZ);
        
        Vec3d color = spell.getParticleColor();
        this.setRBGColorF((float) color.x, (float) color.y, (float) color.z);
        this.particleMaxAge = 20;
        this.particleAlpha = 1.0F;
        if (motionX == 0.0D) this.motionX = 0;
        if (motionY == 0.0D) this.motionY = 0;
        if (motionZ == 0.0D) this.motionZ = 0;
        this.setParticleTextureIndex(textureIndex);
    }
    
    //I didn't even bother checking if all these are correct. magic number final boss
    //Note to self: use Minecraft.getMinecraft().getTextureManager().bindTexture() and override fxlayer to 2 for custom particles
    public static int getTextureIndexFromEnum(EnumParticleTypes type) {
        switch (type) {
            case EXPLOSION_NORMAL: return 0;
            case EXPLOSION_LARGE: return 1;
            case EXPLOSION_HUGE: return 2;
            case FIREWORKS_SPARK: return 3;
            case WATER_BUBBLE: return 4;
            case WATER_SPLASH: return 5;
            case WATER_WAKE: return 6;
            case SUSPENDED: return 7;
            case SUSPENDED_DEPTH: return 8;
            case CRIT: return 9;
            case CRIT_MAGIC: return 10;
            case SMOKE_NORMAL: return 50;
            case SMOKE_LARGE: return 51;
            case SPELL: return 128;
            case SPELL_INSTANT: return 129;
            case SPELL_MOB: return 144;
            case SPELL_MOB_AMBIENT: return 145;
            case SPELL_WITCH: return 146;
            case DRIP_WATER: return 18;
            case DRIP_LAVA: return 19;
            case VILLAGER_ANGRY: return 81;
            case VILLAGER_HAPPY: return 82;
            case TOWN_AURA: return 22;
            case NOTE: return 67;
            case PORTAL: return 73;
            case ENCHANTMENT_TABLE: return 78;
            case FLAME: return 48;
            case LAVA: return 49;
            case FOOTSTEP: return 28;
            case CLOUD: return 68;
            case REDSTONE: return 65;
            case SNOWBALL: return 31;
            case SNOW_SHOVEL: return 32;
            case SLIME: return 33;
            case HEART: return 64;
            case BARRIER: return 35;
            case ITEM_CRACK: return 36;
            case BLOCK_CRACK: return 37;
            case BLOCK_DUST: return 38;
            case WATER_DROP: return 39;
            case ITEM_TAKE: return 40;
            case MOB_APPEARANCE: return 41;
            case DRAGON_BREATH: return 42;
            case END_ROD: return 43;
            case DAMAGE_INDICATOR: return 44;
            case SWEEP_ATTACK: return 45;
            case FALLING_DUST: return 46;
            case TOTEM: return 47;
            case SPIT: return 48;
            default: return 0;
        }
    }
}