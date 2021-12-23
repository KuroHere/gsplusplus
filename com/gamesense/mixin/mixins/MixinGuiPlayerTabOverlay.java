/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.mixin.mixins;

import com.gamesense.api.util.player.social.SocialManager;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.gui.ColorMain;
import com.gamesense.client.module.modules.misc.ExtraTab;
import java.util.List;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={GuiPlayerTabOverlay.class})
public class MixinGuiPlayerTabOverlay {
    ExtraTab extraTab = ModuleManager.getModule(ExtraTab.class);

    @Redirect(method={"renderPlayerlist"}, at=@At(value="INVOKE", target="Ljava/util/List;subList(II)Ljava/util/List;", remap=false))
    public List<NetworkPlayerInfo> subListHook(List<NetworkPlayerInfo> list, int fromIndex, int toIndex) {
        return list.subList(fromIndex, this.extraTab.isEnabled() ? Math.min((Integer)this.extraTab.players.getValue(), list.size()) : toIndex);
    }

    @Inject(method={"getPlayerName"}, at={@At(value="HEAD")}, cancellable=true)
    public void getPlayerNameHead(NetworkPlayerInfo networkPlayerInfoIn, CallbackInfoReturnable<String> callbackInfoReturnable) {
        callbackInfoReturnable.setReturnValue(this.getPlayerNameGS(networkPlayerInfoIn));
    }

    private String getPlayerNameGS(NetworkPlayerInfo networkPlayerInfoIn) {
        String displayName;
        String string = displayName = networkPlayerInfoIn.func_178854_k() != null ? networkPlayerInfoIn.func_178854_k().func_150254_d() : ScorePlayerTeam.func_96667_a((Team)networkPlayerInfoIn.func_178850_i(), (String)networkPlayerInfoIn.func_178845_a().getName());
        if (SocialManager.isFriend(displayName)) {
            return ModuleManager.getModule(ColorMain.class).getFriendColor() + displayName;
        }
        if (SocialManager.isEnemy(displayName)) {
            return ModuleManager.getModule(ColorMain.class).getEnemyColor() + displayName;
        }
        return displayName;
    }
}

