package rlshenanigans.data;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import rlshenanigans.util.TamedParasiteInfo;

import java.util.ArrayList;
import java.util.List;

public class TamedParasiteData extends WorldSavedData
{
    public static final String ID = "TamedParasiteRegistryData";
    private final List<TamedParasiteInfo> registry = new ArrayList<>();
    
    public TamedParasiteData(String name) {
        super(name);
    }
    
    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        registry.clear();
        NBTTagList tagList = nbt.getTagList("Parasites", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < tagList.tagCount(); i++) {
            TamedParasiteInfo info = TamedParasiteInfo.fromNBT(tagList.getCompoundTagAt(i));
            if (info != null) registry.add(info);
        }
    }
    
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagList tagList = new NBTTagList();
        for (TamedParasiteInfo info : registry) {
            tagList.appendTag(info.toNBT());
        }
        compound.setTag("Parasites", tagList);
        return compound;
    }
    
    public static TamedParasiteData get(World world) {
        MapStorage storage = world.getMapStorage();
        TamedParasiteData data = (TamedParasiteData) storage.getOrLoadData(TamedParasiteData.class, ID);
        if (data == null) {
            data = new TamedParasiteData(ID);
            storage.setData(ID, data);
        }
        return data;
    }
    
    public List<TamedParasiteInfo> getAll() {
        return registry;
    }
    
    public void markDirty() {
        super.markDirty();
    }
}