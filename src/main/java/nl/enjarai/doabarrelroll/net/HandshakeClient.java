package nl.enjarai.doabarrelroll.net;

import nl.enjarai.doabarrelroll.DoABarrelRoll;
import nl.enjarai.doabarrelroll.config.LimitedModConfigServer;
import nl.enjarai.doabarrelroll.config.ModConfigServer;
import nl.enjarai.doabarrelroll.net.packet.ConfigResponseC2SPacket;
import nl.enjarai.doabarrelroll.net.packet.ConfigSyncS2CPacket;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class HandshakeClient<P extends ConfigResponseC2SPacket> {
    private final BiFunction<Integer, Boolean, P> responseConstructor;
    private final Consumer<LimitedModConfigServer> updateCallback;
    private LimitedModConfigServer serverConfig = null;
    private ModConfigServer fullServerConfig = null;
    // Null until the server says, which a server from before the setting never does
    private Boolean operatorBypass = null;
    private boolean hasConnected = false;

    public HandshakeClient(BiFunction<Integer, Boolean, P> responseConstructor, Consumer<LimitedModConfigServer> updateCallback) {
        this.responseConstructor = responseConstructor;
        this.updateCallback = updateCallback;
    }

    /**
     * Returns the server config if the client has received one for this server,
     * returns an empty optional in any other case.
     */
    public Optional<LimitedModConfigServer> getConfig() {
        return Optional.ofNullable(serverConfig);
    }

    public Optional<ModConfigServer> getFullConfig() {
        return Optional.ofNullable(fullServerConfig);
    }

    /**
     * The server's operatorBypass setting, present only for players allowed to edit the server config
     * on a server new enough to have it.
     */
    public Optional<Boolean> getOperatorBypass() {
        return Optional.ofNullable(operatorBypass);
    }

    public void setOperatorBypass(boolean operatorBypass) {
        this.operatorBypass = operatorBypass;
    }

//    public void setConfig(@Nullable L config) {
//        serverConfig = config;
//        updateCallback.accept(serverConfig);
//        hasConnected = serverConfig != null;
//    }

    public boolean hasConnected() {
        return hasConnected;
    }

    public P handleConfigSync(ConfigSyncS2CPacket packet) {
        serverConfig = null;
        fullServerConfig = null;
        // Cleared with the rest so a player who lost the right to edit stops seeing it. The server
        // sends it again straight after this packet to anyone who still may.
        operatorBypass = null;

        var protocolVersion = packet.protocolVersion();
        if (protocolVersion < 4) {
            DoABarrelRoll.LOGGER.error("Received config with old protocol version: {}, this version is no longer supported!", protocolVersion);
            return responseConstructor.apply(protocolVersion, false);
        } else if (protocolVersion > HandshakeServer.PROTOCOL_VERSION) {
            DoABarrelRoll.LOGGER.warn("Received config with unknown protocol version: {}, will attempt to load anyway", protocolVersion);
        }

        serverConfig = packet.applicableConfig();
        if (!packet.isLimited()) {
            fullServerConfig = packet.fullConfig();
        }

        updateCallback.accept(serverConfig);
        hasConnected = true;
        DoABarrelRoll.LOGGER.info("Received config from server");

        return responseConstructor.apply(protocolVersion, true);
    }

    public void reset() {
        serverConfig = null;
        operatorBypass = null;
        hasConnected = false;
    }
}
