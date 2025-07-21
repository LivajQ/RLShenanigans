package rlshenanigans.item;

import com.oblivioussp.spartanweaponry.api.ToolMaterialEx;
import com.oblivioussp.spartanweaponry.api.WeaponProperties;
import com.oblivioussp.spartanweaponry.api.weaponproperty.WeaponProperty;
import com.oblivioussp.spartanweaponry.item.ItemWeaponBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import rlshenanigans.RLShenanigans;
import rlshenanigans.handlers.RLSPacketHandler;
import rlshenanigans.packet.ParticlePulsePacket;
import rlshenanigans.potion.PotionStagger;

import java.util.List;


@Mod.EventBusSubscriber(modid = RLShenanigans.MODID)
public class ItemWeaponZweihander extends ItemWeaponBase
{
    private static final int STAGGER_LENGTH = 60;
    private static final int STAGGER_COOLDOWN = 120;
    
    public ItemWeaponZweihander(String unlocName) {
        super(unlocName, ToolMaterialEx.DIAMOND, 10.0F, 1.0F, 0.6D,
                new WeaponProperty[] {
                        new WeaponProperty("reach", RLShenanigans.MODID, 3, 8.0F),
                        WeaponProperties.SWEEP_DAMAGE_FULL
                }
        );
        
        this.setMaxDamage(2137);
        this.setTranslationKey("weapon_zweihander");
        this.setCreativeTab(CreativeTabs.COMBAT);
        this.setMaxStackSize(1);
    }
    
    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker)
    {
        long currentTime = attacker.world.getTotalWorldTime();
        
        AxisAlignedBB sweepBox = target.getEntityBoundingBox().grow(1.5D, 0.25D, 1.5D);
        List<EntityLivingBase> swept = attacker.world.getEntitiesWithinAABB(
                EntityLivingBase.class,
                sweepBox,
                e -> e != attacker && e.isEntityAlive()
        );
        
        for (EntityLivingBase sweptTarget : swept)
        {
            NBTTagCompound tag = sweptTarget.getEntityData();
            long lastEffect = tag.getLong("StaggerEffect");
            
            if (currentTime - lastEffect >= STAGGER_COOLDOWN)
            {
                sweptTarget.addPotionEffect(new PotionEffect(PotionStagger.INSTANCE, STAGGER_LENGTH, 1));
                tag.setLong("StaggerEffect", currentTime);
                
                RLSPacketHandler.INSTANCE.sendToAll(
                        new ParticlePulsePacket(sweptTarget, EnumParticleTypes.VILLAGER_ANGRY, STAGGER_LENGTH, 15)
                );
            }
        }
        
        return super.hitEntity(stack, target, attacker);
    }
    
    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        EntityLivingBase target = event.getEntityLiving();
        DamageSource source = event.getSource();
        
        if (source.getImmediateSource() instanceof EntityLivingBase) {
            EntityLivingBase attacker = (EntityLivingBase) source.getImmediateSource();
            ItemStack weapon = attacker.getHeldItemMainhand();
            
            if (weapon.getItem() instanceof ItemWeaponZweihander && !(attacker instanceof EntityPlayer)) {
                ((ItemWeaponZweihander) weapon.getItem()).hitEntity(weapon, target, attacker);
            }
        }
    }
}