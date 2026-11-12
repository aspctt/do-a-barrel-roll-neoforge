package nl.enjarai.doabarrelroll.config;

public class MutableConfigServer {
    public boolean allowThrusting;
    public boolean forceEnabled;
    public boolean forceInstalled;
    public int installedTimeout;
    public KineticDamage kineticDamage;
    public boolean operatorBypass;

    public MutableConfigServer(ModConfigServer config) {
        this.allowThrusting = config.allowThrusting();
        this.forceEnabled = config.forceEnabled();
        this.forceInstalled = config.forceInstalled();
        this.installedTimeout = config.installedTimeout();
        this.kineticDamage = config.kineticDamage();
        this.operatorBypass = config.operatorBypass();
    }

    public ModConfigServer toImmutable() {
        return new ModConfigServer(
                this.allowThrusting, this.forceEnabled,
                this.forceInstalled, this.installedTimeout,
                this.kineticDamage, this.operatorBypass
        );
    }
}
