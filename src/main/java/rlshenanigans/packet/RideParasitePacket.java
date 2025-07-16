package rlshenanigans.packet;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class RideParasitePacket implements IMessage {
    public float forward;
    public float strafe;
    public boolean jump;
    public boolean sprinting;
    public boolean ascend;
    public boolean descend;
    public boolean projectile;
    
    public RideParasitePacket() {
    }
    
    public RideParasitePacket(float forward, float strafe, boolean jump, boolean sprinting, boolean ascend, boolean descend, boolean projectile) {
        this.forward = forward;
        this.strafe = strafe;
        this.jump = jump;
        this.sprinting = sprinting;
        this.ascend = ascend;
        this.descend = descend;
        this.projectile = projectile;
    }
    
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeFloat(forward);
        buf.writeFloat(strafe);
        buf.writeBoolean(jump);
        buf.writeBoolean(sprinting);
        buf.writeBoolean(ascend);
        buf.writeBoolean(descend);
        buf.writeBoolean(projectile);
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        forward = buf.readFloat();
        strafe = buf.readFloat();
        jump = buf.readBoolean();
        sprinting = buf.readBoolean();
        ascend = buf.readBoolean();
        descend = buf.readBoolean();
        projectile = buf.readBoolean();
    }
}