package nl.enjarai.doabarrelroll.net;

import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.network.chat.Component;

public interface SyncableConfig<T, L> {
    Integer getSyncTimeout();

    Component getSyncTimeoutMessage();

    L getLimited(ServerGamePacketListenerImpl handler);
}
