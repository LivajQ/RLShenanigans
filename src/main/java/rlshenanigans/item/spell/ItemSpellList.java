package rlshenanigans.item.spell;

import static rlshenanigans.handlers.ForgeConfigHandler.spell;

public class ItemSpellList {

    public static final ItemSpellBase SPELL_EMPTY = new ItemSpellEmpty("spell_empty", 0, 40, 16);
    public static final ItemSpellBase SPELL_HEAL_LIGHT = new ItemSpellHealLight("spell_heal_light", spell.spellHealLight);
    public static final ItemSpellBase SPELL_HEAL_MEDIUM = new ItemSpellHealMedium("spell_heal_medium", spell.spellHealMedium);
    public static final ItemSpellBase SPELL_HEAL_HEAVY = new ItemSpellHealHeavy("spell_heal_heavy", spell.spellHealHeavy);
    public static final ItemSpellBase SPELL_INVULNERABILITY = new ItemSpellInvulnerability("spell_invulnerability", spell.spellInvulnerability);
    public static final ItemSpellBase SPELL_POWER_WITHIN = new ItemSpellPowerWithin("spell_power_within", spell.spellPowerWithin);
    public static final ItemSpellBase SPELL_FIREBALL = new ItemSpellFireball("spell_fireball", spell.spellFireball);
    public static final ItemSpellBase SPELL_CLOUD_POISON = new ItemSpellCloudPoison("spell_cloud_poison", spell.spellCloudPoison);
    public static final ItemSpellBase SPELL_ARMY_OF_DARKNESS = new ItemSpellArmyOfDarkness("spell_army_of_darkness", spell.spellArmyOfDarkness);
    public static final ItemSpellBase SPELL_ARMAGEDDON = new ItemSpellArmageddon("spell_armageddon", spell.spellArmageddon);
    public static final ItemSpellBase SPELL_RAY_OF_FROST = new ItemSpellRayOfFrost("spell_ray_of_frost", spell.spellRayOfFrost);
    public static final ItemSpellBase SPELL_ARCANE_SHIELD = new ItemSpellArcaneShield("spell_arcane_shield", spell.spellArcaneShield);
    public static final ItemSpellBase SPELL_FRENZY = new ItemSpellFrenzy("spell_frenzy", spell.spellFrenzy);
    public static final ItemSpellBase SPELL_BLINK = new ItemSpellBlink("spell_blink", spell.spellBlink);
    public static final ItemSpellBase SPELL_EAGLE_EYE = new ItemSpellEagleEye("spell_eagle_eye", spell.spellEagleEye);
    public static final ItemSpellBase SPELL_CHAIN_LIGHTNING = new ItemSpellChainLightning("spell_chain_lightning", spell.spellChainLightning);
    public static final ItemSpellBase SPELL_SHOCK_WARD = new ItemSpellShockWard("spell_shock_ward", spell.spellShockWard);
    public static final ItemSpellBase SPELL_FORCE = new ItemSpellForce("spell_force", spell.spellForce);
    public static final ItemSpellBase SPELL_IMPLOSION = new ItemSpellImplosion("spell_implosion", spell.spellImplosion);
    public static final ItemSpellBase SPELL_RAIN_OF_FIRE = new ItemSpellRainOfFire("spell_rain_of_fire", spell.spellRainOfFire);
    //public static final ItemSpellBase SPELL_MAGIC_WEAPON = new ItemSpellMagicWeapon("spell_magic_weapon", spell.spellMagicWeapon);
    
    public static ItemSpellBase[] getAllSpells() {
        return new ItemSpellBase[] {
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
                SPELL_IMPLOSION,
                SPELL_RAIN_OF_FIRE,
                //SPELL_MAGIC_WEAPON
        };
    }
}