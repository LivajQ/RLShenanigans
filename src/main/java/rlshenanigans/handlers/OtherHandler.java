package rlshenanigans.handlers;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class OtherHandler {
    
    @SubscribeEvent
    public static void lavaChickenDrop(LivingDeathEvent event) {
        if (event.getEntityLiving().world.isRemote) return;
        if (!(event.getSource().getTrueSource() instanceof EntityPlayer)) return;
        EntityLivingBase deadMob = event.getEntityLiving();
        if (!(deadMob instanceof EntityZombie) && !(deadMob instanceof EntityChicken)) return;
        ItemStack lavaChickenDisc = new ItemStack(ModRegistry.musicDiscLavaChicken);
        
        if (deadMob instanceof EntityZombie) {
            if (deadMob.getRidingEntity() instanceof EntityChicken) deadMob.entityDropItem(lavaChickenDisc, 0.5F);
        }
        else {
            if (deadMob.getPassengers().stream().anyMatch(e -> e instanceof EntityZombie)) deadMob.entityDropItem(lavaChickenDisc, 0.5F);
        }
    }
}
