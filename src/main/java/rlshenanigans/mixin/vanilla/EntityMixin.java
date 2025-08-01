package rlshenanigans.mixin.vanilla;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Entity.class)
public interface EntityMixin
{
    @Accessor("width")
    public abstract float getWidth();
    
    @Accessor("height")
    public abstract float getHeight();
    
    @Invoker("setSize")
    public abstract void invokeSetSize(float width, float height);
}

