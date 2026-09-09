package nl.enjarai.doabarrelroll.net;

//? if <1.21.11 {
import net.neoforged.neoforge.network.PacketDistributor;
//?} else
/*import net.neoforged.neoforge.client.network.ClientPacketDistributor;*/
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

            //? if <1.21.11 {
            PacketDistributor.sendToServer(new RollSyncC2SPacket(rolling, roll));
            //?} else
            /*ClientPacketDistributor.sendToServer(new RollSyncC2SPacket(rolling, roll));*/
        }
    }

    public static void sendConfigUpdatePacket(ModConfigServer config) {
        //? if <1.21.11 {
        PacketDistributor.sendToServer(CONFIG_UPDATE_CLIENT.prepUpdatePacket(config));
        //?} else
        /*ClientPacketDistributor.sendToServer(CONFIG_UPDATE_CLIENT.prepUpdatePacket(config));*/
    }
}
