package rlshenanigans.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rlshenanigans.item.ItemSpellBase;

public class SpellParticlePacket implements IMessage {
    private String itemName;
    private int textureIndex;
    private double x, y, z;
    private double motionX, motionY, motionZ;
    private int count;
    private int age;
    
    public SpellParticlePacket() {}
    
    public SpellParticlePacket(ItemSpellBase item, int textureIndex,
                               double x, double y, double z,
                               double motionX, double motionY, double motionZ,
                               int count, int age) {
        this.itemName = Item.REGISTRY.getNameForObject(item).toString();
        this.textureIndex = textureIndex;
        this.x = x;
        this.y = y;
        this.z = z;
        this.motionX = motionX;
        this.motionY = motionY;
        this.motionZ = motionZ;
        this.count = count;
        this.age = age;
    }
    
    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, itemName);
        buf.writeInt(textureIndex);
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
        buf.writeDouble(motionX);
        buf.writeDouble(motionY);
        buf.writeDouble(motionZ);
        buf.writeInt(count);
        buf.writeInt(age);
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        itemName = ByteBufUtils.readUTF8String(buf);
        textureIndex = buf.readInt();
        x = buf.readDouble();
        y = buf.readDouble();
        z = buf.readDouble();
        motionX = buf.readDouble();
        motionY = buf.readDouble();
        motionZ = buf.readDouble();
        count = buf.readInt();
        age = buf.readInt();
    }
    
    public static class Handler implements IMessageHandler<SpellParticlePacket, IMessage> {
        @SideOnly(Side.CLIENT)
        @Override
        public IMessage onMessage(SpellParticlePacket msg, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                World world = Minecraft.getMinecraft().world;
                Item item = Item.REGISTRY.getObject(new ResourceLocation(msg.itemName));
                if (!(item instanceof ItemSpellBase)) return;
                ItemSpellBase spell = (ItemSpellBase) item;
                
                for (int i = 0; i < msg.count; i++) {
                    double dx = msg.x + (world.rand.nextDouble() - 0.5);
                    double dy = msg.y + (world.rand.nextDouble() - 0.5);
                    double dz = msg.z + (world.rand.nextDouble() - 0.5);
                    double mx = msg.motionX;
                    double my = msg.motionY;
                    double mz = msg.motionZ;
                    int age = (int) (msg.age * (0.75 + world.rand.nextDouble() * 0.5));
                    
                    Particle particle = spell.getParticle(world, msg.textureIndex, age, dx, dy, dz, mx, my, mz);
                    if (particle == null) return;
                    Minecraft.getMinecraft().effectRenderer.addEffect(particle);
                }
            });
            return null;
        }
    }
}