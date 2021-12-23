/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.mixin.mixins;

import com.gamesense.api.util.font.FontUtil;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.gui.ColorMain;
import net.minecraft.client.gui.FontRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={FontRenderer.class})
public class MixinFontRenderer {
    @Redirect(method={"drawStringWithShadow"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/gui/FontRenderer;drawString(Ljava/lang/String;FFIZ)I"))
    public int drawCustomFontStringWithShadow(FontRenderer fontRenderer, String text, float x, float y, int color, boolean dropShadow) {
        ColorMain colorMain = ModuleManager.getModule(ColorMain.class);
        return (Boolean)colorMain.textFont.getValue() != false ? (int)FontUtil.drawStringWithShadow(true, text, (int)x, (int)y, new GSColor(color)) : fontRenderer.func_175065_a(text, x, y, color, true);
    }
}

