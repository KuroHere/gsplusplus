/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.hud;

import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.util.player.social.SocialManager;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.api.util.world.combat.CrystalUtil;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.HUDModule;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.combat.AutoCrystalRewrite;
import com.gamesense.client.module.modules.combat.OffHand;
import com.lukflug.panelstudio.hud.HUDList;
import com.lukflug.panelstudio.hud.ListComponent;
import com.lukflug.panelstudio.setting.Labeled;
import com.lukflug.panelstudio.theme.ITheme;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

@Module.Declaration(name="CombatInfo", category=Category.HUD)
@HUDModule.Declaration(posX=0, posZ=150)
public class CombatInfo
extends HUDModule {
    ModeSetting infoType = this.registerMode("Type", Arrays.asList("Cyber", "Hoosiers"), "Hoosiers");
    ColorSetting color1 = this.registerColor("On", new GSColor(0, 255, 0, 255));
    ColorSetting color2 = this.registerColor("Off", new GSColor(255, 0, 0, 255));
    private final InfoList list = new InfoList();
    private static final BlockPos[] surroundOffset = new BlockPos[]{new BlockPos(0, 0, -1), new BlockPos(1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(-1, 0, 0)};
    private static final String[] hoosiersModules = new String[]{"AutoCrystal", "KillAura", "Surround", "AutoTrap", "SelfTrap"};
    private static final String[] hoosiersNames = new String[]{"AC", "KA", "SU", "AT", "ST"};

    @Override
    public void populate(ITheme theme) {
        this.component = new ListComponent(new Labeled(this.getName(), null, () -> true), this.position, this.getName(), this.list, 9, 1);
    }

    @Override
    public void onRender() {
        AutoCrystalRewrite autoCrystal = ModuleManager.getModule(AutoCrystalRewrite.class);
        this.list.totems = CombatInfo.mc.field_71439_g.field_71071_by.field_70462_a.stream().filter(itemStack -> itemStack.func_77973_b() == Items.field_190929_cY).mapToInt(ItemStack::func_190916_E).sum() + (CombatInfo.mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_190929_cY ? 1 : 0);
        this.list.players = CombatInfo.mc.field_71441_e.field_72996_f.stream().filter(entity -> entity instanceof EntityOtherPlayerMP).filter(entity -> !SocialManager.isFriend(entity.func_70005_c_())).filter(e -> (double)CombatInfo.mc.field_71439_g.func_70032_d(e) <= (Double)autoCrystal.placeRange.getValue()).map(entity -> (EntityOtherPlayerMP)entity).min(Comparator.comparing(cl -> Float.valueOf(CombatInfo.mc.field_71439_g.func_70032_d((Entity)cl)))).orElse(null);
        this.list.renderLby = false;
        ArrayList entities = new ArrayList(CombatInfo.mc.field_71441_e.field_73010_i.stream().filter(entityPlayer -> !SocialManager.isFriend(entityPlayer.func_70005_c_())).collect(Collectors.toList()));
        for (EntityPlayer e2 : entities) {
            int i = 0;
            for (BlockPos add : surroundOffset) {
                ++i;
                BlockPos o = new BlockPos(e2.func_174791_d().field_72450_a, e2.func_174791_d().field_72448_b, e2.func_174791_d().field_72449_c).func_177982_a(add.func_177958_n(), add.func_177956_o(), add.func_177952_p());
                if (CombatInfo.mc.field_71441_e.func_180495_p(o).func_177230_c() == Blocks.field_150343_Z) {
                    if (i == 1 && CrystalUtil.canPlaceCrystal(o.func_177964_d(1).func_177977_b(), (Boolean)autoCrystal.newPlace.getValue())) {
                        this.list.lby = true;
                        this.list.renderLby = true;
                        continue;
                    }
                    if (i == 2 && CrystalUtil.canPlaceCrystal(o.func_177965_g(1).func_177977_b(), (Boolean)autoCrystal.newPlace.getValue())) {
                        this.list.lby = true;
                        this.list.renderLby = true;
                        continue;
                    }
                    if (i == 3 && CrystalUtil.canPlaceCrystal(o.func_177970_e(1).func_177977_b(), (Boolean)autoCrystal.newPlace.getValue())) {
                        this.list.lby = true;
                        this.list.renderLby = true;
                        continue;
                    }
                    if (i != 4 || !CrystalUtil.canPlaceCrystal(o.func_177985_f(1).func_177977_b(), (Boolean)autoCrystal.newPlace.getValue())) continue;
                    this.list.lby = true;
                    this.list.renderLby = true;
                    continue;
                }
                this.list.lby = false;
                this.list.renderLby = true;
            }
        }
    }

    private static int getPing() {
        int p = -1;
        p = CombatInfo.mc.field_71439_g == null || mc.func_147114_u() == null || mc.func_147114_u().func_175104_a(CombatInfo.mc.field_71439_g.func_70005_c_()) == null ? -1 : mc.func_147114_u().func_175104_a(CombatInfo.mc.field_71439_g.func_70005_c_()).func_178853_c();
        return p;
    }

    private class InfoList
    implements HUDList {
        public int totems = 0;
        public EntityOtherPlayerMP players = null;
        public boolean renderLby = false;
        public boolean lby = false;

        private InfoList() {
        }

        @Override
        public int getSize() {
            if (((String)CombatInfo.this.infoType.getValue()).equals("Hoosiers")) {
                return hoosiersModules.length;
            }
            if (((String)CombatInfo.this.infoType.getValue()).equals("Cyber")) {
                return this.renderLby ? 6 : 5;
            }
            return 0;
        }

        @Override
        public String getItem(int index) {
            if (((String)CombatInfo.this.infoType.getValue()).equals("Hoosiers")) {
                if (ModuleManager.isModuleEnabled(hoosiersModules[index])) {
                    return hoosiersNames[index] + ": ON";
                }
                return hoosiersNames[index] + ": OFF";
            }
            if (((String)CombatInfo.this.infoType.getValue()).equals("Cyber")) {
                if (index == 0) {
                    return "gs++ v2.3.4";
                }
                if (index == 1) {
                    return "HTR";
                }
                if (index == 2) {
                    return "PLR";
                }
                if (index == 3) {
                    return "" + this.totems;
                }
                if (index == 4) {
                    return "PING " + CombatInfo.getPing();
                }
                return "LBY";
            }
            return "";
        }

        @Override
        public Color getItemColor(int index) {
            AutoCrystalRewrite autoCrystal = ModuleManager.getModule(AutoCrystalRewrite.class);
            if (((String)CombatInfo.this.infoType.getValue()).equals("Hoosiers")) {
                if (ModuleManager.isModuleEnabled(hoosiersModules[index])) {
                    return CombatInfo.this.color1.getValue();
                }
                return CombatInfo.this.color2.getValue();
            }
            if (((String)CombatInfo.this.infoType.getValue()).equals("Cyber")) {
                boolean on = false;
                if (index == 0) {
                    on = true;
                } else if (index == 1) {
                    if (this.players != null) {
                        on = (double)mc.field_71439_g.func_70032_d((Entity)this.players) <= (Double)autoCrystal.breakRange.getValue();
                    }
                } else if (index == 2) {
                    if (this.players != null) {
                        on = (double)mc.field_71439_g.func_70032_d((Entity)this.players) <= (Double)autoCrystal.placeRange.getValue();
                    }
                } else {
                    on = index == 3 ? this.totems > 0 && ModuleManager.isModuleEnabled(OffHand.class) : (index == 4 ? CombatInfo.getPing() <= 100 : this.lby);
                }
                if (on) {
                    return CombatInfo.this.color1.getValue();
                }
                return CombatInfo.this.color2.getValue();
            }
            return new Color(255, 255, 255);
        }

        @Override
        public boolean sortUp() {
            return false;
        }

        @Override
        public boolean sortRight() {
            return false;
        }
    }
}

