package rlshenanigans.item;

import net.minecraft.item.Item;

import static rlshenanigans.handlers.ForgeConfigHandler.SpellConfig;

public class ItemSpellList {

    public static final ItemSpellBase SPELL_EMPTY = new ItemSpellEmpty("spell_empty", 0, 40, 16);
    public static final ItemSpellBase SPELL_HEAL_LIGHT = new ItemSpellHealLight("spell_heal_light", SpellConfig.spellHealLight);
    public static final ItemSpellBase SPELL_HEAL_MEDIUM = new ItemSpellHealMedium("spell_heal_medium", SpellConfig.spellHealMedium);
    public static final ItemSpellBase SPELL_HEAL_HEAVY = new ItemSpellHealHeavy("spell_heal_heavy", SpellConfig.spellHealHeavy);
    public static final ItemSpellBase SPELL_INVULNERABILITY = new ItemSpellInvulnerability("spell_invulnerability", SpellConfig.spellInvulnerability);
    public static final ItemSpellBase SPELL_POWER_WITHIN = new ItemSpellPowerWithin("spell_power_within", SpellConfig.spellPowerWithin);
    public static final ItemSpellBase SPELL_FIREBALL = new ItemSpellFireball("spell_fireball", SpellConfig.spellFireball);
    public static final ItemSpellBase SPELL_CLOUD_POISON = new ItemSpellCloudPoison("spell_cloud_poison", SpellConfig.spellCloudPoison);
    public static final ItemSpellBase SPELL_ARMY_OF_DARKNESS = new ItemSpellArmyOfDarkness("spell_army_of_darkness", SpellConfig.spellArmyOfDarkness);
    public static final ItemSpellBase SPELL_ARMAGEDDON = new ItemSpellArmageddon("spell_armageddon", SpellConfig.spellArmageddon);
    public static final ItemSpellBase SPELL_RAY_OF_FROST = new ItemSpellRayOfFrost("spell_ray_of_frost", SpellConfig.spellRayOfFrost);
    public static final ItemSpellBase SPELL_ARCANE_SHIELD = new ItemSpellArcaneShield("spell_arcane_shield", SpellConfig.spellArcaneShield);
    public static final ItemSpellBase SPELL_FRENZY = new ItemSpellFrenzy("spell_frenzy", SpellConfig.spellFrenzy);
    public static final ItemSpellBase SPELL_BLINK = new ItemSpellBlink("spell_blink", SpellConfig.spellBlink);
    public static final ItemSpellBase SPELL_EAGLE_EYE = new ItemSpellEagleEye("spell_eagle_eye", SpellConfig.spellEagleEye);
    public static final ItemSpellBase SPELL_CHAIN_LIGHTNING = new ItemSpellChainLightning("spell_chain_lightning", SpellConfig.spellChainLightning);
    public static final ItemSpellBase SPELL_SHOCK_WARD = new ItemSpellShockWard("spell_shock_ward", SpellConfig.spellShockWard);
    public static final ItemSpellBase SPELL_FORCE = new ItemSpellForce("spell_force", SpellConfig.spellForce);
    public static final ItemSpellBase SPELL_IMPLOSION = new ItemSpellImplosion("spell_implosion", SpellConfig.spellImplosion);
    
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
                SPELL_ARCANE_SHIELD,
                SPELL_FRENZY,
                SPELL_BLINK,
                SPELL_EAGLE_EYE,
                SPELL_CHAIN_LIGHTNING,
                SPELL_SHOCK_WARD,
                SPELL_FORCE,
                SPELL_IMPLOSION
        };
    }
}