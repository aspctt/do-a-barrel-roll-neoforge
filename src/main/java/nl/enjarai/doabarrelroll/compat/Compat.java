package nl.enjarai.doabarrelroll.compat;

import net.neoforged.fml.ModList;

public class Compat {
    public static final String YACL_MOD_ID = "yet_another_config_lib_v3";
    public static final String YACL_MIN_VERSION = "3.6.0";

    public static boolean isYACLLoaded() {
        return checkModLoaded(YACL_MOD_ID);
    }

    public static boolean isYACLUpToDate() {
        return isModVersionAtLeast(YACL_MOD_ID, YACL_MIN_VERSION);
    }

    public static boolean checkModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    public static boolean isModVersionAtLeast(String modId, String minimum) {
        return ModList.get().getModContainerById(modId)
                .map(container -> container.getModInfo().getVersion().toString())
                .filter(present -> compareVersions(present, minimum) >= 0)
                .isPresent();
    }

    /**
     * Compares the leading numeric parts of two version strings.
     *
     * <p>Deliberately not Maven's own ordering. Mod versions here look like
     * "3.8.1+1.21.1-neoforge", and Maven parses everything after the first "+" as
     * an opaque qualifier compared as text, so "3.8.1+1.21.1" and "3.10.0+1.21.1"
     * sort by string rather than by number. Only the release number decides
     * whether an API is present, so everything from the first "+" or "-" is cut
     * and the dotted numbers in front of it are compared as numbers. A component
     * that is not a number stops the comparison, leaving the versions equal from
     * that point on.
     */
    static int compareVersions(String a, String b) {
        String[] left = trimBuildMetadata(a).split("\\.");
        String[] right = trimBuildMetadata(b).split("\\.");

        for (int i = 0; i < Math.max(left.length, right.length); i++) {
            int l = parseOrNegative(i < left.length ? left[i] : "0");
            int r = parseOrNegative(i < right.length ? right[i] : "0");

            if (l < 0 || r < 0) return 0;
            if (l != r) return Integer.compare(l, r);
        }

        return 0;
    }

    private static String trimBuildMetadata(String version) {
        int cut = version.length();
        for (var separator : new char[]{'+', '-'}) {
            int index = version.indexOf(separator);
            if (index >= 0 && index < cut) cut = index;
        }
        return version.substring(0, cut);
    }

    private static int parseOrNegative(String component) {
        try {
            return Integer.parseInt(component.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
