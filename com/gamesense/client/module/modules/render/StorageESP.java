/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.render;

import com.gamesense.api.event.events.RenderEvent;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.api.util.render.RenderUtil;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import java.util.List;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.world.BlockEvent;

@Module.Declaration(name="StorageESP", category=Category.Render)
public class StorageESP
extends Module {
    List<BlockPos> storage = new NonNullList<BlockPos>(){};
    @EventHandler
    final Listener<BlockEvent> blockEventListener = new Listener<BlockEvent>(event -> {
        if (event.getState().func_177230_c() instanceof BlockChest || event.getState().func_177230_c() instanceof BlockShulkerBox || event.getState().func_177230_c() instanceof BlockEnderChest) {
            this.storage.add(event.getPos());
        } else {
            this.storage.remove(event.getPos());
        }
    }, new Predicate[0]);

    @Override
    public void onWorldRender(RenderEvent event) {
        for (BlockPos pos : this.storage) {
            Block block = StorageESP.mc.field_71441_e.func_180495_p(pos).func_177230_c();
            int colour = block.field_181083_K.field_76291_p;
            int r = (int)((float)(colour >> 16 & 0xFF) / 255.0f);
            int g = (int)((float)(colour >> 8 & 0xFF) / 255.0f);
            int b = (int)((float)(colour & 0xFF) / 255.0f);
            RenderUtil.drawBox(pos, 1.0, new GSColor(r, g, b), 63);
            RenderUtil.drawBoundingBox(pos, 1.0, 1.0f, new GSColor(r, g, b));
        }
    }
}

