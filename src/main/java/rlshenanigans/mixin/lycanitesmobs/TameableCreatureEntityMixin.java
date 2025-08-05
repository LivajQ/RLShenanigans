package rlshenanigans.mixin.lycanitesmobs;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.client.localisation.LanguageManager;
import com.lycanitesmobs.core.entity.CreatureRelationshipEntry;
import com.lycanitesmobs.core.entity.ExtendedPlayer;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.info.CreatureKnowledge;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.item.consumable.CreatureTreatItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import rlshenanigans.entity.lycaniterideable.EntityKobold;


@Mixin(TameableCreatureEntity.class)
public class TameableCreatureEntityMixin {
    
    @Inject(method = "isTamingItem", at = @At("HEAD"), cancellable = true, remap = false)
    public void injectIsTamingItem(ItemStack itemstack, CallbackInfoReturnable<Boolean> cir) {
        TameableCreatureEntity creature = (TameableCreatureEntity)(Object)this;
        
        if ((creature instanceof EntityKobold)
                && !itemstack.isEmpty()
                && creature.creatureInfo != null
                && creature.creatureInfo.creatureType != null
                && itemstack.getItem() instanceof CreatureTreatItem) {
            
            CreatureTreatItem itemTreat = (CreatureTreatItem)itemstack.getItem();
            if (itemTreat.getCreatureType() == creature.creatureInfo.creatureType) {
                cir.setReturnValue(creature.creatureInfo.isTameable());
            }
        }
    }
    
    @Inject(method = "tame", at = @At("HEAD"), cancellable = true, remap = false)
    public void injectTame(EntityPlayer player, CallbackInfoReturnable<Boolean> cir) {
        TameableCreatureEntity creature = (TameableCreatureEntity)(Object)this;
        
        boolean isBossKobold = (creature instanceof EntityKobold) && creature.isBoss();
        
        if (creature.isTamed()) return;
        if(isBossKobold) {
            //hot kotlin uwu
        }
        else if (creature.isBoss() || !creature.isRareVariant()) return;
        
        ExtendedPlayer extendedPlayer = ExtendedPlayer.getForPlayer(player);
        if (extendedPlayer == null) {
            cir.setReturnValue(creature.isTamed());
            return;
        }
        
        extendedPlayer.studyCreature(creature, CreatureManager.getInstance().config.creatureTreatKnowledge, false, true);
        
        CreatureKnowledge knowledge = extendedPlayer.getBeastiary().getCreatureKnowledge(creature.creatureInfo.getName());
        if (knowledge == null || knowledge.rank < 2) {
            cir.setReturnValue(creature.isTamed());
            return;
        }
        
        CreatureRelationshipEntry relationshipEntry = creature.relationships.getOrCreateEntry(player);
        int reputationAmount = 50 + creature.getRNG().nextInt(50);
        relationshipEntry.increaseReputation(reputationAmount);
        
        if (creature.creatureInfo.isTameable() && relationshipEntry.getReputation() >= creature.creatureInfo.getTamingReputation()) {
            creature.setPlayerOwner(player);
            creature.onTamedByPlayer();
            creature.unsetTemporary();
            
            String tameMessage = LanguageManager.translate("message.pet.tamed")
                    .replace("%creature%", creature.getSpeciesName());
            player.sendMessage(new TextComponentString(tameMessage));
            
            player.addStat(ObjectManager.getStat(creature.creatureInfo.getName() + ".tame"), 1);
            
            if (creature.timeUntilPortal > creature.getPortalCooldown()) {
                creature.timeUntilPortal = creature.getPortalCooldown();
            }
        }
        
        cir.setReturnValue(creature.isTamed());
    }
}