/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.misc;

import com.gamesense.api.event.events.PacketEvent;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.util.misc.NewChat;
import com.gamesense.api.util.player.social.Enemy;
import com.gamesense.api.util.player.social.Friend;
import com.gamesense.api.util.player.social.SocialManager;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.client.command.CommandManager;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.modules.combat.PistonCrystal;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

@Module.Declaration(name="ChatModifier", category=Category.Misc)
public class ChatModifier
extends Module {
    public ColorSetting backColor = this.registerColor("Background Color", new GSColor(0, 0, 0));
    public IntegerSetting alphaColor = this.registerInteger("Background Alpha", 0, 0, 255);
    public ColorSetting normalColor = this.registerColor("Normal Color", new GSColor(255, 255, 255));
    public ColorSetting specialColor = this.registerColor("Special Color", new GSColor(0, 255, 125));
    public BooleanSetting desyncRainbowNormal = this.registerBoolean("Desync Rainbow Normal", false);
    public BooleanSetting desyncRainbowSpecial = this.registerBoolean("Desync Rainbow Special", true);
    public BooleanSetting stopDesyncNormal = this.registerBoolean("Stop Desync Normal", false);
    public BooleanSetting stopDesyncSpecial = this.registerBoolean("Stop Desync Special", false);
    public ModeSetting rainbowType = this.registerMode("Rainbow Type", Arrays.asList("Slow", "Custom", "Sin", "Tan", "Secant", "Cosecant", "Cotangent"), "Custom");
    public IntegerSetting rainbowDesyncSmooth = this.registerInteger("Rainbow Desync Smooth", 250, 1, 1000);
    public DoubleSetting customAdd = this.registerDouble("customAdd", 12.0, 1.0, 40.0);
    public DoubleSetting customMultiply = this.registerDouble("customMultiply", 3.64, 1.0, 30.0);
    public IntegerSetting cutomDesync = this.registerInteger("Custom Desync Multiply", 1, 1, 100);
    public DoubleSetting heightSin = this.registerDouble("Height Sin", 1.0, 0.1, 20.0);
    public IntegerSetting multiplyHeight = this.registerInteger("Multiply Height Sin", 1, 1, 10);
    public DoubleSetting millSin = this.registerDouble("Mill Sin", 10.0, 0.1, 15.0);
    public BooleanSetting hideSlider = this.registerBoolean("Hide Slider", false);
    public IntegerSetting sliderWidth = this.registerInteger("Slider Width", 1, 0, 10);
    public IntegerSetting sliderSpace = this.registerInteger("Slider Space", 1, 0, 10);
    public ColorSetting firstColor = this.registerColor("First Color", new GSColor(0, 255, 0));
    public IntegerSetting firstAlpha = this.registerInteger("First Alpha", 255, 0, 255);
    public ColorSetting secondColor = this.registerColor("Second Color", new GSColor(255, 255, 255));
    public IntegerSetting secondAlpha = this.registerInteger("Second Alpha", 255, 0, 255);
    public ColorSetting thirdColor = this.registerColor("Third Color", new GSColor(255, 0, 0));
    public IntegerSetting thirdAlpha = this.registerInteger("Third Alpha", 255, 0, 255);
    public IntegerSetting upPosition = this.registerInteger("Up Translation", -1, -100, 700);
    public IntegerSetting leftPosition = this.registerInteger("Left Translation", -1, -1, 700);
    public DoubleSetting yScale = this.registerDouble("Height Scale", 1.0, 0.0, 3.0);
    public DoubleSetting xScale = this.registerDouble("Width Scale", 1.0, 0.0, 3.0);
    public IntegerSetting maxH = this.registerInteger("Max Height", -1, -1, 500);
    public IntegerSetting maxW = this.registerInteger("Max Width", -1, -1, 500);
    public ModeSetting animationtext = this.registerMode("Animation Text", Arrays.asList("None", "Down Up", "Left Right"), "Down Up");
    BooleanSetting greenText = this.registerBoolean("Green Text", false);
    BooleanSetting unFormattedText = this.registerBoolean("Unformatted Text", false);
    BooleanSetting chatTimeStamps = this.registerBoolean("Chat Time Stamp", true);
    ModeSetting format = this.registerMode("Format", Arrays.asList("H24:mm", "H12:mm", "H12:mm a", "H24:mm:ss", "H12:mm:ss", "H12:mm:ss a"), "H24:mm");
    ModeSetting decoration = this.registerMode("Deco", Arrays.asList("< >", "[ ]", "{ }", " "), "< >");
    public ColorSetting timeColor = this.registerColor("Time Color", new GSColor(85, 255, 255));
    BooleanSetting specialTime = this.registerBoolean("Special Color Time", true);
    BooleanSetting space = this.registerBoolean("Space", true);
    BooleanSetting fakeName = this.registerBoolean("Fake Name", false);
    BooleanSetting customName = this.registerBoolean("Custom Name", true);
    public ColorSetting friendColor = this.registerColor("Friend Color", new GSColor(85, 255, 255));
    BooleanSetting specialFriend = this.registerBoolean("Special Color Friend", false);
    public ColorSetting enemyColor = this.registerColor("Enemy Color", new GSColor(85, 255, 255));
    public ColorSetting playerColor = this.registerColor("Player Color", new GSColor(85, 255, 255));
    public BooleanSetting watermarkSpecial = this.registerBoolean("Watermark Special", true);
    public ColorSetting aqua = this.registerColor("Aqua", new GSColor(85, 255, 255));
    public ColorSetting black = this.registerColor("Black", new GSColor(0, 0, 0));
    public ColorSetting blue = this.registerColor("Blue", new GSColor(85, 85, 255));
    public ColorSetting dark_aqua = this.registerColor("Dark Aqua", new GSColor(0, 170, 170));
    public ColorSetting dark_blue = this.registerColor("Dark Blue", new GSColor(0, 0, 170));
    public ColorSetting dark_cyan = this.registerColor("Dark Cyan", new GSColor(0, 170, 170));
    public ColorSetting dark_gray = this.registerColor("Dark Gray", new GSColor(85, 85, 85));
    public ColorSetting dark_green = this.registerColor("Dark Green", new GSColor(0, 170, 0));
    public ColorSetting dark_purple = this.registerColor("Dark Purple", new GSColor(170, 0, 170));
    public ColorSetting dark_red = this.registerColor("Dark Red", new GSColor(170, 0, 0));
    public ColorSetting gray = this.registerColor("Gray", new GSColor(170, 170, 170));
    public ColorSetting green = this.registerColor("Green", new GSColor(85, 255, 85));
    public ColorSetting gold = this.registerColor("Gold", new GSColor(255, 170, 0));
    public ColorSetting yellow = this.registerColor("Yellow", new GSColor(255, 255, 85));
    public ColorSetting purple = this.registerColor("Purple", new GSColor(255, 85, 255));
    public ColorSetting red = this.registerColor("Red", new GSColor(255, 85, 85));
    public ColorSetting white = this.registerColor("White", new GSColor(255, 255, 255));
    boolean iniz = false;
    @EventHandler
    private final Listener<ClientChatReceivedEvent> chatReceivedEventListener = new Listener<ClientChatReceivedEvent>(event -> {
        ITextComponent output = event.getMessage();
        if (((Boolean)this.fakeName.getValue()).booleanValue()) {
            output = new TextComponentString(output.func_150254_d().replaceAll(ChatModifier.mc.field_71439_g.func_70005_c_(), "YourName"));
        }
        if (output.func_150254_d().contains("[Abyss]")) {
            PistonCrystal.printDebug("Ciao", false);
            output = new TextComponentString(output.func_150254_d().substring(3));
        }
        try {
            if (((Boolean)this.customName.getValue()).booleanValue()) {
                String name = output.func_150260_c().split(" ")[0];
                output = new TextComponentString((this.isFriend(name) ? (((Boolean)this.specialFriend.getValue()).booleanValue() ? "\u2063" : "\u2064") : (this.isEnemy(name) ? "\u2065" : "\u2066")) + name + ChatFormatting.RESET + output.func_150254_d().substring(output.func_150254_d().split(" ")[0].length()));
            }
            if (((Boolean)this.chatTimeStamps.getValue()).booleanValue()) {
                String decoRight;
                String decoLeft = ((String)this.decoration.getValue()).equalsIgnoreCase(" ") ? "" : ((String)this.decoration.getValue()).split(" ")[0];
                String string = decoRight = ((String)this.decoration.getValue()).equalsIgnoreCase(" ") ? "" : ((String)this.decoration.getValue()).split(" ")[1];
                if (((Boolean)this.space.getValue()).booleanValue()) {
                    decoRight = decoRight + " ";
                }
                String dateFormat = ((String)this.format.getValue()).replace("H24", "k").replace("H12", "h");
                String date = new SimpleDateFormat(dateFormat).format(new Date());
                TextComponentString time = new TextComponentString(((Boolean)this.specialTime.getValue() != false ? "\u2063" : "\u2067") + decoLeft + date + decoRight + TextFormatting.RESET);
                output = time.func_150257_a(output);
            }
            if (((Boolean)this.unFormattedText.getValue()).booleanValue()) {
                output = new TextComponentString(output.func_150260_c());
            }
        }
        catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
            // empty catch block
        }
        event.setMessage(output);
    }, new Predicate[0]);
    @EventHandler
    private final Listener<PacketEvent.Send> listener = new Listener<PacketEvent.Send>(event -> {
        if (((Boolean)this.greenText.getValue()).booleanValue() && event.getPacket() instanceof CPacketChatMessage) {
            if (((CPacketChatMessage)event.getPacket()).func_149439_c().startsWith("/") || ((CPacketChatMessage)event.getPacket()).func_149439_c().startsWith(CommandManager.getCommandPrefix())) {
                return;
            }
            String message = ((CPacketChatMessage)event.getPacket()).func_149439_c();
            String prefix = "";
            prefix = ">";
            String s = prefix + message;
            if (s.length() > 255) {
                return;
            }
            ((CPacketChatMessage)event.getPacket()).field_149440_a = s;
        }
    }, new Predicate[0]);

    public float clamp(float number, float min, float max) {
        return number < min ? min : Math.min(number, max);
    }

    @Override
    protected void onEnable() {
        this.iniz = false;
    }

    @Override
    public void onUpdate() {
        if (ChatModifier.mc.field_71439_g == null || ChatModifier.mc.field_71441_e == null) {
            return;
        }
        if (!this.iniz) {
            ObfuscationReflectionHelper.setPrivateValue(GuiIngame.class, (Object)Minecraft.func_71410_x().field_71456_v, (Object)((Object)new NewChat(Minecraft.func_71410_x())), (String[])new String[]{"field_73840_e"});
            this.iniz = true;
        }
    }

    @Override
    protected void onDisable() {
        ObfuscationReflectionHelper.setPrivateValue(GuiIngame.class, (Object)Minecraft.func_71410_x().field_71456_v, (Object)new GuiNewChat(Minecraft.func_71410_x()), (String[])new String[]{"field_73840_e"});
    }

    private boolean isFriend(String name) {
        name = name.toLowerCase();
        for (Friend friend : SocialManager.getFriends()) {
            if (!name.contains(friend.getName().toLowerCase())) continue;
            return true;
        }
        return false;
    }

    private boolean isEnemy(String name) {
        name = name.toLowerCase();
        for (Enemy enemy : SocialManager.getEnemies()) {
            if (!name.contains(enemy.getName().toLowerCase())) continue;
            return true;
        }
        return false;
    }
}

