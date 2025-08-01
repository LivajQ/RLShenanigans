package rlshenanigans.mixin.vanilla;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntitySRPProjectile;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.CombatEntry;
import net.minecraft.util.CombatTracker;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import rlshenanigans.handlers.ForgeConfigHandler;
import rlshenanigans.util.ParasiteDeathMessages;

import java.util.List;

@Mixin(CombatTracker.class)
public abstract class CombatTrackerMixin {
    
    @Shadow private EntityLivingBase fighter;
    
    @Accessor("combatEntries")
    protected abstract List<CombatEntry> getCombatEntries();
    
    @Inject(method = "getDeathMessage", at = @At("HEAD"), cancellable = true)
    private void overrideDeathMessageForParasites(CallbackInfoReturnable<ITextComponent> cir) {
        if(!ForgeConfigHandler.client.parasiteDeathMessagesEnabled) return;
        
        List<CombatEntry> entries = getCombatEntries();
        if (entries.isEmpty()) return;
        
        CombatEntry lastEntry = entries.get(entries.size() - 1);
        Entity trueSource = lastEntry.getDamageSrc().getTrueSource();
        
        EntityParasiteBase parasite = null;
        
        if (trueSource instanceof EntityParasiteBase) {
            parasite = (EntityParasiteBase) trueSource;
        } else if (trueSource instanceof EntitySRPProjectile) {
            Entity projectileShooter = ((EntitySRPProjectile) trueSource).shootingEntity;
            if (projectileShooter instanceof EntityParasiteBase) {
                parasite = (EntityParasiteBase) projectileShooter;
            }
        }
        
        if (parasite != null) {
            String custom = ParasiteDeathMessages.getParasiteDeathMessage(fighter, parasite);
            cir.setReturnValue(new TextComponentString(custom));
        }
    }
}