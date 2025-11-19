package rlshenanigans.item;

import net.minecraft.item.Item;

public class ItemSpellList {
    public static final ItemSpellBase SPELL_EMPTY = new ItemSpellEmpty("spell_empty", 0, 40, 16);
    public static final ItemSpellBase SPELL_HEAL_LIGHT = new ItemSpellHealLight("spell_heal_light", 10, 40, 8);
    public static final ItemSpellBase SPELL_HEAL_MEDIUM = new ItemSpellHealMedium("spell_heal_medium", 25, 60, 8);
    public static final ItemSpellBase SPELL_HEAL_HEAVY = new ItemSpellHealHeavy("spell_heal_heavy", 35, 80, 8);
    public static final ItemSpellBase SPELL_INVULNERABILITY = new ItemSpellInvulnerability("spell_invulnerability", 75, 100, 3);
    public static final ItemSpellBase SPELL_POWER_WITHIN = new ItemSpellPowerWithin("spell_power_within", 50, 60, 3);
    public static final ItemSpellBase SPELL_FIREBALL = new ItemSpellFireball("spell_fireball", 10, 10, 16);
    public static final ItemSpellBase SPELL_CLOUD_POISON = new ItemSpellCloudPoison("spell_cloud_poison", 35, 40, 8);
    public static final ItemSpellBase SPELL_ARMY_OF_DARKNESS = new ItemSpellArmyOfDarkness("spell_army_of_darkness", 100, 120, 3);
    public static final ItemSpellBase SPELL_ARMAGEDDON = new ItemSpellArmageddon("spell_armageddon", 150, 100, 3);
    public static final ItemSpellBase SPELL_RAY_OF_FROST = new ItemSpellRayOfFrost("spell_ray_of_frost", 35, 35, 8);
    public static final ItemSpellBase SPELL_CHARM = new ItemSpellCharm("spell_charm", 50, 40, 8);
    public static final ItemSpellBase SPELL_FRENZY = new ItemSpellFrenzy("spell_frenzy", 65, 50, 8);
    
    public static Item[] getAllSpells() {
        return new Item[] {
                SPELL_EMPTY,
                SPELL_HEAL_LIGHT,
                SPELL_HEAL_MEDIUM,
                SPELL_HEAL_HEAVY,
                SPELL_INVULNERABILITY,
                SPELL_POWER_WITHIN,
                SPELL_FIREBALL,
                SPELL_CLOUD_POISON,
                SPELL_ARMY_OF_DARKNESS,
                SPELL_ARMAGEDDON,
                SPELL_RAY_OF_FROST,
                SPELL_CHARM,
                SPELL_FRENZY
        };
    }
}