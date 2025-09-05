package rlshenanigans.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemRecord;
import net.minecraft.util.SoundEvent;
import rlshenanigans.RLShenanigans;

public class ItemMusicDisc extends ItemRecord {
    public ItemMusicDisc(String name, SoundEvent sound) {
        super(RLShenanigans.MODID + ":" + name, sound);
        this.setRegistryName(RLShenanigans.MODID, name);
        this.setTranslationKey(name);
        this.setCreativeTab(CreativeTabs.MISC);
    }
}