/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.misc;

import com.gamesense.api.event.events.PacketEvent;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.Arrays;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.network.Packet;
import net.minecraft.network.login.server.SPacketEnableCompression;
import net.minecraft.network.login.server.SPacketEncryptionRequest;
import net.minecraft.network.login.server.SPacketLoginSuccess;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.client.CPacketClientStatus;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketCreativeInventoryAction;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.network.play.server.SPacketAdvancementInfo;
import net.minecraft.network.play.server.SPacketAnimation;
import net.minecraft.network.play.server.SPacketBlockAction;
import net.minecraft.network.play.server.SPacketBlockBreakAnim;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.network.play.server.SPacketCamera;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.network.play.server.SPacketCloseWindow;
import net.minecraft.network.play.server.SPacketCollectItem;
import net.minecraft.network.play.server.SPacketCombatEvent;
import net.minecraft.network.play.server.SPacketConfirmTransaction;
import net.minecraft.network.play.server.SPacketCooldown;
import net.minecraft.network.play.server.SPacketCustomPayload;
import net.minecraft.network.play.server.SPacketCustomSound;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.network.play.server.SPacketDisconnect;
import net.minecraft.network.play.server.SPacketDisplayObjective;
import net.minecraft.network.play.server.SPacketEffect;
import net.minecraft.network.play.server.SPacketEntity;
import net.minecraft.network.play.server.SPacketEntityAttach;
import net.minecraft.network.play.server.SPacketEntityEffect;
import net.minecraft.network.play.server.SPacketEntityEquipment;
import net.minecraft.network.play.server.SPacketEntityHeadLook;
import net.minecraft.network.play.server.SPacketEntityMetadata;
import net.minecraft.network.play.server.SPacketEntityProperties;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.network.play.server.SPacketEntityTeleport;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.network.play.server.SPacketHeldItemChange;
import net.minecraft.network.play.server.SPacketJoinGame;
import net.minecraft.network.play.server.SPacketKeepAlive;
import net.minecraft.network.play.server.SPacketMaps;
import net.minecraft.network.play.server.SPacketMoveVehicle;
import net.minecraft.network.play.server.SPacketMultiBlockChange;
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.network.play.server.SPacketParticles;
import net.minecraft.network.play.server.SPacketPlayerAbilities;
import net.minecraft.network.play.server.SPacketPlayerListHeaderFooter;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.network.play.server.SPacketRecipeBook;
import net.minecraft.network.play.server.SPacketRemoveEntityEffect;
import net.minecraft.network.play.server.SPacketRespawn;
import net.minecraft.network.play.server.SPacketScoreboardObjective;
import net.minecraft.network.play.server.SPacketSelectAdvancementsTab;
import net.minecraft.network.play.server.SPacketServerDifficulty;
import net.minecraft.network.play.server.SPacketSetExperience;
import net.minecraft.network.play.server.SPacketSetPassengers;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.network.play.server.SPacketSignEditorOpen;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketSpawnExperienceOrb;
import net.minecraft.network.play.server.SPacketSpawnGlobalEntity;
import net.minecraft.network.play.server.SPacketSpawnMob;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.network.play.server.SPacketSpawnPainting;
import net.minecraft.network.play.server.SPacketSpawnPlayer;
import net.minecraft.network.play.server.SPacketSpawnPosition;
import net.minecraft.network.play.server.SPacketTabComplete;
import net.minecraft.network.play.server.SPacketUnloadChunk;
import net.minecraft.network.play.server.SPacketUpdateHealth;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.network.play.server.SPacketUseBed;
import net.minecraft.network.status.server.SPacketPong;
import net.minecraft.network.status.server.SPacketServerInfo;
import net.minecraft.world.World;

@Module.Declaration(name="PacketLogger", category=Category.Misc)
public class PacketLogger
extends Module {
    BooleanSetting incoming = this.registerBoolean("Receive", true);
    BooleanSetting AdvancementInfo = this.registerBoolean("SPacketAdvancementInfo", false, () -> (Boolean)this.incoming.getValue());
    BooleanSetting SAnimation = this.registerBoolean("SPacketAnimation", false, () -> (Boolean)this.incoming.getValue());
    BooleanSetting SBlockAction = this.registerBoolean("SPacketBlockAction", false, () -> (Boolean)this.incoming.getValue());
    BooleanSetting SBlockBreakAnim = this.registerBoolean("SPacketBlockBreakAnim", false, () -> (Boolean)this.incoming.getValue());
    BooleanSetting SBlockChange = this.registerBoolean("SPacketBlockChange", false, () -> (Boolean)this.incoming.getValue());
    BooleanSetting SCamera = this.registerBoolean("SPacketCamera", false, () -> (Boolean)this.incoming.getValue());
    BooleanSetting SChat = this.registerBoolean("SPacketChat", false, () -> (Boolean)this.incoming.getValue());
    BooleanSetting SCooldown = this.registerBoolean("SPacketCooldown", false, () -> (Boolean)this.incoming.getValue());
    BooleanSetting SChunkData = this.registerBoolean("SPacketChunkData", false, () -> (Boolean)this.incoming.getValue());
    BooleanSetting SChangeGameState = this.registerBoolean("SPacketChangeGameState", false, () -> (Boolean)this.incoming.getValue());
    BooleanSetting SCloseWindow = this.registerBoolean("SPacketCloseWindow", false, () -> (Boolean)this.incoming.getValue());
    BooleanSetting SCollectItem = this.registerBoolean("SPacketCollectItem", false, () -> (Boolean)this.incoming.getValue());
    BooleanSetting SCombatEvent = this.registerBoolean("SPacketCombatEvent", false, () -> (Boolean)this.incoming.getValue());
    BooleanSetting SConfirmTransaction = this.registerBoolean("SPacketConfirmTransaction", false, () -> (Boolean)this.incoming.getValue());
    BooleanSetting SCustomPayload = this.registerBoolean("SPacketCustomPayload", false, () -> (Boolean)this.incoming.getValue());
    BooleanSetting SCustomSound = this.registerBoolean("SPacketCustomSound", false, () -> (Boolean)this.incoming.getValue());
    BooleanSetting SDestroyEntities = this.registerBoolean("SPacketDestroyEntities", false, () -> (Boolean)this.incoming.getValue());
    BooleanSetting SDisconnect = this.registerBoolean("SPacketDisconnect", false, () -> (Boolean)this.incoming.getValue());
    BooleanSetting SDisplayObjective = this.registerBoolean("SPacketDisplayObjective", false, () -> (Boolean)this.incoming.getValue());
    BooleanSetting SEffect = this.registerBoolean("SPacketEffect", false, () -> (Boolean)this.incoming.getValue());
    BooleanSetting SEntity = this.registerBoolean("SPacketEntity", false, () -> (Boolean)this.incoming.getValue());
    BooleanSetting SEntityAttach = this.registerBoolean("SPacketEntityAttach", false, () -> (Boolean)this.incoming.getValue());
    BooleanSetting SEntityEffect = this.registerBoolean("SPacketEntityEffect", false, () -> (Boolean)this.incoming.getValue());
    BooleanSetting SEntityEquipment = this.registerBoolean("SPacketEntityEquipment", false, () -> (Boolean)this.incoming.getValue());
    BooleanSetting SEntityHeadLook = this.registerBoolean("SPacketEntityHeadLook", false, () -> (Boolean)this.incoming.getValue());
    BooleanSetting SEntityMetadata = this.registerBoolean("SPacketEntityMetadata", false, () -> (Boolean)this.incoming.getValue());
    BooleanSetting SEntityProperties = this.registerBoolean("SPacketEntityProperties", false, () -> (Boolean)this.incoming.getValue());
    BooleanSetting SEntityStatus = this.registerBoolean("SPacketEntityStatus", false, () -> (Boolean)this.incoming.getValue());
    BooleanSetting SEntityTeleport = this.registerBoolean("SPacketEntityTeleport", false, () -> (Boolean)this.incoming.getValue());
    BooleanSetting SEntityVelocity = this.registerBoolean("SPacketEntityVelocity", false, () -> (Boolean)this.incoming.getValue());
    BooleanSetting SExplosion = this.registerBoolean("SPacketExplosion", false, () -> (Boolean)this.incoming.getValue());
    BooleanSetting SEnableCompression = this.registerBoolean("SPacketEnableCompression", false, () -> (Boolean)this.incoming.getValue());
    BooleanSetting SEncryptionRequest = this.registerBoolean("SPacketEncryptionRequest", false, () -> (Boolean)this.incoming.getValue());
    BooleanSetting SHeldItemChange = this.registerBoolean("SPacketHeldItemChange", false, () -> (Boolean)this.incoming.getValue());
    BooleanSetting SJoinGame = this.registerBoolean("SPacketJoinGame", false, () -> (Boolean)this.incoming.getValue());
    BooleanSetting SKeepAlive = this.registerBoolean("SPacketKeepAlive", false, () -> (Boolean)this.incoming.getValue());
    BooleanSetting SLoginSuccess = this.registerBoolean("SPacketLoginSuccess", false, () -> (Boolean)this.incoming.getValue());
    BooleanSetting SMaps = this.registerBoolean("SPacketMaps", false, () -> (Boolean)this.incoming.getValue());
    BooleanSetting SMoveVehicle = this.registerBoolean("SPacketMoveVehicle", false, () -> (Boolean)this.incoming.getValue());
    BooleanSetting SMultiBlockChange = this.registerBoolean("SPacketMultiBlockChange", false, () -> (Boolean)this.incoming.getValue());
    BooleanSetting SOpenWindow = this.registerBoolean("SPacketOpenWindow", false, () -> (Boolean)this.incoming.getValue());
    BooleanSetting SParticles = this.registerBoolean("SPacketParticles", false, () -> (Boolean)this.incoming.getValue());
    BooleanSetting SPlayerAbilities = this.registerBoolean("SPacketPlayerAbilities", false, () -> (Boolean)this.incoming.getValue());
    BooleanSetting SPlayerListHeaderFooter = this.registerBoolean("SPacketPlayerListHeaderFooter", false, () -> (Boolean)this.incoming.getValue());
    BooleanSetting SPlayerListItem = this.registerBoolean("SPacketPlayerListItem", false, () -> (Boolean)this.incoming.getValue());
    BooleanSetting SPlayerPosLook = this.registerBoolean("SPacketPlayerPosLook", false, () -> (Boolean)this.incoming.getValue());
    BooleanSetting SPong = this.registerBoolean("SPacketPong", false, () -> (Boolean)this.incoming.getValue());
    BooleanSetting SRecipeBook = this.registerBoolean("SPacketRecipeBook", false, () -> (Boolean)this.incoming.getValue());
    BooleanSetting SRespawn = this.registerBoolean("SPacketRespawn", false, () -> (Boolean)this.incoming.getValue());
    BooleanSetting SRemoveEntityEffect = this.registerBoolean("SPacketRemoveEntityEffect", false, () -> (Boolean)this.incoming.getValue());
    BooleanSetting SScoreboardObjective = this.registerBoolean("SPacketScoreboardObjective", false, () -> (Boolean)this.incoming.getValue());
    BooleanSetting SServerDifficulty = this.registerBoolean("SPacketServerDifficulty", false, () -> (Boolean)this.incoming.getValue());
    BooleanSetting SSelectAdvancementsTab = this.registerBoolean("SPacketSelectAdvancementsTab", false, () -> (Boolean)this.incoming.getValue());
    BooleanSetting SServerInfo = this.registerBoolean("SPacketServerInfo", false, () -> (Boolean)this.incoming.getValue());
    BooleanSetting SSetExperience = this.registerBoolean("SPacketSetExperience", false, () -> (Boolean)this.incoming.getValue());
    BooleanSetting SSetPassengers = this.registerBoolean("SPacketSetPassengers", false, () -> (Boolean)this.incoming.getValue());
    BooleanSetting SSetSlot = this.registerBoolean("SPacketSetSlot", false, () -> (Boolean)this.incoming.getValue());
    BooleanSetting SSignEditorOpen = this.registerBoolean("SPacketSignEditorOpen", false, () -> (Boolean)this.incoming.getValue());
    BooleanSetting SSoundEffect = this.registerBoolean("SPacketSoundEffect", false, () -> (Boolean)this.incoming.getValue());
    BooleanSetting SSpawnGlobalEntity = this.registerBoolean("SPacketSpawnGlobalEntity", false, () -> (Boolean)this.incoming.getValue());
    BooleanSetting SSpawnMob = this.registerBoolean("SPacketSpawnMob", false, () -> (Boolean)this.incoming.getValue());
    BooleanSetting SSpawnPlayer = this.registerBoolean("SPacketSpawnPlayer", false, () -> (Boolean)this.incoming.getValue());
    BooleanSetting SSpawnExperienceOrb = this.registerBoolean("SPacketSpawnExperienceOrb", false, () -> (Boolean)this.incoming.getValue());
    BooleanSetting SSpawnPainting = this.registerBoolean("SPacketSpawnPainting", false, () -> (Boolean)this.incoming.getValue());
    BooleanSetting SSpawnObject = this.registerBoolean("SPacketSpawnObject", false, () -> (Boolean)this.incoming.getValue());
    BooleanSetting SSpawnPosition = this.registerBoolean("SPacketSpawnPosition", false, () -> (Boolean)this.incoming.getValue());
    BooleanSetting STabComplete = this.registerBoolean("SPacketTabComplete", false, () -> (Boolean)this.incoming.getValue());
    BooleanSetting SUnloadChunk = this.registerBoolean("SPacketUnloadChunk", false, () -> (Boolean)this.incoming.getValue());
    BooleanSetting SUseBed = this.registerBoolean("SPacketUseBed", false, () -> (Boolean)this.incoming.getValue());
    BooleanSetting SUpdateHealth = this.registerBoolean("SPacketUpdateHealth", false, () -> (Boolean)this.incoming.getValue());
    BooleanSetting outgoing = this.registerBoolean("Outgoing", true);
    BooleanSetting CAnimation = this.registerBoolean("CPacketAnimation", false, () -> (Boolean)this.outgoing.getValue());
    BooleanSetting CChatMessage = this.registerBoolean("CPacketChatMessage", false, () -> (Boolean)this.outgoing.getValue());
    BooleanSetting CClickWindow = this.registerBoolean("CPacketClickWindow", false, () -> (Boolean)this.outgoing.getValue());
    BooleanSetting CConfirmTeleport = this.registerBoolean("CPacketConfirmTeleport", false, () -> (Boolean)this.outgoing.getValue());
    BooleanSetting CClientStatus = this.registerBoolean("CPacketClientStatus", false, () -> (Boolean)this.outgoing.getValue());
    BooleanSetting CCustomPayload = this.registerBoolean("CPacketCustomPayload", false, () -> (Boolean)this.outgoing.getValue());
    BooleanSetting CCreativeInventoryAction = this.registerBoolean("CPacketCreativeInventoryAction", false, () -> (Boolean)this.outgoing.getValue());
    BooleanSetting printChat = this.registerBoolean("Print Chat", false);
    BooleanSetting logFile = this.registerBoolean("Log File", false);
    BooleanSetting showTick = this.registerBoolean("Show Tick", false);
    BooleanSetting separator = this.registerBoolean("Separator", false);
    int tick;
    @EventHandler
    private final Listener<PacketEvent.Send> sendListener = new Listener<PacketEvent.Send>(event -> {
        if (!((Boolean)this.outgoing.getValue()).booleanValue()) {
            return;
        }
        Packet pack = event.getPacket();
        if (pack instanceof CPacketAnimation && ((Boolean)this.CAnimation.getValue()).booleanValue()) {
            CPacketAnimation s = (CPacketAnimation)pack;
            this.sendMessage("CPacketAnimation\n - Hand name: " + s.func_187018_a().name());
        } else if (pack instanceof CPacketChatMessage && ((Boolean)this.CChatMessage.getValue()).booleanValue()) {
            CPacketChatMessage s = (CPacketChatMessage)pack;
            this.sendMessage("CPacketChatMessage\n - Message: " + s.field_149440_a);
        } else if (pack instanceof CPacketClickWindow && ((Boolean)this.CClickWindow.getValue()).booleanValue()) {
            CPacketClickWindow s = (CPacketClickWindow)pack;
            this.sendMessage("CPacketClickWindow\n - Acton Number: " + s.func_149547_f() + "\n - Window ID: " + s.func_149548_c() + "\n - Item Name: " + s.func_149546_g().func_82833_r() + "\n - Click Type Name: " + s.func_186993_f().name());
        } else if (pack instanceof CPacketConfirmTeleport && ((Boolean)this.CConfirmTeleport.getValue()).booleanValue()) {
            CPacketConfirmTeleport s = (CPacketConfirmTeleport)pack;
            this.sendMessage("CPacketConfirmTeleport\n - Tp id: " + s.func_186987_a());
        } else if (pack instanceof CPacketClientStatus && ((Boolean)this.CClientStatus.getValue()).booleanValue()) {
            CPacketClientStatus s = (CPacketClientStatus)pack;
            this.sendMessage("CPacketClientStatus\n - Status Name: " + s.func_149435_c().name());
        } else if (pack instanceof CPacketCustomPayload && ((Boolean)this.CCustomPayload.getValue()).booleanValue()) {
            CPacketCustomPayload s = (CPacketCustomPayload)pack;
            this.sendMessage("CPacketCustomPayload\n - Channel: " + s.func_149559_c() + "\n - Data: " + s.func_180760_b().func_150789_c(10000));
        } else if (pack instanceof CPacketCreativeInventoryAction && ((Boolean)this.CCreativeInventoryAction.getValue()).booleanValue()) {
            CPacketCreativeInventoryAction s = (CPacketCreativeInventoryAction)pack;
            this.sendMessage("CPacketCreativeInventoryAction\n - Item name: " + s.func_149625_d().func_82833_r() + "\n - Slot Id: " + s.func_149627_c());
        }
    }, new Predicate[0]);
    @EventHandler
    private final Listener<PacketEvent.Receive> receiveListener = new Listener<PacketEvent.Receive>(event -> {
        if (!((Boolean)this.incoming.getValue()).booleanValue()) {
            return;
        }
        Packet pack = event.getPacket();
        if (pack instanceof SPacketAdvancementInfo && ((Boolean)this.AdvancementInfo.getValue()).booleanValue()) {
            SPacketAdvancementInfo s = (SPacketAdvancementInfo)pack;
            this.sendMessage("SPacketAdvancementInfo:\n -Is First Sync: " + s.func_192602_d());
        } else if (pack instanceof SPacketAnimation && ((Boolean)this.SAnimation.getValue()).booleanValue()) {
            SPacketAnimation s = (SPacketAnimation)pack;
            this.sendMessage("SPacketAnimation:\n - Animation Type: " + s.func_148977_d() + "\n - Entity Id: " + s.func_148978_c());
        } else if (pack instanceof SPacketCamera && ((Boolean)this.SCamera.getValue()).booleanValue()) {
            SPacketCamera s = (SPacketCamera)pack;
            try {
                this.sendMessage("SPacketCamera:\n - Entity name: " + s.func_179780_a((World)PacketLogger.mc.field_71441_e).func_70005_c_() + "\n - Entity Id: " + s.field_179781_a);
            }
            catch (NullPointerException e) {
                this.sendMessage("SPacketCamera:\n - Entity name: null\n - Entity Id: " + s.field_179781_a);
            }
        } else if (pack instanceof SPacketChat && ((Boolean)this.SChat.getValue()).booleanValue()) {
            SPacketChat s = (SPacketChat)pack;
            this.sendMessage("SPacketChat:\n - Chat Type: " + s.field_179842_b.name() + "\n - Formatted Text: " + s.field_148919_a.func_150254_d());
        } else if (pack instanceof SPacketBlockAction && ((Boolean)this.SBlockAction.getValue()).booleanValue()) {
            SPacketBlockAction s = (SPacketBlockAction)pack;
            this.sendMessage("SPacketBlockAction:\n - Block Type Name: " + s.func_148868_c().func_149732_F() + "\n - Block Type: " + s.func_148868_c() + "\n - Block Pos: " + s.func_179825_a() + "\n - Data1: " + s.func_148869_g() + "\n - Data2: " + s.func_148864_h());
        } else if (pack instanceof SPacketBlockBreakAnim && ((Boolean)this.SBlockBreakAnim.getValue()).booleanValue()) {
            SPacketBlockBreakAnim s = (SPacketBlockBreakAnim)pack;
            this.sendMessage("SPacketBlockBreakAnim:\n - Break Id: " + s.func_148845_c() + "\n - Block Pos: " + s.func_179821_b() + "\n - Progress: " + s.func_148846_g());
        } else if (pack instanceof SPacketBlockChange && ((Boolean)this.SBlockChange.getValue()).booleanValue()) {
            SPacketBlockChange s = (SPacketBlockChange)pack;
            this.sendMessage("SPacketBlockChange:\n - Block Pos: " + s.func_179827_b() + "\n - Block Name: " + s.field_148883_d.func_177230_c().func_149732_F() + "\n - Block State: " + s.func_180728_a());
        } else if (pack instanceof SPacketCooldown && ((Boolean)this.SCooldown.getValue()).booleanValue()) {
            SPacketCooldown s = (SPacketCooldown)pack;
            this.sendMessage("SPacketCooldown:\n - Item: " + s.func_186920_a() + "\n - Ticks: " + s.func_186922_b());
        } else if (pack instanceof SPacketChunkData && ((Boolean)this.SChunkData.getValue()).booleanValue()) {
            SPacketChunkData s = (SPacketChunkData)pack;
            this.sendMessage("SPacketChunkData:\n - Chunk Pos: " + s.func_149273_e() + " " + s.func_149271_f());
        } else if (pack instanceof SPacketChangeGameState && ((Boolean)this.SChangeGameState.getValue()).booleanValue()) {
            SPacketChangeGameState s = (SPacketChangeGameState)pack;
            this.sendMessage("SPacketChangeGameState:\n - Game State Value: " + s.func_149137_d() + "\n - Game State: " + s.func_149138_c());
        } else if (pack instanceof SPacketCloseWindow && ((Boolean)this.SCloseWindow.getValue()).booleanValue()) {
            this.sendMessage("SPacketCloseWindow");
        } else if (pack instanceof SPacketCollectItem && ((Boolean)this.SCollectItem.getValue()).booleanValue()) {
            SPacketCollectItem s = (SPacketCollectItem)pack;
            this.sendMessage("SPacketCollectItem:\n - Entity ID: " + s.func_149353_d() + "\n - Amount: " + s.func_191208_c() + "\n - Collected Item Id: " + s.func_149354_c());
        } else if (pack instanceof SPacketCombatEvent && ((Boolean)this.SCombatEvent.getValue()).booleanValue()) {
            SPacketCombatEvent s = (SPacketCombatEvent)pack;
            this.sendMessage("SPacketCombatEvent:\n - Entity ID: " + s.field_179775_c + "\n - Player Id: " + s.field_179774_b + "\n - Event Name: " + s.field_179776_a.name() + "\n - Duration: " + s.field_179772_d + "\n - Death Message: " + s.field_179773_e.func_150254_d());
        } else if (pack instanceof SPacketConfirmTransaction && ((Boolean)this.SConfirmTransaction.getValue()).booleanValue()) {
            SPacketConfirmTransaction s = (SPacketConfirmTransaction)pack;
            this.sendMessage("SPacketConfirmTransaction:\n - Action Number: " + s.func_148890_d() + "\n - Window Id: " + s.func_148889_c() + "\n - Was Accepted: " + s.func_148888_e());
        } else if (pack instanceof SPacketCustomPayload && ((Boolean)this.SCustomPayload.getValue()).booleanValue()) {
            SPacketCustomPayload s = (SPacketCustomPayload)pack;
            this.sendMessage("SPacketCustomPayload:\n - Channel Name: " + s.func_149169_c() + "\n - Buffer Data: " + s.func_180735_b().func_150789_c(1000));
        } else if (pack instanceof SPacketCustomSound && ((Boolean)this.SCustomSound.getValue()).booleanValue()) {
            SPacketCustomSound s = (SPacketCustomSound)pack;
            this.sendMessage("SPacketCustomSound:\n - Sound Name: " + s.func_186930_a() + "\n - Sound Category: " + s.func_186929_b().func_187948_a() + "\n - Sound Pos: " + s.func_186932_c() + " " + s.func_186926_d() + " " + s.func_186925_e() + "\n - Sound Pitch: " + s.func_186928_g() + "\n - Sound Volume: " + s.func_186927_f());
        } else if (pack instanceof SPacketDestroyEntities && ((Boolean)this.SDestroyEntities.getValue()).booleanValue()) {
            SPacketDestroyEntities s = (SPacketDestroyEntities)pack;
            this.sendMessage("SPacketDestroyEntities:\n");
            Arrays.stream(s.func_149098_c()).forEach(id -> this.sendMessage("Removed Id: " + id));
        } else if (pack instanceof SPacketDisconnect && ((Boolean)this.SDisconnect.getValue()).booleanValue()) {
            SPacketDisconnect s = (SPacketDisconnect)pack;
            this.sendMessage("SPacketDisconnect:\n - Disconnect Reason: " + s.func_149165_c().func_150254_d());
        } else if (pack instanceof SPacketDisplayObjective && ((Boolean)this.SDisplayObjective.getValue()).booleanValue()) {
            SPacketDisplayObjective s = (SPacketDisplayObjective)pack;
            this.sendMessage("SPacketDisplayObjective:\n - Objective Name: " + s.func_149370_d() + "\n - Objective Pos: " + s.func_149371_c());
        } else if (pack instanceof SPacketEffect && ((Boolean)this.SEffect.getValue()).booleanValue()) {
            SPacketEffect s = (SPacketEffect)pack;
            this.sendMessage("SPacketEffect:\n - Sound Data: " + s.func_149241_e() + "\n - Sound Pos: " + s.func_179746_d() + "\n - Sound Type: " + s.func_149242_d() + "\n - Is Sound Server Wide: " + s.func_149244_c());
        } else if (pack instanceof SPacketEntity && ((Boolean)this.SEntity.getValue()).booleanValue()) {
            SPacketEntity s = (SPacketEntity)pack;
            this.sendMessage("SPacketEntity:\n - Entity Name: " + s.func_149065_a((World)PacketLogger.mc.field_71441_e).func_70005_c_() + "\n - Entity Id: " + s.func_149065_a((World)PacketLogger.mc.field_71441_e).field_145783_c + "\n - Entity Pitch: " + s.func_149063_g() + "\n - Is Entity OnGround: " + s.func_179742_g() + "\n - Entity Yaw: " + s.func_149066_f() + "\n - Entity Pos: " + s.func_186952_a() + " " + s.func_186953_b() + " " + s.func_186951_c());
        } else if (pack instanceof SPacketEntityAttach && ((Boolean)this.SEntityAttach.getValue()).booleanValue()) {
            SPacketEntityAttach s = (SPacketEntityAttach)pack;
            this.sendMessage("SPacketEntityAttach:\n - Entity Id: " + s.func_149403_d() + "\n - Entity Vehicle Id: " + s.func_149402_e());
        } else if (pack instanceof SPacketEntityEffect && ((Boolean)this.SEntityEffect.getValue()).booleanValue()) {
            SPacketEntityEffect s = (SPacketEntityEffect)pack;
            this.sendMessage("SPacketEntityEffect:\n - Entity Id: " + s.func_149426_d() + "\n - Effect Amplifier: " + s.func_149428_f() + "\n - Effect ID: " + s.func_149427_e() + "\n - Effect Duration: " + s.func_180755_e() + "\n - Is Effect Ambient: " + s.func_186984_g());
        } else if (pack instanceof SPacketEntityEquipment && ((Boolean)this.SEntityEquipment.getValue()).booleanValue()) {
            SPacketEntityEquipment s = (SPacketEntityEquipment)pack;
            this.sendMessage("SPacketEntityEquipment:\n - Entity Id: " + s.func_149389_d() + "\n - Equipment Slot Name: " + s.func_186969_c().func_188450_d() + "\n - Item Name: " + s.func_149390_c().func_82833_r());
        } else if (pack instanceof SPacketEntityHeadLook && ((Boolean)this.SEntityHeadLook.getValue()).booleanValue()) {
            SPacketEntityHeadLook s = (SPacketEntityHeadLook)pack;
            this.sendMessage("SPacketEntityHeadLook:\n - Entity Id: " + s.func_149381_a((World)PacketLogger.mc.field_71441_e).field_145783_c + "\n - Entity Name: " + s.func_149381_a((World)PacketLogger.mc.field_71441_e).func_70005_c_() + "\n - Yaw: " + s.func_149380_c());
        } else if (pack instanceof SPacketEntityMetadata && ((Boolean)this.SEntityMetadata.getValue()).booleanValue()) {
            SPacketEntityMetadata s = (SPacketEntityMetadata)pack;
            this.sendMessage("SPacketEntityMetadata:\n - Entity Id: " + s.func_149375_d());
        } else if (pack instanceof SPacketEntityProperties && ((Boolean)this.SEntityProperties.getValue()).booleanValue()) {
            SPacketEntityProperties s = (SPacketEntityProperties)pack;
            this.sendMessage("SPacketEntityProperties:\n - Entity Id: " + s.func_149442_c());
        } else if (pack instanceof SPacketEntityStatus && ((Boolean)this.SEntityStatus.getValue()).booleanValue()) {
            SPacketEntityStatus s = (SPacketEntityStatus)pack;
            this.sendMessage("SPacketEntityStatus:\n - Entity Id: " + s.func_149161_a((World)PacketLogger.mc.field_71441_e).func_145782_y() + "\n - Entity Name: " + s.func_149161_a((World)PacketLogger.mc.field_71441_e).func_70005_c_() + "\n - Entity OP code: " + s.func_149160_c());
        } else if (pack instanceof SPacketEntityTeleport && ((Boolean)this.SEntityTeleport.getValue()).booleanValue()) {
            SPacketEntityTeleport s = (SPacketEntityTeleport)pack;
            this.sendMessage("SPacketEntityTeleport:\n - Entity Id: " + s.func_149451_c() + "\n - Entity Pos: " + s.func_186982_b() + " " + s.func_186983_c() + " " + s.func_186981_d() + "\n - Entity Yaw: " + s.func_149450_g() + "\n - Entity Pitch: " + s.func_149447_h() + "\n - Is Entity On Ground: " + s.func_179697_g());
        } else if (pack instanceof SPacketEntityVelocity && ((Boolean)this.SEntityVelocity.getValue()).booleanValue()) {
            SPacketEntityVelocity s = (SPacketEntityVelocity)pack;
            this.sendMessage("SPacketEntityVelocity:\n - Entity Id: " + s.func_149412_c() + "\n - MotionX: " + s.field_149415_b + "\n - MotionY: " + s.field_149416_c + "\n - MotionZ: " + s.field_149414_d);
        } else if (pack instanceof SPacketExplosion && ((Boolean)this.SExplosion.getValue()).booleanValue()) {
            SPacketExplosion s = (SPacketExplosion)pack;
            this.sendMessage("SPacketExplosion:\n - Explosion Pos: " + s.field_149158_a + " " + s.func_149143_g() + " " + s.func_149145_h() + "\n - MotionX: " + s.field_149152_f + "\n - MotionY: " + s.field_149153_g + "\n - MotionZ: " + s.field_149159_h + "\n - Strength: " + s.func_149146_i());
        } else if (pack instanceof SPacketEnableCompression && ((Boolean)this.SEnableCompression.getValue()).booleanValue()) {
            SPacketEnableCompression s = (SPacketEnableCompression)pack;
            this.sendMessage("SPacketEnableCompression:\n - Compression Threshold: " + s.func_179731_a());
        } else if (pack instanceof SPacketEncryptionRequest && ((Boolean)this.SEncryptionRequest.getValue()).booleanValue()) {
            SPacketEncryptionRequest s = (SPacketEncryptionRequest)pack;
            this.sendMessage("SPacketEncryptionRequest:\n - Server Id: " + s.func_149609_c() + "\n - Public key: " + s.func_149608_d());
        } else if (pack instanceof SPacketHeldItemChange && ((Boolean)this.SHeldItemChange.getValue()).booleanValue()) {
            SPacketHeldItemChange s = (SPacketHeldItemChange)pack;
            this.sendMessage("SPacketEncryptionRequest:\n - Held Item Hotbar Index: " + s.func_149385_c());
        } else if (pack instanceof SPacketJoinGame && ((Boolean)this.SJoinGame.getValue()).booleanValue()) {
            SPacketJoinGame s = (SPacketJoinGame)pack;
            this.sendMessage("SPacketJoinGame:\n - Player ID: " + s.func_149197_c() + "\n - Difficulty: " + s.func_149192_g().name() + "\n - Dimension: " + s.func_149194_f() + "\n - Game Type: " + s.func_149198_e().func_77149_b() + "\n - World Type: " + s.func_149196_i().func_77127_a() + "\n - Max Players: " + s.func_149193_h() + "\n - Is Hardcore Mode: " + s.func_149195_d());
        } else if (pack instanceof SPacketKeepAlive && ((Boolean)this.SKeepAlive.getValue()).booleanValue()) {
            SPacketKeepAlive s = (SPacketKeepAlive)pack;
            this.sendMessage("SPacketKeepAlive:\n - ID: " + s.func_149134_c());
        } else if (pack instanceof SPacketLoginSuccess && ((Boolean)this.SLoginSuccess.getValue()).booleanValue()) {
            SPacketLoginSuccess s = (SPacketLoginSuccess)pack;
            this.sendMessage("SPacketLoginSuccess:\n - Name: " + s.func_179730_a().getName());
        } else if (pack instanceof SPacketMaps && ((Boolean)this.SMaps.getValue()).booleanValue()) {
            SPacketMaps s = (SPacketMaps)pack;
            this.sendMessage("SPacketMaps:\n - Map ID: " + s.func_149188_c());
        } else if (pack instanceof SPacketMoveVehicle && ((Boolean)this.SMoveVehicle.getValue()).booleanValue()) {
            SPacketMoveVehicle s = (SPacketMoveVehicle)pack;
            this.sendMessage("SPacketMoveVehicle:\n - Pitch: " + s.func_186958_e() + "\n - Yaw: " + s.func_186959_d() + "\n - Pos: " + s.func_186957_a() + " " + s.func_186955_b() + " " + s.func_186956_c());
        } else if (pack instanceof SPacketMultiBlockChange && ((Boolean)this.SMultiBlockChange.getValue()).booleanValue()) {
            SPacketMultiBlockChange s = (SPacketMultiBlockChange)pack;
            this.sendMessage("SPacketMultiBlockChange \n " + Arrays.toString(s.func_179844_a()));
        } else if (pack instanceof SPacketOpenWindow && ((Boolean)this.SOpenWindow.getValue()).booleanValue()) {
            SPacketOpenWindow s = (SPacketOpenWindow)pack;
            this.sendMessage("SPacketOpenWindow:\n - Gui ID: " + s.func_148902_e() + "\n - Entity ID: " + s.func_148897_h() + "\n - Window ID: " + s.func_148901_c() + "\n - Window Title: " + s.func_179840_c() + "\n - Slot Count: " + s.func_148898_f());
        } else if (pack instanceof SPacketParticles && ((Boolean)this.SParticles.getValue()).booleanValue()) {
            SPacketParticles s = (SPacketParticles)pack;
            this.sendMessage("SPacketParticles:\n - Particle Count: " + s.func_149222_k() + "\n - Particle Speed: " + s.func_149227_j() + "\n - Particle Name: " + s.func_179749_a().func_179346_b() + "\n - Pos: " + s.func_149220_d() + " " + s.func_149226_e() + " " + s.func_149225_f());
        } else if (pack instanceof SPacketPlayerAbilities && ((Boolean)this.SPlayerAbilities.getValue()).booleanValue()) {
            SPacketPlayerAbilities s = (SPacketPlayerAbilities)pack;
            this.sendMessage("SPacketPlayerAbilities:\n - Walk Speed: " + s.func_149107_h() + "\n - Fly Speed: " + s.func_149101_g() + "\n - Is Allow Flying: " + s.func_149105_e() + "\n - Is Creative Mode: " + s.func_149103_f() + "\n - Is Flying: " + s.func_149106_d() + "\n - Is Flying: " + s.func_149112_c());
        } else if (pack instanceof SPacketPlayerListHeaderFooter && ((Boolean)this.SPlayerListHeaderFooter.getValue()).booleanValue()) {
            SPacketPlayerListHeaderFooter s = (SPacketPlayerListHeaderFooter)pack;
            this.sendMessage("SPacketPlayerListHeaderFooter:\n - Footer: " + s.func_179701_b().func_150254_d() + "\n - Header: " + s.func_179700_a());
        } else if (pack instanceof SPacketPlayerListItem && ((Boolean)this.SPlayerListItem.getValue()).booleanValue()) {
            SPacketPlayerListItem s = (SPacketPlayerListItem)pack;
            this.sendMessage("SPacketPlayerListItem:\n - Action Name: " + s.func_179768_b().name());
        } else if (pack instanceof SPacketPlayerPosLook && ((Boolean)this.SPlayerPosLook.getValue()).booleanValue()) {
            SPacketPlayerPosLook s = (SPacketPlayerPosLook)pack;
            this.sendMessage("SPacketPlayerPosLook:\n - Pitch: " + s.func_148930_g() + "\n - Yaw: " + s.func_148931_f() + "\n - Pos: " + s.func_148932_c() + " " + s.func_148928_d() + " " + s.func_148933_e() + "\n - Teleport ID: " + s.func_186965_f());
        } else if (pack instanceof SPacketPong && ((Boolean)this.SPong.getValue()).booleanValue()) {
            this.sendMessage("SPacketPong");
        } else if (pack instanceof SPacketRecipeBook && ((Boolean)this.SRecipeBook.getValue()).booleanValue()) {
            SPacketRecipeBook s = (SPacketRecipeBook)pack;
            this.sendMessage("SPacketRecipeBook \n" + s.func_193644_b().toString() + "\n" + s.func_192595_a().toString());
        } else if (pack instanceof SPacketRespawn && ((Boolean)this.SRespawn.getValue()).booleanValue()) {
            SPacketRespawn s = (SPacketRespawn)pack;
            this.sendMessage("SPacketRecipeBook: \n - Dimension ID " + s.func_149082_c() + "\n - WorldType Name " + s.func_149080_f().func_77127_a() + "\n - Difficulty " + s.func_149081_d().name() + "\n - GameType name " + s.func_149083_e().name());
        } else if (pack instanceof SPacketRemoveEntityEffect && ((Boolean)this.SRemoveEntityEffect.getValue()).booleanValue()) {
            SPacketRemoveEntityEffect s = (SPacketRemoveEntityEffect)pack;
            try {
                this.sendMessage("SPacketRemoveEntityEffect: \n - Entity Name " + s.func_186967_a((World)PacketLogger.mc.field_71441_e).func_70005_c_() + "\n - Potion Name " + s.func_186968_a().func_76393_a() + "\n - Entity ID " + s.func_186967_a((World)PacketLogger.mc.field_71441_e).func_145782_y());
            }
            catch (NullPointerException e) {
                this.sendMessage("SPacketRemoveEntityEffect: \n - Entity Name null\n - Potion Name null\n - Entity ID null");
            }
        } else if (pack instanceof SPacketScoreboardObjective && ((Boolean)this.SScoreboardObjective.getValue()).booleanValue()) {
            SPacketScoreboardObjective s = (SPacketScoreboardObjective)pack;
            this.sendMessage("SPacketScoreboardObjective: \n - Objective Name " + s.func_149339_c() + "\n - Acton " + s.func_149338_e() + "\n - Render Type Name" + s.func_179817_d().name());
        } else if (pack instanceof SPacketServerDifficulty && ((Boolean)this.SServerDifficulty.getValue()).booleanValue()) {
            SPacketServerDifficulty s = (SPacketServerDifficulty)pack;
            this.sendMessage("SPacketServerDifficulty: \n - Difficulty Name " + s.func_179831_b().name());
        } else if (pack instanceof SPacketSelectAdvancementsTab && ((Boolean)this.SSelectAdvancementsTab.getValue()).booleanValue()) {
            SPacketSelectAdvancementsTab s = (SPacketSelectAdvancementsTab)pack;
            try {
                this.sendMessage("SPacketSelectAdvancementsTab \n" + s.func_194154_a().toString());
            }
            catch (NullPointerException e) {
                this.sendMessage("SPacketSelectAdvancementsTab null");
            }
        } else if (pack instanceof SPacketServerInfo && ((Boolean)this.SServerInfo.getValue()).booleanValue()) {
            SPacketServerInfo s = (SPacketServerInfo)pack;
            this.sendMessage("SPacketServerInfo: \n - Server Info " + s.func_149294_c().getJson());
        } else if (pack instanceof SPacketSetExperience && ((Boolean)this.SSetExperience.getValue()).booleanValue()) {
            SPacketSetExperience s = (SPacketSetExperience)pack;
            this.sendMessage("SPacketSetExperience: \n - Experience Bar " + s.func_149397_c() + "\n - Total Experience " + s.func_149396_d() + "\n - Level " + s.func_149395_e());
        } else if (pack instanceof SPacketSetPassengers && ((Boolean)this.SSetPassengers.getValue()).booleanValue()) {
            SPacketSetPassengers s = (SPacketSetPassengers)pack;
            this.sendMessage("SPacketSetPassengers: \n - Entity ID " + s.func_186972_b() + "\n - Passengers ID " + Arrays.toString(s.func_186971_a()));
        } else if (pack instanceof SPacketSetSlot && ((Boolean)this.SSetSlot.getValue()).booleanValue()) {
            SPacketSetSlot s = (SPacketSetSlot)pack;
            this.sendMessage("SPacketSetSlot: \n - Window ID " + s.func_149175_c() + "\n - Slot " + s.func_149173_d() + "\n - Item Name " + s.func_149174_e().func_82833_r());
        } else if (pack instanceof SPacketSignEditorOpen && ((Boolean)this.SSignEditorOpen.getValue()).booleanValue()) {
            SPacketSignEditorOpen s = (SPacketSignEditorOpen)pack;
            this.sendMessage("SPacketSignEditorOpen: \n - Sign Pos " + s.func_179777_a());
        } else if (pack instanceof SPacketSoundEffect && ((Boolean)this.SSoundEffect.getValue()).booleanValue()) {
            SPacketSoundEffect s = (SPacketSoundEffect)pack;
            this.sendMessage("SPacketSoundEffect: \n - Sound Name: " + s.func_186978_a().func_187503_a() + "\n - Sound Category: " + s.func_186977_b().func_187948_a() + "\n - Sound Pos: " + s.func_149207_d() + " " + s.func_149211_e() + " " + s.func_149210_f() + "\n - Sound Pitch: " + s.func_149209_h() + "\n - Sound Volume: " + s.func_149208_g());
        } else if (pack instanceof SPacketSpawnGlobalEntity && ((Boolean)this.SSpawnGlobalEntity.getValue()).booleanValue()) {
            SPacketSpawnGlobalEntity s = (SPacketSpawnGlobalEntity)pack;
            this.sendMessage("SPacketSpawnGlobalEntity: \n - Entity ID: " + s.func_149052_c() + "\n - Pos: " + s.func_186888_b() + " " + s.func_186889_c() + " " + s.func_186887_d() + "\n - Type: " + s.func_149053_g());
        } else if (pack instanceof SPacketSpawnMob && ((Boolean)this.SSpawnMob.getValue()).booleanValue()) {
            SPacketSpawnMob s = (SPacketSpawnMob)pack;
            this.sendMessage("SPacketSpawnMob: \n - Entity ID: " + s.func_149024_d() + "\n - Pos: " + s.func_186891_e() + " " + s.func_186892_f() + " " + s.func_186893_g() + "\n - UUID: " + s.func_186890_c() + "\n - Yaw " + s.func_149028_l() + "\n - Pitch " + s.func_149030_m() + "\n - Type: " + s.func_149025_e());
        } else if (pack instanceof SPacketSpawnPlayer && ((Boolean)this.SSpawnPlayer.getValue()).booleanValue()) {
            SPacketSpawnPlayer s = (SPacketSpawnPlayer)pack;
            this.sendMessage("SPacketSpawnPlayer: \n - Entity ID: " + s.func_148943_d() + "\n - Pos: " + s.func_186898_d() + " " + s.func_186897_e() + " " + s.func_186899_f() + "\n - UUID: " + s.func_179819_c() + "\n - Yaw " + s.func_148941_i() + "\n - Pitch " + s.func_148945_j());
        } else if (pack instanceof SPacketSpawnExperienceOrb && ((Boolean)this.SSpawnExperienceOrb.getValue()).booleanValue()) {
            SPacketSpawnExperienceOrb s = (SPacketSpawnExperienceOrb)pack;
            this.sendMessage("SPacketSpawnExperienceOrb: \n - Entity ID: " + s.func_148985_c() + "\n - Pos: " + s.func_186885_b() + " " + s.func_186886_c() + " " + s.func_186884_d() + "\n - XP value: " + s.func_148986_g());
        } else if (pack instanceof SPacketSpawnPainting && ((Boolean)this.SSpawnPainting.getValue()).booleanValue()) {
            SPacketSpawnPainting s = (SPacketSpawnPainting)pack;
            this.sendMessage("SPacketSpawnPainting: \n - Entity ID: " + s.func_148965_c() + "\n - Title: " + s.func_148961_h() + "\n - Pos: " + s.func_179837_b() + "\n - UUID: " + s.func_186895_b() + "\n - Facing: " + s.func_179836_c().func_176610_l());
        } else if (pack instanceof SPacketSpawnObject && ((Boolean)this.SSpawnObject.getValue()).booleanValue()) {
            SPacketSpawnObject s = (SPacketSpawnObject)pack;
            this.sendMessage("SPacketSpawnObject: \n - Entity ID: " + s.func_149001_c() + "\n - Pos: " + s.func_186880_c() + " " + s.func_186882_d() + " " + s.func_186881_e() + "\n - Speed Pos: " + s.func_149010_g() + " " + s.func_149004_h() + " " + s.func_148999_i() + "\n - UUID: " + s.func_186879_b() + "\n - Data: " + s.func_149009_m() + "\n - Type: " + s.func_148993_l() + "\n - Pitch: " + s.func_149008_j() + "\n - Yaw: " + s.func_149006_k());
        } else if (pack instanceof SPacketSpawnPosition && ((Boolean)this.SSpawnPosition.getValue()).booleanValue()) {
            SPacketSpawnPosition s = (SPacketSpawnPosition)pack;
            this.sendMessage("SPacketSpawnPosition: \n - Pos: " + s.func_179800_a());
        } else if (pack instanceof SPacketTabComplete && ((Boolean)this.STabComplete.getValue()).booleanValue()) {
            SPacketTabComplete s = (SPacketTabComplete)pack;
            this.sendMessage("SPacketTabComplete\n" + Arrays.toString(s.func_149630_c()));
        } else if (pack instanceof SPacketUnloadChunk && ((Boolean)this.SUnloadChunk.getValue()).booleanValue()) {
            SPacketUnloadChunk s = (SPacketUnloadChunk)pack;
            this.sendMessage("SPacketUnloadChunk\n - Chunk Pos: " + s.func_186940_a() + " " + s.func_186941_b());
        } else if (pack instanceof SPacketUseBed && ((Boolean)this.SUseBed.getValue()).booleanValue()) {
            SPacketUseBed s = (SPacketUseBed)pack;
            this.sendMessage("SPacketUseBed\n - Pos: " + s.func_179798_a() + "\n - Player name: " + s.func_149091_a((World)PacketLogger.mc.field_71441_e).func_70005_c_());
        } else if (pack instanceof SPacketUpdateHealth && ((Boolean)this.SUpdateHealth.getValue()).booleanValue()) {
            SPacketUpdateHealth s = (SPacketUpdateHealth)pack;
            this.sendMessage("SPacketUpdateHealth\n - Health: " + s.func_149332_c() + "\n - Food: " + s.func_149330_d() + "\n - Saturation: " + s.func_149331_e());
        } else if (pack instanceof SPacketUpdateTileEntity && ((Boolean)this.SUpdateHealth.getValue()).booleanValue()) {
            SPacketUpdateTileEntity s = (SPacketUpdateTileEntity)pack;
            this.sendMessage("SPacketUpdateTileEntity\n - Pos: " + s.func_179823_a() + "\n - Type: " + s.func_148853_f() + "\n - NBT tag: " + s.func_148857_g());
        }
    }, new Predicate[0]);
    StringBuilder file = new StringBuilder();

    @Override
    protected void onEnable() {
        this.tick = 0;
        this.file = new StringBuilder();
    }

    @Override
    public void onUpdate() {
        ++this.tick;
    }

    @Override
    protected void onDisable() {
        if (((Boolean)this.logFile.getValue()).booleanValue()) {
            try {
                if (!Files.exists(Paths.get("gs++", new String[0]), new LinkOption[0])) {
                    Files.createDirectories(Paths.get("gs++", new String[0]), new FileAttribute[0]);
                }
                if (!Files.exists(Paths.get("gs++/logs", new String[0]), new LinkOption[0])) {
                    Files.createDirectories(Paths.get("gs++/logs", new String[0]), new FileAttribute[0]);
                }
                OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter((OutputStream)new FileOutputStream("gs++/logs/" + System.currentTimeMillis() + ".txt"), StandardCharsets.UTF_8);
                fileOutputStreamWriter.write(this.file.toString());
                fileOutputStreamWriter.close();
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
    }

    void sendMessage(String message) {
        StringBuilder e = new StringBuilder();
        if (((Boolean)this.showTick.getValue()).booleanValue()) {
            e.append("\nTick: ").append(this.tick);
        }
        e.append("\n").append(message);
        if (((Boolean)this.separator.getValue()).booleanValue()) {
            e.append("\n----------");
        }
        if (((Boolean)this.logFile.getValue()).booleanValue()) {
            this.file.append((CharSequence)e);
        }
        if (((Boolean)this.printChat.getValue()).booleanValue()) {
            MessageBus.sendClientRawMessage(e.toString());
        }
    }
}

