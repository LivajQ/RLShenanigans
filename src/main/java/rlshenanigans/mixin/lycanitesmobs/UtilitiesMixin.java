package rlshenanigans.mixin.lycanitesmobs;

import com.lycanitesmobs.Utilities;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import rlshenanigans.RLShenanigans;

import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;


@Mixin(Utilities.class)
public abstract class UtilitiesMixin {
    
    @Inject(method = "getAssetPath", at = @At("HEAD"), cancellable = true, remap = false)
    private static void forceOurClass(Class<?> ignoredClazz, String assetDomain, String assetPath, CallbackInfoReturnable<Path> cir) {
        
        Class<?> ourClazz = RLShenanigans.class;
        
        Path path = null;
        String assetDir = "/assets/" + assetDomain + (!"".equals(assetPath) ? "/" + assetPath : "");
        
        try {
            URL url = ourClazz.getResource("/assets/" + assetDomain + "/.root");
            URI uri = url.toURI();
            if ("file".equals(uri.getScheme())) {
                path = Paths.get(ourClazz.getResource(assetDir).toURI());
            } else {
                if (!"jar".equals(uri.getScheme())) {
                    System.out.println("Unsupported file scheme: " + uri.getScheme());
                    cir.setReturnValue(null);
                    return;
                }
                
                FileSystem filesystem;
                try {
                    filesystem = FileSystems.getFileSystem(uri);
                } catch (Exception e) {
                    filesystem = FileSystems.newFileSystem(uri, Collections.emptyMap());
                }
                
                path = filesystem.getPath(assetDir);
            }
        } catch (Exception e) {
            System.out.println("No data found in: " + assetDir);
        }
        
        cir.setReturnValue(path);
    }
}