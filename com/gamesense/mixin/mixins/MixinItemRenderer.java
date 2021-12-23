/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.mixin.mixins;

import com.gamesense.api.event.events.TransformSideFirstPersonEvent;
import com.gamesense.client.GameSense;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.render.NoRender;
import com.gamesense.client.module.modules.render.ViewModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ItemRenderer.class})
public abstract class MixinItemRenderer {
    private boolean injection = true;
    @Shadow
    public ItemStack field_187468_e;
    @Shadow
    @Final
    public Minecraft field_78455_a;

    @Shadow
    public abstract void func_187457_a(AbstractClientPlayer var1, float var2, float var3, EnumHand var4, float var5, ItemStack var6, float var7);

    @Shadow
    protected abstract void func_187456_a(float var1, float var2, EnumHandSide var3);

    @Shadow
    protected abstract void func_187463_a(float var1, float var2, float var3);

    @Shadow
    protected abstract void func_187465_a(float var1, EnumHandSide var2, float var3, ItemStack var4);

    @Shadow
    protected abstract void func_187459_b(EnumHandSide var1, float var2);

    @Shadow
    protected abstract void func_187454_a(float var1, EnumHandSide var2, ItemStack var3);

    @Shadow
    protected abstract void func_187453_a(EnumHandSide var1, float var2);

    @Shadow
    public abstract void func_187462_a(EntityLivingBase var1, ItemStack var2, ItemCameraTransforms.TransformType var3, boolean var4);

    /*
     * Enabled aggressive block sorting
     */
    @Inject(method={"renderItemInFirstPerson(Lnet/minecraft/client/entity/AbstractClientPlayer;FFLnet/minecraft/util/EnumHand;FLnet/minecraft/item/ItemStack;F)V"}, at={@At(value="HEAD")}, cancellable=true)
    public void prova(AbstractClientPlayer player, float p_187457_2_, float p_187457_3_, EnumHand hand, float p_187457_5_, ItemStack stack, float p_187457_7_, CallbackInfo ci) {
        ViewModel viewModel = ModuleManager.getModule(ViewModel.class);
        if (viewModel.isEnabled()) {
            boolean flag = hand == EnumHand.MAIN_HAND;
            EnumHandSide enumhandside = flag ? player.func_184591_cq() : player.func_184591_cq().func_188468_a();
            GlStateManager.func_179094_E();
            boolean popAfter = true;
            if (stack.func_190926_b()) {
                if (flag && !player.func_82150_aj()) {
                    float addX = 0.0f;
                    float addY = 0.0f;
                    if (((Boolean)viewModel.hand.getValue()).booleanValue()) {
                        if (enumhandside == EnumHandSide.LEFT) {
                            addX = (float)((double)addX + (Double)viewModel.offX.getValue());
                            addY = (float)((double)addY + (Double)viewModel.offY.getValue());
                        } else {
                            addX = (float)((double)addX + (Double)viewModel.mainX.getValue());
                            addY = (float)((double)addY + (Double)viewModel.mainY.getValue());
                        }
                    }
                    this.func_187456_a(p_187457_7_ + addX, p_187457_5_ + addY, enumhandside);
                }
            } else if (stack.func_77973_b() instanceof ItemMap) {
                if (flag && this.field_187468_e.func_190926_b()) {
                    this.func_187463_a(p_187457_3_, p_187457_7_, p_187457_5_);
                } else {
                    this.func_187465_a(p_187457_7_, enumhandside, p_187457_5_, stack);
                }
            } else {
                boolean flag1;
                boolean bl = flag1 = enumhandside == EnumHandSide.RIGHT;
                if (player.func_184587_cr() && player.func_184605_cv() > 0 && player.func_184600_cs() == hand) {
                    int j = flag1 ? 1 : -1;
                    switch (stack.func_77975_n()) {
                        case NONE: {
                            this.func_187459_b(enumhandside, p_187457_7_);
                            break;
                        }
                        case EAT: 
                        case DRINK: {
                            if (((Boolean)viewModel.cancelEating.getValue()).booleanValue()) {
                                TransformSideFirstPersonEvent event = new TransformSideFirstPersonEvent(enumhandside);
                                GameSense.EVENT_BUS.post(event);
                                popAfter = false;
                                if (enumhandside == EnumHandSide.LEFT) {
                                    this.func_187459_b(enumhandside, p_187457_7_);
                                    break;
                                }
                                this.func_187453_a(enumhandside, p_187457_5_);
                                break;
                            }
                            GlStateManager.func_179121_F();
                            if (((Boolean)viewModel.leftDipendentRight.getValue()).booleanValue() || enumhandside == EnumHandSide.LEFT && this.field_78455_a.field_71439_g.func_184614_ca().func_190926_b()) {
                                GlStateManager.func_179094_E();
                            }
                            GlStateManager.func_179137_b((double)((Double)viewModel.xEat.getValue()), (double)((Double)viewModel.yEat.getValue()), (double)((Double)viewModel.zEat.getValue()));
                            GL11.glRotatef((float)((Integer)viewModel.xEatRotate.getValue()).intValue(), (float)1.0f, (float)0.0f, (float)0.0f);
                            GL11.glRotatef((float)((Integer)viewModel.yEatRotate.getValue()).intValue(), (float)0.0f, (float)1.0f, (float)0.0f);
                            GL11.glRotatef((float)((Integer)viewModel.zEatRotate.getValue()).intValue(), (float)0.0f, (float)0.0f, (float)1.0f);
                            GlStateManager.func_179139_a((double)((Double)viewModel.xScaleEat.getValue()), (double)((Double)viewModel.yScaleEat.getValue()), (double)((Double)viewModel.zScaleEat.getValue()));
                            popAfter = false;
                            break;
                        }
                        case BLOCK: {
                            this.func_187459_b(enumhandside, p_187457_7_);
                            break;
                        }
                        case BOW: {
                            if (!((Boolean)viewModel.cancelStandardBow.getValue()).booleanValue()) {
                                this.func_187459_b(enumhandside, p_187457_7_);
                                GlStateManager.func_179109_b((float)((float)j * -0.2785682f), (float)0.18344387f, (float)0.15731531f);
                                GlStateManager.func_179114_b((float)-13.935f, (float)1.0f, (float)0.0f, (float)0.0f);
                                GlStateManager.func_179114_b((float)((float)j * 35.3f), (float)0.0f, (float)1.0f, (float)0.0f);
                                GlStateManager.func_179114_b((float)((float)j * -9.785f), (float)0.0f, (float)0.0f, (float)1.0f);
                                float f5 = (float)stack.func_77988_m() - ((float)this.field_78455_a.field_71439_g.func_184605_cv() - p_187457_2_ + 1.0f);
                                float f6 = f5 / 20.0f;
                                f6 = (f6 * f6 + f6 * 2.0f) / 3.0f;
                                if (f6 > 1.0f) {
                                    f6 = 1.0f;
                                }
                                if (f6 > 0.1f) {
                                    float f7 = MathHelper.func_76126_a((float)((f5 - 0.1f) * 1.3f));
                                    float f3 = f6 - 0.1f;
                                    float f4 = f7 * f3;
                                    GlStateManager.func_179109_b((float)(f4 * 0.0f), (float)(f4 * 0.004f), (float)(f4 * 0.0f));
                                }
                                GlStateManager.func_179109_b((float)(f6 * 0.0f), (float)(f6 * 0.0f), (float)(f6 * 0.04f));
                                GlStateManager.func_179152_a((float)1.0f, (float)1.0f, (float)(1.0f + f6 * 0.2f));
                                GlStateManager.func_179114_b((float)((float)j * 45.0f), (float)0.0f, (float)-1.0f, (float)0.0f);
                                if (!((Boolean)viewModel.leftDipendentRight.getValue()).booleanValue() && (enumhandside != EnumHandSide.LEFT || !this.field_78455_a.field_71439_g.func_184614_ca().func_190926_b())) break;
                                GlStateManager.func_179094_E();
                                break;
                            }
                            TransformSideFirstPersonEvent event = new TransformSideFirstPersonEvent(enumhandside);
                            GameSense.EVENT_BUS.post(event);
                            popAfter = false;
                            break;
                        }
                    }
                } else {
                    float f = -0.4f * MathHelper.func_76126_a((float)(MathHelper.func_76129_c((float)p_187457_5_) * (float)Math.PI));
                    float f1 = 0.2f * MathHelper.func_76126_a((float)(MathHelper.func_76129_c((float)p_187457_5_) * ((float)Math.PI * 2)));
                    float f2 = -0.2f * MathHelper.func_76126_a((float)(p_187457_5_ * (float)Math.PI));
                    int i = flag1 ? 1 : -1;
                    GlStateManager.func_179109_b((float)((float)i * f), (float)f1, (float)f2);
                    TransformSideFirstPersonEvent event = new TransformSideFirstPersonEvent(enumhandside);
                    GameSense.EVENT_BUS.post(event);
                    popAfter = false;
                    if (enumhandside == EnumHandSide.LEFT) {
                        this.func_187459_b(enumhandside, p_187457_7_);
                    } else {
                        this.func_187453_a(enumhandside, p_187457_5_);
                    }
                }
                this.func_187462_a((EntityLivingBase)player, stack, flag1 ? ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND : ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, !flag1);
                if (((Boolean)viewModel.leftDipendentRight.getValue()).booleanValue() || enumhandside == EnumHandSide.LEFT && this.field_78455_a.field_71439_g.func_184614_ca().func_190926_b()) {
                    GlStateManager.func_179121_F();
                }
            }
            if (popAfter) {
                GlStateManager.func_179121_F();
            }
            ci.cancel();
        }
    }

    @Inject(method={"renderOverlays"}, at={@At(value="HEAD")}, cancellable=true)
    public void renderOverlays(float partialTicks, CallbackInfo callbackInfo) {
        NoRender noRender = ModuleManager.getModule(NoRender.class);
        if (noRender.isEnabled() && ((Boolean)noRender.noOverlay.getValue()).booleanValue()) {
            callbackInfo.cancel();
        }
    }
}

