package nl.enjarai.doabarrelroll.net;

import net.neoforged.neoforge.network.PacketDistributor;
import nl.enjarai.doabarrelroll.api.RollEntity;
import nl.enjarai.doabarrelroll.api.event.ClientEvents;
import nl.enjarai.doabarrelroll.config.ModConfigServer;
import nl.enjarai.doabarrelroll.net.packet.ConfigResponseC2SPacket;
import nl.enjarai.doabarrelroll.net.packet.ConfigUpdateC2SPacket;
import nl.enjarai.doabarrelroll.net.packet.RollSyncC2SPacket;

public class ClientNetworking {
    public static final HandshakeClient<ConfigResponseC2SPacket> HANDSHAKE_CLIENT = new HandshakeClient<>(
            ConfigResponseC2SPacket::new,
            ClientEvents::updateServerConfig
    );
    public static final ServerConfigUpdateClient<ConfigUpdateC2SPacket> CONFIG_UPDATE_CLIENT = new ServerConfigUpdateClient<>(
            ConfigUpdateC2SPacket::new
    );

    public static void sendRollUpdate(RollEntity entity) {
        if (HANDSHAKE_CLIENT.hasConnected()) {
            boolean rolling = entity.doABarrelRoll$isRolling();
            float roll = entity.doABarrelRoll$getRoll();

            PacketDistributor.sendToServer(new RollSyncC2SPacket(rolling, roll));
        }
    }

    public static void sendConfigUpdatePacket(ModConfigServer config) {
        PacketDistributor.sendToServer(CONFIG_UPDATE_CLIENT.prepUpdatePacket(config));
    }
}
