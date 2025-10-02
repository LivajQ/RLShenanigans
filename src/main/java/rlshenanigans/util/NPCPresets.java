package rlshenanigans.util;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import rlshenanigans.RLShenanigans;
import rlshenanigans.entity.npc.EntityNPCBase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static rlshenanigans.RLShenanigans.RLSRAND;

public class NPCPresets {
    
    public enum Categories {
        RANDOM, GENERIC, SUMMON, INVADER
    }
    
    public static class NPCPreset {
        public final String name;
        public final ResourceLocation skin;
        public final WeaponRegistry.WeaponTypes preferredWeapon;
        public final ResourceLocation offhandItem;
        public final int offhandItemCount;
        
        public NPCPreset(String name, String skinPath, WeaponRegistry.WeaponTypes preferredWeapon, ResourceLocation offhandItem) {
            this(name, skinPath, preferredWeapon, offhandItem, 1);
        }
        
        public NPCPreset(String name, String skinName, WeaponRegistry.WeaponTypes preferredWeapon, ResourceLocation offhandItem, int offhandItemCount) {
            this.name = name;
            this.skin = new ResourceLocation(RLShenanigans.MODID, "textures/entity/npc/" + skinName + ".png");
            this.preferredWeapon = preferredWeapon;
            this.offhandItem = offhandItem;
            this.offhandItemCount = offhandItemCount;
        }
    }
    
    public static final String[] RANDOM_NAMES = { "sth", "whatev" };
    public static final String[] RANDOM_SKINS;
    static {
        int skinCount = 10;
        RANDOM_SKINS = new String[skinCount];
        for (int i = 0; i < skinCount; i++) {
            RANDOM_SKINS[i] = "npc_random_" + (i + 1);
        }
    }
    
    public static final List<NPCPreset> GENERIC_PRESETS = new ArrayList<>(Arrays.asList(
            new NPCPreset("John Pork", "npc_generic_johnpork", null, new ResourceLocation("minecraft", "cooked_porkchop"), 10),
            new NPCPreset("All-Nighter", "npc_generic_allnighter", WeaponRegistry.WeaponTypes.SABER, new ResourceLocation("spartanshields", "shield_basic_diamond")),
            new NPCPreset("Eyevan", "npc_generic_eyevan", WeaponRegistry.WeaponTypes.WARHAMMER, null),
            new NPCPreset("Tourist", "npc_generic_tourist", WeaponRegistry.WeaponTypes.KATANA, new ResourceLocation("bountifulbaubles", "phantomprism")),
            new NPCPreset("Creepa", "npc_generic_creepa", WeaponRegistry.WeaponTypes.QUARTERSTAFF, new ResourceLocation("minecraft", "tnt")),
            new NPCPreset("Assassin", "npc_generic_assassin", WeaponRegistry.WeaponTypes.DAGGER, new ResourceLocation("defiledlands", "scarlite_reaver")),
            new NPCPreset("Lancelot", "npc_generic_lancelot", WeaponRegistry.WeaponTypes.LANCE, null),
            new NPCPreset("Aegis", "npc_generic_aegis", WeaponRegistry.WeaponTypes.SPEAR, new ResourceLocation("spartanshields", "shield_basic_obsidian")),
            new NPCPreset("Waltuh", "npc_generic_waltuh", null, new ResourceLocation("minecraft", "prismarine_crystals")),
            new NPCPreset("Professor", "npc_generic_professor", WeaponRegistry.WeaponTypes.SCYTHE, null),
            new NPCPreset("Spookman", "npc_generic_spookman", null, null),
            new NPCPreset("Roderick", "npc_generic_roderick", null, new ResourceLocation("forgottenitems", "heath_talisman")),
            new NPCPreset("Jay", "npc_generic_jay", null, new ResourceLocation("defiledlands", "umbra_blaster"))
    ));
    
    public static final List<NPCPreset> SUMMON_PRESETS = new ArrayList<>(Arrays.asList(
            new NPCPreset("Prince Horace", "npc_summon_princehorace", WeaponRegistry.WeaponTypes.SWORD, new ResourceLocation("spartanshields", "shield_basic_gold")),
            new NPCPreset("Jorund", "npc_summon_jorund", null, null)
    ));
    
    public static final List<NPCPreset> INVADER_PRESETS = new ArrayList<>(Arrays.asList(
            new NPCPreset("Father Esteban", "npc_invader_fatheresteban", WeaponRegistry.WeaponTypes.MACE, new ResourceLocation("minecraft", "totem_of_undying"), 3),
            new NPCPreset("Big Sword Bill", "npc_invader_bigswordbill", WeaponRegistry.WeaponTypes.GREATSWORD, null),
            new NPCPreset("Blade Master", "npc_invader_blademaster", WeaponRegistry.WeaponTypes.KATANA, null),
            new NPCPreset("Watchful Jim", "npc_invader_watchfuljim", WeaponRegistry.WeaponTypes.PIKE, null),
            new NPCPreset("Crazy Axe Man", "npc_invader_crazyaxeman", WeaponRegistry.WeaponTypes.BATTLEAXE, new ResourceLocation("minecraft", "shield")),
            new NPCPreset("Mr Can't Parry", "npc_invader_mrcantparry", WeaponRegistry.WeaponTypes.DAGGER, new ResourceLocation("minecraft", "shield"))
    ));
    
    public static final List<ResourceLocation> OFFHAND_RANDOM = Arrays.asList(
            new ResourceLocation("minecraft", "shield"),
            new ResourceLocation("minecraft", "totem_of_undying"),
            new ResourceLocation("simpledifficulty", "canteen")
    );
    
    public static void generatePreset(EntityNPCBase npc, Categories category) {
        switch (category) {
            case RANDOM:
                npc.name = RANDOM_NAMES[RLSRAND.nextInt(RANDOM_NAMES.length)];
                npc.skin = new ResourceLocation(RLShenanigans.MODID, "textures/entity/npc/" +
                        RANDOM_SKINS[RLSRAND.nextInt(RANDOM_SKINS.length)] + ".png");
                npc.preferredWeapon = null;
                if (RLSRAND.nextFloat() < npc.getRandomOffhandChance()) {
                    npc.offhandItem = rollOffhandItem();
                    npc.offhandItemCount = 1;
                }
                else npc.offhandItem = null;
                break;
            
            case GENERIC:
                applyPreset(npc, GENERIC_PRESETS);
                break;
            
            case SUMMON:
                applyPreset(npc, SUMMON_PRESETS);
                break;
            
            case INVADER:
                applyPreset(npc, INVADER_PRESETS);
                break;
        }
    }
    
    private static void applyPreset(EntityNPCBase npc, List<NPCPreset> pool) {
        if (pool.isEmpty()) return;
        NPCPreset chosen = pool.get(RLSRAND.nextInt(pool.size()));
        npc.name = chosen.name;
        npc.skin = chosen.skin;
        npc.preferredWeapon = chosen.preferredWeapon;
        if (chosen.offhandItem != null) npc.offhandItem = chosen.offhandItem;
        else if (RLSRAND.nextFloat() < npc.getRandomOffhandChance()) npc.offhandItem = rollOffhandItem();
        npc.offhandItemCount = MathHelper.clamp(chosen.offhandItemCount, 1, 64);
    }
    
    private static ResourceLocation rollOffhandItem() {
        return OFFHAND_RANDOM.get(RLSRAND.nextInt(OFFHAND_RANDOM.size()));
    }
}