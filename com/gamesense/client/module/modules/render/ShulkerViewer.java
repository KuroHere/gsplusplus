/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.render;

import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.api.util.font.FontUtil;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.api.util.render.RenderUtil;
import com.gamesense.client.clickgui.GameSenseGUI;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.gui.ColorMain;
import java.awt.Point;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;

@Module.Declaration(name="ShulkerViewer", category=Category.Render)
public class ShulkerViewer
extends Module {
    public ColorSetting outlineColor = this.registerColor("Outline", new GSColor(255, 0, 0, 255));
    public ColorSetting fillColor = this.registerColor("Fill", new GSColor(0, 0, 0, 255));

    public void renderShulkerPreview(ItemStack itemStack, int posX, int posY, int width, int height) {
        GSColor outline = new GSColor(this.outlineColor.getValue(), 255);
        GSColor fill = new GSColor(this.fillColor.getValue(), 200);
        RenderUtil.draw2DRect(posX + 1, posY + 1, width - 2, height - 2, 1000, fill);
        RenderUtil.draw2DRect(posX, posY, width, 1, 1000, outline);
        RenderUtil.draw2DRect(posX, posY + height - 1, width, 1, 1000, outline);
        RenderUtil.draw2DRect(posX, posY, 1, height, 1000, outline);
        RenderUtil.draw2DRect(posX + width - 1, posY, 1, height, 1000, outline);
        GlStateManager.func_179097_i();
        FontUtil.drawStringWithShadow((Boolean)ModuleManager.getModule(ColorMain.class).customFont.getValue(), itemStack.func_82833_r(), posX + 3, posY + 3, new GSColor(255, 255, 255, 255));
        GlStateManager.func_179126_j();
        NonNullList contentItems = NonNullList.func_191197_a((int)27, (Object)ItemStack.field_190927_a);
        ItemStackHelper.func_191283_b((NBTTagCompound)itemStack.func_77978_p().func_74775_l("BlockEntityTag"), (NonNullList)contentItems);
        for (int i = 0; i < contentItems.size(); ++i) {
            int finalX = posX + 1 + i % 9 * 18;
            int finalY = posY + 31 + (i / 9 - 1) * 18;
            GameSenseGUI.renderItemTest((ItemStack)contentItems.get(i), new Point(finalX, finalY));
        }
    }
}

