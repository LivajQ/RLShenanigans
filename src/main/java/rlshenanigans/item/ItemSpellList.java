package rlshenanigans.item;

import net.minecraft.item.Item;

public class ItemSpellList {
    public static final ItemSpellBase SPELL_EMPTY = new ItemSpellEmpty("spell_empty", 0, 40, 8);
    public static final ItemSpellBase SPELL_HEAL_LIGHT = new ItemSpellHealLight("spell_heal_light", 10, 40, 8);
    
    public static Item[] getAllSpells() {
        return new Item[] {
                SPELL_EMPTY,
                SPELL_HEAL_LIGHT,
        };
    }
}