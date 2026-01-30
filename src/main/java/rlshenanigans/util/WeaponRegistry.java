package rlshenanigans.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import rlshenanigans.RLShenanigans;

import java.util.*;

public class WeaponRegistry {
    public final ResourceLocation id;
    public final WeaponTypes type;
    public final WeaponQualities quality;
    private final double reachBonusOverride;

    public enum WeaponQualities {
        SPECIAL(325), DRAGONBONEINF(300), DRAGONBONE(270), MYRMEX(240), DIAMOND(200),
        STEEL(180), SILVER(150), UMBRIUM(120), IRON(100), BRONZE(80),
        GOLD(60), STONE(30), WOOD(0);
        
        public final int powerLevel;
        
        WeaponQualities(int powerLevel) {
            this.powerLevel = powerLevel;
        }
    }
    
    public enum WeaponTypes {
        SABER(0.0), RAPIER(0.2), GREATSWORD(0.5), SWORD(0.0), HAMMER(0.0),
        SPEAR(0.5), PIKE(1.0), DAGGER(-0.2), BATTLEAXE(0.2), MACE(0.0),
        KATANA(0.3), WARHAMMER(0.5), HALBERD(0.5), LANCE(1.0), GLAIVE(0.5),
        QUARTERSTAFF(0.5), SCYTHE(0.5);
        
        private final double reachBonus;
        
        WeaponTypes(double reachBonus) {
            this.reachBonus = reachBonus;
        }
    }
    
    public static final Map<ResourceLocation, WeaponRegistry> WEAPONS = new HashMap<>();
    static {
        register(new WeaponRegistry(
                new ResourceLocation("rlshenanigans", "weapon_zweihander"),
                WeaponTypes.GREATSWORD, WeaponQualities.SPECIAL, 1.5));
        
        register(new WeaponRegistry(
                new ResourceLocation("mod_lavacow", "reapers_scythe"),
                WeaponTypes.SCYTHE, WeaponQualities.SPECIAL, 1.0));
        
        register(new WeaponRegistry(
                new ResourceLocation("srparasites", "weapon_scythe"),
                WeaponTypes.SCYTHE, WeaponQualities.SPECIAL, 1.5));
        
        register(new WeaponRegistry(
                new ResourceLocation("srparasites", "weapon_scythe_sentient"),
                WeaponTypes.SCYTHE, WeaponQualities.SPECIAL, 1.5));
        
        register(new WeaponRegistry(
                new ResourceLocation("srparasites", "weapon_axe"),
                WeaponTypes.BATTLEAXE, WeaponQualities.SPECIAL, 1.5));
        
        register(new WeaponRegistry(
                new ResourceLocation("srparasites", "weapon_axe_sentient"),
                WeaponTypes.BATTLEAXE, WeaponQualities.SPECIAL, 1.5));
        
        register(new WeaponRegistry(
                new ResourceLocation("srparasites", "weapon_sword"),
                WeaponTypes.SWORD, WeaponQualities.SPECIAL, 1.5));
        
        register(new WeaponRegistry(
                new ResourceLocation("srparasites", "weapon_sword_sentient"),
                WeaponTypes.SWORD, WeaponQualities.SPECIAL, 1.5));
        
        register(new WeaponRegistry(
                new ResourceLocation("srparasites", "weapon_cleaver"),
                WeaponTypes.KATANA, WeaponQualities.SPECIAL, 1.5));
        
        register(new WeaponRegistry(
                new ResourceLocation("srparasites", "weapon_cleaver_sentient"),
                WeaponTypes.KATANA, WeaponQualities.SPECIAL, 1.5));
        
        register(new WeaponRegistry(
                new ResourceLocation("iceandfire", "troll_weapon.axe"),
                WeaponTypes.BATTLEAXE, WeaponQualities.STEEL, 0.5));
        
        register(new WeaponRegistry(
                new ResourceLocation("iceandfire", "troll_weapon.hammer"),
                WeaponTypes.WARHAMMER, WeaponQualities.STEEL));
    }
    
    private static void register(WeaponRegistry reg) {
        WEAPONS.put(reg.id, reg);
    }
    
    public static final Map<String, WeaponTypes> TYPE_KEYWORDS = new LinkedHashMap<>();
    static {
        TYPE_KEYWORDS.put("saber", WeaponTypes.SABER);
        TYPE_KEYWORDS.put("rapier", WeaponTypes.RAPIER);
        TYPE_KEYWORDS.put("greatsword", WeaponTypes.GREATSWORD);
        TYPE_KEYWORDS.put("sword", WeaponTypes.SWORD);
        TYPE_KEYWORDS.put("warhammer", WeaponTypes.WARHAMMER);
        TYPE_KEYWORDS.put("hammer", WeaponTypes.HAMMER);
        TYPE_KEYWORDS.put("spear", WeaponTypes.SPEAR);
        TYPE_KEYWORDS.put("pike", WeaponTypes.PIKE);
        TYPE_KEYWORDS.put("dagger", WeaponTypes.DAGGER);
        TYPE_KEYWORDS.put("battleaxe", WeaponTypes.BATTLEAXE);
        TYPE_KEYWORDS.put("mace", WeaponTypes.MACE);
        TYPE_KEYWORDS.put("katana", WeaponTypes.KATANA);
        TYPE_KEYWORDS.put("halberd", WeaponTypes.HALBERD);
        TYPE_KEYWORDS.put("lance", WeaponTypes.LANCE);
        TYPE_KEYWORDS.put("glaive", WeaponTypes.GLAIVE);
        TYPE_KEYWORDS.put("quarterstaff", WeaponTypes.QUARTERSTAFF);
        TYPE_KEYWORDS.put("scythe", WeaponTypes.SCYTHE);
    }
    
    public static final Map<String, WeaponQualities> QUALITY_KEYWORDS = new LinkedHashMap<>();
    static {
        QUALITY_KEYWORDS.put("dragonbone", WeaponQualities.DRAGONBONE);
        QUALITY_KEYWORDS.put("stinger", WeaponQualities.MYRMEX);
        QUALITY_KEYWORDS.put("venom", WeaponQualities.MYRMEX);
        QUALITY_KEYWORDS.put("diamond", WeaponQualities.DIAMOND);
        QUALITY_KEYWORDS.put("steel", WeaponQualities.STEEL);
        QUALITY_KEYWORDS.put("silver", WeaponQualities.SILVER);
        QUALITY_KEYWORDS.put("umbrium", WeaponQualities.UMBRIUM);
        QUALITY_KEYWORDS.put("iron", WeaponQualities.IRON);
        QUALITY_KEYWORDS.put("bronze", WeaponQualities.BRONZE);
        QUALITY_KEYWORDS.put("gold", WeaponQualities.GOLD);
        QUALITY_KEYWORDS.put("stone", WeaponQualities.STONE);
        QUALITY_KEYWORDS.put("wood", WeaponQualities.WOOD);
    }

    public WeaponRegistry(ResourceLocation id, WeaponTypes type, WeaponQualities quality) {
        this(id, type, quality, Double.NaN);
    }
    
    public WeaponRegistry(ResourceLocation id, WeaponTypes type, WeaponQualities quality, double reachBonusOverride) {
        this.id = id;
        this.type = type;
        this.quality = quality;
        this.reachBonusOverride = reachBonusOverride;
    }
    
    public static WeaponRegistry getWeaponByResourceLocation(ResourceLocation id) {
        return WEAPONS.get(id);
    }
    
    public double getReachBonus() {
        return Double.isNaN(reachBonusOverride) ? type.reachBonus : reachBonusOverride;
    }
    
    public static Item chooseRandomWeapon() {
        if (WEAPONS.isEmpty()) return null;
        
        List<WeaponRegistry> list = new ArrayList<>(WEAPONS.values());
        WeaponRegistry chosen = list.get(RLShenanigans.RLSRAND.nextInt(list.size()));
        
        return ForgeRegistries.ITEMS.getValue(chosen.id);
    }
    
    public static void scanWeapons() {
        for (Item item : ForgeRegistries.ITEMS.getValuesCollection()) {
            if (!(item instanceof ItemSword)) continue;
            
            ResourceLocation id = ForgeRegistries.ITEMS.getKey(item);
            if (id == null || WEAPONS.containsKey(id)) continue;
            
            String path = id.getPath().toLowerCase();
            WeaponTypes type = detectType(path);
            WeaponQualities quality = detectQuality(path);
            
            if (type != null && quality != null)
                register(new WeaponRegistry(id, type, quality));
        }
    }
    
    private static WeaponTypes detectType(String path) {
        for (Map.Entry<String, WeaponTypes> entry : TYPE_KEYWORDS.entrySet()) {
            if (path.contains(entry.getKey()))
                return entry.getValue();
        }
        return null;
    }
    
    private static WeaponQualities detectQuality(String path) {
        if (path.contains("dragonbone") &&
                (path.contains("ice") || path.contains("fire") || path.contains("lightning"))) {
            return WeaponQualities.DRAGONBONEINF;
        }
        
        for (Map.Entry<String, WeaponQualities> entry : QUALITY_KEYWORDS.entrySet()) {
            if (path.contains(entry.getKey()))
                return entry.getValue();
        }
        return null;
    }
}
