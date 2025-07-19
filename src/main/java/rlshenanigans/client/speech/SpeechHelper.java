package rlshenanigans.client.speech;

import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.capabilities.CapabilitySpeechData;
import org.orecruncher.dsurround.capabilities.speech.ISpeechData;

@SideOnly(Side.CLIENT)
public class SpeechHelper {
    public static void trySpeak(Entity entity, String message, int duration) {
        ISpeechData speech = entity.getCapability(CapabilitySpeechData.SPEECH_DATA, null);
        if (speech != null) {
            speech.addMessage(message, duration);
        }
    }
}