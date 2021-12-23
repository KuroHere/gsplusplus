/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.render;

import com.gamesense.api.event.events.RenderEvent;
import com.gamesense.api.event.events.ShaderColorEvent;
import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.util.player.social.SocialManager;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.api.util.render.RenderUtil;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.gamesense.mixin.mixins.accessor.IRenderGlobal;
import com.gamesense.mixin.mixins.accessor.IShaderGroup;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.shader.Shader;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.client.shader.ShaderUniform;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;

@Module.Declaration(name="ESP", category=Category.Render)
public class ESP
extends Module {
    List<String> Modes = Arrays.asList("None", "Box", "Direction", "Glowing", "Test");
    IntegerSetting range = this.registerInteger("Range", 100, 10, 260);
    IntegerSetting width = this.registerInteger("Line Width", 2, 1, 5);
    DoubleSetting blurDir = this.registerDouble("Blur Dir", 1.0, 0.0, 1.0);
    ModeSetting playerESPMode = this.registerMode("Player Esp", this.Modes, "Box");
    ModeSetting itemEsp = this.registerMode("Item Esp", this.Modes, "None");
    ModeSetting mobEsp = this.registerMode("Entity Esp", this.Modes, "None");
    ModeSetting crystalEsp = this.registerMode("Crystal Esp", this.Modes, "None");
    ColorSetting playerColor = this.registerColor("Player Color", new GSColor(255, 255, 0));
    ColorSetting friendColor = this.registerColor("Friend Color", new GSColor(0, 255, 255));
    ColorSetting enemyColor = this.registerColor("Enemy Color", new GSColor(255, 0, 0));
    ColorSetting mobColor = this.registerColor("Mob Color", new GSColor(0, 255, 0));
    ColorSetting itemColor = this.registerColor("Item Color", new GSColor(255, 255, 255));
    ColorSetting crystalColor = this.registerColor("Crystal Color", new GSColor(255, 0, 255));
    int opacityGradient;
    @EventHandler
    private final Listener<ShaderColorEvent> eventListener = new Listener<ShaderColorEvent>(event -> {
        Entity e = event.getEntity();
        boolean cancel = false;
        GSColor color = null;
        if (e instanceof EntityPlayer && ((String)this.playerESPMode.getValue()).equals("Glowing")) {
            color = this.playerColor.getValue();
            cancel = true;
        } else if (e instanceof EntityCreature && ((String)this.mobEsp.getValue()).equals("Glowing")) {
            color = this.mobColor.getValue();
            cancel = true;
        } else if (e instanceof EntityItem && ((String)this.itemEsp.getValue()).equals("glowing")) {
            color = this.itemColor.getValue();
            cancel = true;
        } else if (e instanceof EntityEnderCrystal && ((String)this.crystalEsp.getValue()).equals("Glowing")) {
            color = this.crystalColor.getValue();
            cancel = true;
        }
        if (cancel) {
            event.setColor(color);
            event.cancel();
        }
    }, new Predicate[0]);

    @Override
    public void onWorldRender(RenderEvent event) {
        ESP.mc.field_71441_e.field_72996_f.stream().filter(entity -> entity != ESP.mc.field_71439_g).filter(this::rangeEntityCheck).forEach(entity -> {
            if (entity instanceof EntityPlayer) {
                this.render((String)this.playerESPMode.getValue(), (Entity)entity, SocialManager.isFriend(entity.func_70005_c_()) ? this.friendColor.getValue() : (SocialManager.isEnemy(entity.func_70005_c_()) ? this.enemyColor.getValue() : this.playerColor.getValue()));
            } else if (entity instanceof EntityCreature) {
                this.render((String)this.mobEsp.getValue(), (Entity)entity, this.mobColor.getValue());
            } else if (entity instanceof EntityItem) {
                this.render((String)this.itemEsp.getValue(), (Entity)entity, this.itemColor.getValue());
            } else if (entity instanceof EntityEnderCrystal) {
                this.render((String)this.crystalEsp.getValue(), (Entity)entity, this.crystalColor.getValue());
            }
        });
    }

    void render(String type, Entity e, GSColor color) {
        switch (type) {
            case "Box": {
                e.func_184195_f(false);
                RenderUtil.drawBoundingBox(e.func_174813_aQ(), (double)((Integer)this.width.getValue()).intValue(), color);
                break;
            }
            case "Direction": {
                e.func_184195_f(false);
                RenderUtil.drawBoxWithDirection(e.func_174813_aQ(), color, e.field_70177_z, ((Integer)this.width.getValue()).intValue(), 0);
                break;
            }
            case "Glowing": {
                e.func_184195_f(true);
                ShaderGroup outlineShaderGroup = ((IRenderGlobal)ESP.mc.field_71438_f).getEntityOutlineShader();
                List<Shader> shaders = ((IShaderGroup)outlineShaderGroup).getListShaders();
                shaders.forEach(shader -> {
                    ShaderUniform blurDir;
                    ShaderUniform outlineRadius = shader.func_148043_c().func_147991_a("Radius");
                    if (outlineRadius != null) {
                        outlineRadius.func_148090_a(((Integer)this.width.getValue()).floatValue());
                    }
                    if ((blurDir = shader.func_148043_c().func_147991_a("BlurDir")) != null) {
                        blurDir.func_148090_a(((Double)this.blurDir.getValue()).floatValue());
                    }
                });
            }
        }
    }

    @Override
    public void onDisable() {
        ESP.mc.field_71441_e.field_72996_f.forEach(entity -> entity.func_184195_f(false));
    }

    private boolean rangeEntityCheck(Entity entity) {
        if (entity.func_70032_d((Entity)ESP.mc.field_71439_g) > (float)((Integer)this.range.getValue()).intValue()) {
            return false;
        }
        this.opacityGradient = entity.func_70032_d((Entity)ESP.mc.field_71439_g) >= 180.0f ? 50 : (entity.func_70032_d((Entity)ESP.mc.field_71439_g) >= 130.0f && entity.func_70032_d((Entity)ESP.mc.field_71439_g) < 180.0f ? 100 : (entity.func_70032_d((Entity)ESP.mc.field_71439_g) >= 80.0f && entity.func_70032_d((Entity)ESP.mc.field_71439_g) < 130.0f ? 150 : (entity.func_70032_d((Entity)ESP.mc.field_71439_g) >= 30.0f && entity.func_70032_d((Entity)ESP.mc.field_71439_g) < 80.0f ? 200 : 255)));
        return true;
    }
}

