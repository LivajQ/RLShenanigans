package rlshenanigans.client.speech;

import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.capabilities.CapabilitySpeechData;
import org.orecruncher.dsurround.capabilities.speech.ISpeechData;
import org.orecruncher.dsurround.capabilities.speech.SpeechData;

import java.lang.reflect.Field;
import java.util.List;

@SideOnly(Side.CLIENT)
public class SpeechHelper {
    public static void trySpeak(Entity entity, String message, int duration) {
        ISpeechData speech = entity.getCapability(CapabilitySpeechData.SPEECH_DATA, null);
        if (speech instanceof SpeechData) {
            try {
                Field dataField = SpeechData.class.getDeclaredField("data");
                dataField.setAccessible(true);
                List<?> bubbleList = (List<?>) dataField.get(speech);
                bubbleList.clear();
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            speech.addMessage(message, duration);
        }
    }
}