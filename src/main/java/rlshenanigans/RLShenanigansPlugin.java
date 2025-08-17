package rlshenanigans;

import java.util.Map;
import fermiumbooter.FermiumRegistryAPI;
import net.minecraftforge.fml.common.Loader;
import org.spongepowered.asm.launch.MixinBootstrap;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.MCVersion("1.12.2")
public class RLShenanigansPlugin implements IFMLLoadingPlugin {

	public RLShenanigansPlugin() {
		MixinBootstrap.init();
		//False for Vanilla/Coremod mixins, true for regular mod mixins
		FermiumRegistryAPI.enqueueMixin(false, "mixins.rlshenanigans.vanilla.json");
		
		FermiumRegistryAPI.enqueueMixin(true, "mixins.rlshenanigans.eaglemixins.json", () ->
				Loader.isModLoaded("eaglemixins")
		);
		
		FermiumRegistryAPI.enqueueMixin(true, "mixins.rlshenanigans.dynamicsurroundings.json", () ->
				Loader.isModLoaded("dsurround")
		);
		
		FermiumRegistryAPI.enqueueMixin(true, "mixins.rlshenanigans.lycanitesmobs.json", () ->
				Loader.isModLoaded("lycanitesmobs")
		);
		
		FermiumRegistryAPI.enqueueMixin(true, "mixins.rlshenanigans.srparasites.json", () ->
				Loader.isModLoaded("srparasites")
		);
		
		FermiumRegistryAPI.enqueueMixin(true, "mixins.rlshenanigans.jei.json", () ->
				Loader.isModLoaded("jei")
		);
	}

	@Override
	public String[] getASMTransformerClass()
	{
		return new String[0];
	}
	
	@Override
	public String getModContainerClass()
	{
		return null;
	}
	
	@Override
	public String getSetupClass()
	{
		return null;
	}
	
	@Override
	public void injectData(Map<String, Object> data) { }
	
	@Override
	public String getAccessTransformerClass()
	{
		return null;
	}
}