package rlshenanigans.mixin.vanilla;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
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

import java.util.List;

@Mixin(CombatTracker.class)
public abstract class CombatTrackerMixin {
    
    @Shadow private EntityLivingBase fighter;
    
    @Accessor("combatEntries")
    protected abstract List<CombatEntry> getCombatEntries();
    
    @Inject(method = "getDeathMessage", at = @At("HEAD"), cancellable = true)
    private void suppressParasiteDeathMessage(CallbackInfoReturnable<ITextComponent> cir) {
        List<CombatEntry> entries = getCombatEntries();
        
        if (entries.isEmpty()) return;
        
        CombatEntry lastEntry = entries.get(entries.size() - 1);
        Entity killer = lastEntry.getDamageSrc().getTrueSource();
        
        if (killer instanceof EntityParasiteBase) {
            cir.setReturnValue(new TextComponentString(""));
        }
    }
}