package com.github.tartaricacid.touhoulittlemaid.network.message;

import com.github.tartaricacid.touhoulittlemaid.client.gui.entity.ChairModelGui;
import com.github.tartaricacid.touhoulittlemaid.entity.item.EntityChair;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class OpenChairGuiMessage {
    private final int id;

    public OpenChairGuiMessage(int id) {
        this.id = id;
    }

    public static void encode(OpenChairGuiMessage message, PacketBuffer buf) {
        buf.writeInt(message.id);
    }

    public static OpenChairGuiMessage decode(PacketBuffer buf) {
        return new OpenChairGuiMessage(buf.readInt());
    }

    public static void handle(OpenChairGuiMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        if (context.getDirection().getReceptionSide().isClient()) {
            context.enqueueWork(() -> {
                Minecraft mc = Minecraft.getInstance();
                if (mc.level == null) {
                    return;
                }
                Entity e = mc.level.getEntity(message.id);
                if (mc.player != null && mc.player.isAlive() && e instanceof EntityChair && e.isAlive()) {
                    mc.setScreen(new ChairModelGui((EntityChair) e));
                }
            });
        }
        context.setPacketHandled(true);
    }
}