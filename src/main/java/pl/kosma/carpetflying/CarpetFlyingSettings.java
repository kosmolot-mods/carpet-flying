package pl.kosma.carpetflying;

import carpet.api.settings.Rule;

import static carpet.api.settings.RuleCategory.*;

public class CarpetFlyingSettings {
    @Rule(categories = FEATURE)
    public static boolean cycleVillagerTrades = false;

    @Rule(categories = FEATURE)
    public static boolean displayNamesOnPlayerList = false;

    @Rule(categories = FEATURE)
    public static boolean displayNameCarpetBot = false;

    @Rule(categories = FEATURE)
    public static boolean displayNameVanillaTweaksAFK = false;

    @Rule(categories = FEATURE)
    public static boolean enderDragonNoGriefing = false;

    @Rule(categories = FEATURE)
    public static boolean disablePlayerSpawnCommand = false;

    @Rule(categories = FEATURE)
    public static boolean perPlayerViewDistance = false;
}
