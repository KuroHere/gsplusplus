/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.misc;

import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ChatType;
import net.minecraftforge.client.event.ClientChatReceivedEvent;

@Module.Declaration(name="QueueNotifier", category=Category.Misc)
public class QueueNotifier
extends Module {
    BooleanSetting techale = this.registerBoolean("Techale mode", false);
    @EventHandler
    Listener<ClientChatReceivedEvent> listener = new Listener<ClientChatReceivedEvent>(event -> {
        String message = event.getMessage().func_150260_c();
        if (message.matches("Position in queue: ([1-5]\\b|[12]0)") && event.getType() == ChatType.SYSTEM) {
            if (((Boolean)this.techale.getValue()).booleanValue()) {
                for (int i = 0; i < 29; ++i) {
                    this.playSound();
                }
            }
            this.playSound();
        }
    }, new Predicate[0]);

    private void playSound() {
        QueueNotifier.mc.field_147127_av.func_147682_a((ISound)PositionedSoundRecord.func_194007_a((SoundEvent)SoundEvents.field_187802_ec, (float)1.0f, (float)1.0f));
    }
}

