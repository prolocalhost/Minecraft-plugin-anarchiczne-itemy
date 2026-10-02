package pl.localhost.anarchia.item;

import org.bukkit.Material;

public enum AnarchiaItemType {
    BOMBARDA_MAXIMA("bombarda-maxima", "&c&lBombarda Maxima", Material.FIRE_CHARGE, 15),
    ANTY_COBWEB("anty-cobweb", "&f&lAnty-Cobweb", Material.SHEARS, 10),
    TRAPBOX("trapbox", "&e&lTrapbox &7(Rzucana Pajęczyna)", Material.COBWEB, 20),
    BOSKI_TOPOR("boski-topor", "&6&lBoski Topór", Material.NETHERITE_AXE, 45),
    EXCALIBUR("excalibur", "&b&lExcalibur", Material.NETHERITE_SWORD, 10),
    KREW_WAMPIRA("krew-wampira", "&4&lKrew Wampira", Material.RED_DYE, 12),
    KOSTKA_RUBIKA("kostka-rubika", "&d&lKostka Rubika", Material.CONDUIT, 25),
    NISZCZYCIEL_BEDROCKA("niszczyciel-bedrocka", "&8&lNiszczyciel Bedrocka", Material.TNT, 30),
    KORONA_ANARCHII("korona-anarchii", "&6&lKorona Anarchii", Material.GOLDEN_HELMET, 0),
    LIZAK_ANARCHII("lizak-anarchii", "&a&lLizak Anarchii", Material.COOKIE, 60),
    KILOF_ANARCHII("kilof-anarchii", "&5&lAnarchiczny Kilof &e☆∞", Material.NETHERITE_PICKAXE, 0);

    private final String configKey;
    private final String defaultName;
    private final Material defaultMaterial;
    private final int defaultCooldown;

    AnarchiaItemType(String configKey, String defaultName, Material defaultMaterial, int defaultCooldown) {
        this.configKey = configKey;
        this.defaultName = defaultName;
        this.defaultMaterial = defaultMaterial;
        this.defaultCooldown = defaultCooldown;
    }

    public String getConfigKey() {
        return configKey;
    }

    public String getDefaultName() {
        return defaultName;
    }

    public Material getDefaultMaterial() {
        return defaultMaterial;
    }

    public int getDefaultCooldown() {
        return defaultCooldown;
    }

    public static AnarchiaItemType fromKey(String key) {
        if (key == null) return null;
        String clean = key.toLowerCase().replace("_", "-");
        for (AnarchiaItemType type : values()) {
            if (type.configKey.equalsIgnoreCase(clean) || type.name().equalsIgnoreCase(key)) {
                return type;
            }
        }
        return null;
    }
}
