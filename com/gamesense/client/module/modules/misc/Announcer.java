/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.misc;

import com.gamesense.api.event.events.DestroyBlockEvent;
import com.gamesense.api.event.events.PacketEvent;
import com.gamesense.api.event.events.PlayerJumpEvent;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemFood;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;

@Module.Declaration(name="Announcer", category=Category.Misc)
public class Announcer
extends Module {
    public BooleanSetting clientSide = this.registerBoolean("Client Side", false);
    BooleanSetting walk = this.registerBoolean("Walk", true);
    BooleanSetting place = this.registerBoolean("Place", true);
    BooleanSetting jump = this.registerBoolean("Jump", true);
    BooleanSetting breaking = this.registerBoolean("Breaking", true);
    BooleanSetting attack = this.registerBoolean("Attack", true);
    BooleanSetting eat = this.registerBoolean("Eat", true);
    public BooleanSetting clickGui = this.registerBoolean("GUI", true);
    IntegerSetting delay = this.registerInteger("Delay", 1, 1, 20);
    public static int blockBrokeDelay = 0;
    static int blockPlacedDelay = 0;
    static int jumpDelay = 0;
    static int attackDelay = 0;
    static int eattingDelay = 0;
    static long lastPositionUpdate;
    static double lastPositionX;
    static double lastPositionY;
    static double lastPositionZ;
    private static double speed;
    String heldItem = "";
    int blocksPlaced = 0;
    int blocksBroken = 0;
    int eaten = 0;
    public static String walkMessage;
    public static String placeMessage;
    public static String jumpMessage;
    public static String breakMessage;
    public static String attackMessage;
    public static String eatMessage;
    public static String guiMessage;
    public static String[] walkMessages;
    public static String[] placeMessages;
    public static String[] jumpMessages;
    public static String[] breakMessages;
    public static String[] eatMessages;
    @EventHandler
    private final Listener<LivingEntityUseItemEvent.Finish> eatListener = new Listener<LivingEntityUseItemEvent.Finish>(event -> {
        int randomNum = ThreadLocalRandom.current().nextInt(1, 11);
        if (event.getEntity() == Announcer.mc.field_71439_g && (event.getItem().func_77973_b() instanceof ItemFood || event.getItem().func_77973_b() instanceof ItemAppleGold)) {
            ++this.eaten;
            if (eattingDelay >= 300 * (Integer)this.delay.getValue() && ((Boolean)this.eat.getValue()).booleanValue() && this.eaten > randomNum) {
                Random random = new Random();
                if (((Boolean)this.clientSide.getValue()).booleanValue()) {
                    MessageBus.sendClientPrefixMessage(eatMessages[random.nextInt(eatMessages.length)].replace("{amount}", " " + this.eaten).replace("{name}", " " + Announcer.mc.field_71439_g.func_184614_ca().func_82833_r()));
                } else {
                    MessageBus.sendServerMessage(eatMessages[random.nextInt(eatMessages.length)].replace("{amount}", " " + this.eaten).replace("{name}", " " + Announcer.mc.field_71439_g.func_184614_ca().func_82833_r()));
                }
                this.eaten = 0;
                eattingDelay = 0;
            }
        }
    }, new Predicate[0]);
    @EventHandler
    private final Listener<PacketEvent.Send> sendListener = new Listener<PacketEvent.Send>(event -> {
        if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock && Announcer.mc.field_71439_g.func_184586_b(EnumHand.MAIN_HAND).func_77973_b() instanceof ItemBlock) {
            ++this.blocksPlaced;
            int randomNum = ThreadLocalRandom.current().nextInt(1, 11);
            if (blockPlacedDelay >= 150 * (Integer)this.delay.getValue() && ((Boolean)this.place.getValue()).booleanValue() && this.blocksPlaced > randomNum) {
                Random random = new Random();
                String msg = placeMessages[random.nextInt(placeMessages.length)].replace("{amount}", " " + this.blocksPlaced).replace("{name}", " " + Announcer.mc.field_71439_g.func_184614_ca().func_82833_r());
                if (((Boolean)this.clientSide.getValue()).booleanValue()) {
                    MessageBus.sendClientPrefixMessage(msg);
                } else {
                    MessageBus.sendServerMessage(msg);
                }
                this.blocksPlaced = 0;
                blockPlacedDelay = 0;
            }
        }
    }, new Predicate[0]);
    @EventHandler
    private final Listener<DestroyBlockEvent> destroyListener = new Listener<DestroyBlockEvent>(event -> {
        ++this.blocksBroken;
        int randomNum = ThreadLocalRandom.current().nextInt(1, 11);
        if (blockBrokeDelay >= 300 * (Integer)this.delay.getValue() && ((Boolean)this.breaking.getValue()).booleanValue() && this.blocksBroken > randomNum) {
            Random random = new Random();
            String msg = breakMessages[random.nextInt(breakMessages.length)].replace("{amount}", " " + this.blocksBroken).replace("{name}", " " + Announcer.mc.field_71441_e.func_180495_p(event.getBlockPos()).func_177230_c().func_149732_F());
            if (((Boolean)this.clientSide.getValue()).booleanValue()) {
                MessageBus.sendClientPrefixMessage(msg);
            } else {
                MessageBus.sendServerMessage(msg);
            }
            this.blocksBroken = 0;
            blockBrokeDelay = 0;
        }
    }, new Predicate[0]);
    @EventHandler
    private final Listener<AttackEntityEvent> attackListener = new Listener<AttackEntityEvent>(event -> {
        if (((Boolean)this.attack.getValue()).booleanValue() && !(event.getTarget() instanceof EntityEnderCrystal) && attackDelay >= 300 * (Integer)this.delay.getValue()) {
            String msg = attackMessage.replace("{name}", " " + event.getTarget().func_70005_c_()).replace("{item}", " " + Announcer.mc.field_71439_g.func_184614_ca().func_82833_r());
            if (((Boolean)this.clientSide.getValue()).booleanValue()) {
                MessageBus.sendClientPrefixMessage(msg);
            } else {
                MessageBus.sendServerMessage(msg);
            }
            attackDelay = 0;
        }
    }, new Predicate[0]);
    @EventHandler
    private final Listener<PlayerJumpEvent> jumpListener = new Listener<PlayerJumpEvent>(event -> {
        if (((Boolean)this.jump.getValue()).booleanValue() && jumpDelay >= 300 * (Integer)this.delay.getValue()) {
            if (((Boolean)this.clientSide.getValue()).booleanValue()) {
                Random random = new Random();
                MessageBus.sendClientPrefixMessage(jumpMessages[random.nextInt(jumpMessages.length)]);
            } else {
                Random random = new Random();
                MessageBus.sendServerMessage(jumpMessages[random.nextInt(jumpMessages.length)]);
            }
            jumpDelay = 0;
        }
    }, new Predicate[0]);

    @Override
    public void onUpdate() {
        double d3;
        double d2;
        double d0;
        ++blockBrokeDelay;
        ++blockPlacedDelay;
        ++jumpDelay;
        ++attackDelay;
        ++eattingDelay;
        this.heldItem = Announcer.mc.field_71439_g.func_184614_ca().func_82833_r();
        if (((Boolean)this.walk.getValue()).booleanValue() && lastPositionUpdate + 5000L * (long)((Integer)this.delay.getValue()).intValue() < System.currentTimeMillis() && !((speed = Math.sqrt((d0 = lastPositionX - Announcer.mc.field_71439_g.field_70142_S) * d0 + (d2 = lastPositionY - Announcer.mc.field_71439_g.field_70137_T) * d2 + (d3 = lastPositionZ - Announcer.mc.field_71439_g.field_70136_U) * d3)) <= 1.0) && !(speed > 5000.0)) {
            String walkAmount = new DecimalFormat("0.00").format(speed);
            Random random = new Random();
            if (((Boolean)this.clientSide.getValue()).booleanValue()) {
                MessageBus.sendClientPrefixMessage(walkMessage.replace("{blocks}", " " + walkAmount));
            } else {
                MessageBus.sendServerMessage(walkMessages[random.nextInt(walkMessages.length)].replace("{blocks}", " " + walkAmount));
            }
            lastPositionUpdate = System.currentTimeMillis();
            lastPositionX = Announcer.mc.field_71439_g.field_70142_S;
            lastPositionY = Announcer.mc.field_71439_g.field_70137_T;
            lastPositionZ = Announcer.mc.field_71439_g.field_70136_U;
        }
    }

    @Override
    public void onEnable() {
        this.blocksPlaced = 0;
        this.blocksBroken = 0;
        this.eaten = 0;
        speed = 0.0;
        blockBrokeDelay = 0;
        blockPlacedDelay = 0;
        jumpDelay = 0;
        attackDelay = 0;
        eattingDelay = 0;
    }

    static {
        walkMessage = "I just walked{blocks} meters thanks to gs++!";
        placeMessage = "I just inserted{amount}{name} into the muliverse thanks to gs++!";
        jumpMessage = "I just hovered in the air thanks to gs++!";
        breakMessage = "I just snapped{amount}{name} out of existance thanks to gs++!";
        attackMessage = "I just disembowed{name} with a{item} thanks to gs++!";
        eatMessage = "I just gobbled up{amount}{name} thanks to gs++!";
        guiMessage = "I just opened my advanced hacking console thanks to gs++!";
        walkMessages = new String[]{"I just walked{blocks} meters thanks to gs++!", "!\u0644\u0642\u062f \u0645\u0634\u064a\u062a \u0644\u0644\u062a\u0648 \u0639\u0644\u0649 \u0628\u0639\u062f{blocks} \u0645\u062a\u0631 \u0645\u0646 \u0627\u0644\u0623\u0645\u062a\u0627\u0631 \u0628\u0641\u0636\u0644 gs++!", "\u00a1Acabo de caminar{blocks} metros gracias a gs++!", "Je viens de marcher{blocks} m\u00e8tres gr\u00e2ce \u00e0 gs++!", "\u05e4\u05e9\u05d5\u05d8 \u05d4\u05dc\u05db\u05ea\u05d9{blocks} \u05de\u05d8\u05e8\u05d9\u05dd \u05d1\u05d6\u05db\u05d5\u05ea gs++!", "Ich bin gerade{blocks} Meter dank gs++ gelaufen!\n", "gs++\u306e\u304a\u304b\u3052\u3067{blocks}\u30e1\u30fc\u30c8\u30eb\u6b69\u3044\u305f\u3068\u3053\u308d\u3067\u3059!", "Ik heb net{blocks} gelopen met dank aan gs++!", "\u039c\u03ce\u03bb\u03b9\u03c2 \u03c0\u03b5\u03c1\u03c0\u03ac\u03c4\u03b7\u03c3\u03b1{blocks} \u03bc\u03ad\u03c4\u03c1\u03b1 \u03c7\u03ac\u03c1\u03b7 \u03c4\u03bf gs++!", "gs++ sayesinde{blocks} metre y\u00fcr\u00fcd\u00fcm!", "W\u0142a\u015bnie przeszed\u0142em{blocks} metry dzi\u0119ki gs++!", "\u042f \u043f\u0440\u043e\u0441\u0442\u043e \u043f\u0440\u043e\u0448\u0435\u043b{blocks} \u043c\u0435\u0442\u0440\u043e\u0432 \u0431\u043b\u0430\u0433\u043e\u0434\u0430\u0440\u044f gs++!", "Jeg gik lige{blocks} meter takket v\u00e6re gs++!", "Un\u00eb vet\u00ebm eca{blocks} metra fal\u00eb gs++!", "\u591a\u4e8f\u4e86gs++\uff0c\u6211\u624d\u8d70\u4e86{blocks}\u7c73\uff01", "K\u00e4velin juuri{blocks} metri\u00e4 gs++n ansiosta!"};
        placeMessages = new String[]{"I just inserted{amount}{name} into the muliverse thanks to gs++!", "\u0644\u0642\u062f \u0623\u062f\u0631\u062c\u062a \u0644\u0644\u062a\u0648{amount}{name} \u0641\u064a muliverse \u0628\u0641\u0636\u0644 gs++!", "\u00a1Acabo de insertar{amount}{name} en el universo gracias a gs++!", "Je viens d'ins\u00e9rer{amount}{name} dans le mulivers gr\u00e2ce \u00e0 gs++!", "\u05d4\u05e8\u05d2\u05e2 \u05d4\u05db\u05e0\u05e1\u05ea\u05d9 \u05d0\u05ea{amount}{name} \u05dc\u05de\u05d5\u05dc\u05d9\u05d1\u05e8\u05e1 \u05d1\u05d6\u05db\u05d5\u05ea gs++!", "Ich habe gerade dank gs++{amount}{name} in das Multiversum eingef\u00fcgt! \n", "gs++\u306e\u304a\u304b\u3052\u3067\u3001{amount}{name}\u3092\u30de\u30eb\u30c1\u30d0\u30fc\u30b9\u306b\u633f\u5165\u3057\u307e\u3057\u305f\uff01", "Ik heb zojuist{amount}{name} in het muliversum ingevoegd dankzij gs++!", "\u039c\u03ce\u03bb\u03b9\u03c2 \u03c7\u03c1\u03b7\u03c3\u03b9\u03bc\u03bf\u03c0\u03bf\u03b9\u03ae\u03c3\u03b1{amount}{name} \u03c7\u03ac\u03c1\u03b7 \u03c4\u03bf gs++", "gs++ sayesinde birden fazla ki\u015fiye{amount}{name} ekledim!", "W\u0142a\u015bnie wstawi\u0142em{amount}{name} do wielu dzi\u0119ki gs++!", "\u042f \u0442\u043e\u043b\u044c\u043a\u043e \u0447\u0442\u043e \u0432\u0441\u0442\u0430\u0432\u0438\u043b{amount}{name} \u0432\u043e \u0432\u0441\u0435\u043b\u0435\u043d\u043d\u0443\u044e \u0431\u043b\u0430\u0433\u043e\u0434\u0430\u0440\u044f gs++!", "Jeg har lige indsat{amount}{name} i muliversen takket v\u00e6re gs++!", "\u591a\u4e8f\u4e86gs++\uff0c\u6211\u521a\u521a\u5c06{amount}{name}\u63d2\u5165\u4e86\u591a\u4eba\u6e38\u620f\uff01", "Un\u00eb vet\u00ebm futa{amount}{name} n\u00eb muliverse fal\u00eb gs++!"};
        jumpMessages = new String[]{"I just hovered in the air thanks to gs++!", "\u0644\u0642\u062f \u062d\u0648\u0645\u062a \u0644\u0644\u062a\u0648 \u0641\u064a \u0627\u0644\u0647\u0648\u0627\u0621 \u0628\u0641\u0636\u0644 gs++!", "\u00a1Acabo de volar en el aire gracias a gs++!", "Je viens de planer dans les airs gr\u00e2ce \u00e0 gs++!", "\u05e4\u05e9\u05d5\u05d8 \u05e8\u05d9\u05d7\u05e4\u05ea\u05d9 \u05d1\u05d0\u05d5\u05d5\u05d9\u05e8 \u05d1\u05d6\u05db\u05d5\u05ea gs++!", "Ich habe gerade dank gs++ in der Luft geschwebt!\n", "gs++\u306e\u304a\u304b\u3052\u3067\u5b99\u306b\u6d6e\u3044\u305f\u3060\u3051\u3067\u3059\uff01", "Dankzij gs++ zweefde ik gewoon in de lucht!", "\u039c\u03cc\u03bb\u03b9\u03c2 \u03b1\u03b9\u03c9\u03c1\u03ae\u03b8\u03b7\u03ba\u03b1 \u03c3\u03c4\u03bf\u03bd \u03b1\u03ad\u03c1\u03b1 \u03c7\u03ac\u03c1\u03b7\u03c2 \u03c4\u03bf gs++!", "gs++ sayesinde havada as\u0131l\u0131 kald\u0131m!", "Po prostu unosi\u0142em si\u0119 w powietrzu dzi\u0119ki gs++!", "\u042f \u043f\u0440\u043e\u0441\u0442\u043e \u0437\u0430\u0432\u0438\u0441 \u0432 \u0432\u043e\u0437\u0434\u0443\u0445\u0435 \u0431\u043b\u0430\u0433\u043e\u0434\u0430\u0440\u044f gs++!", "Un\u00eb thjesht u fiksova n\u00eb aj\u00ebr fal\u00eb gs++!", "\u591a\u4e8f\u4e86gs++\uff0c\u6211\u624d\u5f98\u5f8a\u5728\u7a7a\u4e2d\uff01", "Min\u00e4 vain leijuin ilmassa gs++n ansiosta!"};
        breakMessages = new String[]{"I just snapped{amount}{name} out of existance thanks to gs++!", "\u0644\u0642\u062f \u0642\u0637\u0639\u062a \u0644\u0644\u062a\u0648{amount}{name} \u0645\u0646 \u062e\u0627\u0631\u062c \u0628\u0641\u0636\u0644 gs++!", "\u00a1Acabo de sacar{amount}{name} de la existencia gracias a gs++!", "Je viens de casser{amount}{name} hors de l'existence gr\u00e2ce \u00e0 gs++!", "\u05e4\u05e9\u05d5\u05d8 \u05d7\u05d8\u05e4\u05ea\u05d9 \u05d0\u05ea{amount}{name} \u05de\u05d4\u05d4\u05ea\u05e7\u05d9\u05d9\u05dd \u05d1\u05d6\u05db\u05d5\u05ea gs++!", "Ich habe gerade{amount}{name} dank gs++ aus der Existenz gerissen!", "gs++\u306e\u304a\u304b\u3052\u3067\u3001{amount}{name}\u304c\u5b58\u5728\u3057\u306a\u304f\u306a\u308a\u307e\u3057\u305f\u3002", "Ik heb zojuist{amount}{name} uit het bestaan \u200b\u200bgehaald dankzij gs++!", "\u039c\u03ce\u03bb\u03b9\u03c2 \u03ad\u03c3\u03c0\u03b1\u03c3\u03b1 \u03c4\u03bf{amount}{name} \u03b1\u03c0\u03cc \u03c4\u03b7\u03bd \u03cd\u03c0\u03b1\u03c1\u03be\u03b7 \u03c7\u03ac\u03c1\u03b7 \u03c3\u03c4\u03bf gs++!", "gs++ sayesinde{amount}{name} varl\u0131\u011f\u0131n\u0131 yeni \u00e7\u0131kard\u0131m!", "W\u0142a\u015bnie wyskoczy\u0142em z gry dzi\u0119ki{amount}{name} dzi\u0119ki gs++!", "\u042f \u0442\u043e\u043b\u044c\u043a\u043e \u0447\u0442\u043e \u043e\u0442\u043a\u043b\u044e\u0447\u0438\u043b{amount}{name} \u0438\u0437 \u0441\u0443\u0449\u0435\u0441\u0442\u0432\u043e\u0432\u0430\u043d\u0438\u044f \u0431\u043b\u0430\u0433\u043e\u0434\u0430\u0440\u044f gs++!", "Jeg har lige sl\u00e5et{amount}{name} ud af eksistens takket v\u00e6re gs++!", "\u591a\u4e8f\u4e86gs++\uff0c\u6211\u624d\u5c06{amount}{name}\u6dd8\u6c70\u4e86\uff01", "Napsautin juuri{amount}{name} olemassaolosta gs++n ansiosta!"};
        eatMessages = new String[]{"I just ate{amount}{name} thanks to gs++!", "\u0644\u0642\u062f \u0623\u0643\u0644\u062a \u0644\u0644\u062a\u0648{amount}{name} \u0628\u0641\u0636\u0644 gs++!", "\u00a1Acabo de comer{amount}{name} gracias a gs++!", "Je viens de manger{amount}{name} gr\u00e2ce \u00e0 gs++!", "\u05e4\u05e9\u05d5\u05d8 \u05d0\u05db\u05dc\u05ea\u05d9{amount}{name} \u05d1\u05d6\u05db\u05d5\u05ea gs++!", "Ich habe gerade dank gs++{amount}{name} gegessen!", "gs++\u306e\u304a\u304b\u3052\u3067{amount}{name}\u3092\u98df\u3079\u307e\u3057\u305f\u3002", "Ik heb zojuist{amount}{name} gegeten dankzij gs++!", "\u039c\u03cc\u03bb\u03b9\u03c2 \u03ad\u03c6\u03b1\u03b3\u03b1{amount}{name} \u03c7\u03ac\u03c1\u03b7 \u03c3\u03c4\u03bf gs++!", "gs++ sayesinde sadece{amount}{name} yedim!", "W\u0142a\u015bnie zjad\u0142em{amount}{name} dzi\u0119ki gs++!", "Jeg spiste lige{amount}{name} takket v\u00e6re gs++!", "\u042f \u0442\u043e\u043b\u044c\u043a\u043e \u0447\u0442\u043e \u0441\u044a\u0435\u043b{amount}{name} \u0431\u043b\u0430\u0433\u043e\u0434\u0430\u0440\u044f gs++!", "Un\u00eb thjesht h\u00ebngra{amount}{name} fal\u00eb gs++!", "\u611f\u8c22gs++\uff0c\u6211\u521a\u5403\u4e86{amount}{name}\uff01", "S\u00f6in juuri{amount}{name} Gamessenin ansiosta!"};
    }
}

