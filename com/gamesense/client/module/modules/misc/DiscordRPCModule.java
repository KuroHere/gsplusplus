/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.misc;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.setting.values.StringSetting;
import com.gamesense.api.util.player.PlayerUtil;
import com.gamesense.client.manager.managers.TotemPopManager;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.Items;

@Module.Declaration(name="DiscordRPC", category=Category.Misc, drawn=false)
public class DiscordRPCModule
extends Module {
    ArrayList<String> options = new ArrayList<String>(){
        {
            this.add("None");
            this.add("Dimension");
            this.add("Health");
            this.add("Kill Counter");
            this.add("Name");
            this.add("Pop Counter");
            this.add("Server");
            this.add("Speed");
            this.add("Status");
            this.add("Version");
            this.add("Ping");
            this.add("Fps");
        }
    };
    static final String discordID = "840996509880680479";
    static final DiscordRichPresence discordRichPresence = new DiscordRichPresence();
    static final DiscordRPC discordRPC = DiscordRPC.INSTANCE;
    int curImg = -1;
    ModeSetting imgType = this.registerMode("Image", Arrays.asList("gs++", "insigna", "luk", "aether"), "gs++");
    ModeSetting lowImg = this.registerMode("Low Img", Arrays.asList("none", "nocatsnolife", "sable__", "phantom826", "EightTwoSix", "doogie13", "soulbond", "anonymousplayer", "hoosier", "toxicaven", "0b00101010"), "none");
    BooleanSetting animateGs = this.registerBoolean("Animated gs++", true);
    IntegerSetting msChange = this.registerInteger("Image Change", 2000, 250, 5000);
    ModeSetting timeDisplay = this.registerMode("Display Time", Arrays.asList("Linear", "Reverse", "None"), "Linear");
    static final String MPS = "m/s";
    static final String KMH = "km/h";
    static final String MPH = "mph";
    ModeSetting speedUnit = this.registerMode("Unit", Arrays.asList("m/s", "km/h", "mph"), "km/h");
    BooleanSetting firstLine = this.registerBoolean("First Line", true);
    StringSetting formatFirst = this.registerString("1Format", "%1 %2 %3");
    ModeSetting Line1Option1 = this.registerMode("Opt 1: ", Arrays.asList(this.options.toArray(new String[0])), "Version");
    ModeSetting Line1Option2 = this.registerMode("Opt 2: ", Arrays.asList(this.options.toArray(new String[0])), "Server");
    ModeSetting Line1Option3 = this.registerMode("Opt 3: ", Arrays.asList(this.options.toArray(new String[0])), "None");
    BooleanSetting secondLine = this.registerBoolean("Second Line", true);
    StringSetting formatSecond = this.registerString("2Format", "%1 %2 %3");
    ModeSetting Line2Option1 = this.registerMode("Opt 1; ", Arrays.asList(this.options.toArray(new String[0])), "Status");
    ModeSetting Line2Option2 = this.registerMode("Opt 2; ", Arrays.asList(this.options.toArray(new String[0])), "Health");
    ModeSetting Line2Option3 = this.registerMode("Opt 3; ", Arrays.asList(this.options.toArray(new String[0])), "Speed");
    private long prevTimeImg;

    String getVersion() {
        return "gs++ v2.3.4";
    }

    String getDimension() {
        return DiscordRPCModule.mc.field_71441_e == null ? "In the main menu" : (DiscordRPCModule.mc.field_71441_e.field_73011_w.getDimension() == 0 ? "Overworld" : (DiscordRPCModule.mc.field_71441_e.field_73011_w.getDimension() == 1 ? "End" : "Nether"));
    }

    String getFps() {
        return String.valueOf(DiscordRPCModule.mc.field_71420_M);
    }

    String getHealth() {
        return (int)PlayerUtil.getHealth() + "hp";
    }

    String getStatus() {
        return DiscordRPCModule.mc.field_71439_g.field_71071_by.func_70440_f(2).func_77973_b().equals(Items.field_151163_ad) ? "Fighting" : "Chilling around";
    }

    String getKill() {
        return TotemPopManager.INSTANCE.getKills() + "kills";
    }

    String getPops() {
        return TotemPopManager.INSTANCE.getPops() + "pops";
    }

    String getServer() {
        return mc.func_147104_D() == null ? "Singleplayer" : DiscordRPCModule.mc.func_147104_D().field_78845_b;
    }

    String getPing() {
        String p = DiscordRPCModule.mc.field_71439_g == null || mc.func_147114_u() == null || mc.func_147114_u().func_175104_a(DiscordRPCModule.mc.field_71439_g.func_70005_c_()) == null ? "-1" : String.valueOf(Objects.requireNonNull(mc.func_147114_u().func_175104_a(DiscordRPCModule.mc.field_71439_g.func_70005_c_())).func_178853_c());
        p = p + "ping";
        return p;
    }

    String getMcName() {
        return DiscordRPCModule.mc.field_71439_g.func_70005_c_();
    }

    String getSpeed() {
        return (int)this.calcSpeed(DiscordRPCModule.mc.field_71439_g, (String)this.speedUnit.getValue()) + (String)this.speedUnit.getValue();
    }

    private double calcSpeed(EntityPlayerSP player, String unit) {
        double tps = 1000.0 / (double)DiscordRPCModule.mc.field_71428_T.field_194149_e;
        double xDiff = player.field_70165_t - player.field_70169_q;
        double zDiff = player.field_70161_v - player.field_70166_s;
        double speed = Math.hypot(xDiff, zDiff) * tps;
        switch (unit) {
            case "km/h": {
                speed *= 3.6;
                break;
            }
            case "mph": {
                speed *= 2.237;
                break;
            }
        }
        return speed;
    }

    @Override
    public void onEnable() {
        DiscordEventHandlers eventHandlers = new DiscordEventHandlers();
        eventHandlers.disconnected = (var1, var2) -> System.out.println("Discord RPC disconnected, var1: " + var1 + ", var2: " + var2);
        discordRPC.Discord_Initialize(discordID, eventHandlers, true, null);
        this.prevTimeImg = System.currentTimeMillis();
        switch ((String)this.timeDisplay.getValue()) {
            case "Linear": {
                DiscordRPCModule.discordRichPresence.startTimestamp = System.currentTimeMillis() / 1000L;
                break;
            }
            case "Reverse": {
                DiscordRPCModule.discordRichPresence.endTimestamp = DiscordRPCModule.discordRichPresence.startTimestamp + 100L;
                break;
            }
        }
        this.updateRpc();
    }

    @Override
    public void onUpdate() {
        this.updateRpc();
    }

    @Override
    public void onDisable() {
        discordRPC.Discord_Shutdown();
        discordRPC.Discord_ClearPresence();
    }

    void updateRpc() {
        this.updatePicture();
        this.updateStatus();
        discordRPC.Discord_UpdatePresence(discordRichPresence);
    }

    void updateStatus() {
        if (((Boolean)this.firstLine.getValue()).booleanValue()) {
            DiscordRPCModule.discordRichPresence.details = this.formatFirst.getText().replace("%1", this.getValues((String)this.Line1Option1.getValue())).replace("%2", this.getValues((String)this.Line1Option2.getValue())).replace("%3", this.getValues((String)this.Line1Option3.getValue()));
        }
        if (((Boolean)this.secondLine.getValue()).booleanValue()) {
            DiscordRPCModule.discordRichPresence.state = this.formatSecond.getText().replace("%1", this.getValues((String)this.Line2Option1.getValue())).replace("%2", this.getValues((String)this.Line2Option2.getValue())).replace("%3", this.getValues((String)this.Line2Option3.getValue()));
        }
    }

    String getValues(String values) {
        switch (values) {
            case "Dimension": {
                return this.getDimension();
            }
            case "Health": {
                return this.getHealth();
            }
            case "Kill Counter": {
                return this.getKill();
            }
            case "Pop Counter": {
                return this.getPops();
            }
            case "Name": {
                return this.getMcName();
            }
            case "Server": {
                return this.getServer();
            }
            case "Speed": {
                return this.getSpeed();
            }
            case "Status": {
                return this.getStatus();
            }
            case "Version": {
                return this.getVersion();
            }
            case "Ping": {
                return this.getPing();
            }
            case "Fps": {
                return this.getFps();
            }
        }
        return "";
    }

    void updatePicture() {
        String description;
        String imgNow;
        if ("gs++".equals(this.imgType.getValue())) {
            if (((Boolean)this.animateGs.getValue()).booleanValue()) {
                if (this.prevTimeImg + (long)((Integer)this.msChange.getValue()).intValue() < System.currentTimeMillis()) {
                    int maxImg = 4;
                    this.curImg = this.curImg >= maxImg ? 0 : this.curImg + 1;
                    this.prevTimeImg = System.currentTimeMillis();
                }
            } else {
                this.curImg = 0;
            }
            imgNow = "gs" + this.curImg;
            description = "gs++ engine";
        } else {
            imgNow = (String)this.imgType.getValue();
            description = imgNow + " powered by gs++";
        }
        DiscordRPCModule.discordRichPresence.largeImageKey = imgNow;
        DiscordRPCModule.discordRichPresence.largeImageText = description;
        if (!((String)this.lowImg.getValue()).equals("none")) {
            DiscordRPCModule.discordRichPresence.smallImageKey = (String)this.lowImg.getValue();
            DiscordRPCModule.discordRichPresence.smallImageText = ((String)this.lowImg.getValue()).equalsIgnoreCase(this.getMcName()) ? "Confirmed user" : "Not identified user";
        } else {
            DiscordRPCModule.discordRichPresence.smallImageKey = null;
        }
    }
}

