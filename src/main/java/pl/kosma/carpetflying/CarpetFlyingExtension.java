package pl.kosma.carpetflying;

import carpet.CarpetExtension;
import carpet.CarpetServer;
import carpet.api.settings.SettingsManager;
import carpet.utils.Translations;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class CarpetFlyingExtension implements ModInitializer, CarpetExtension {
    private static final String MOD_ID = "carpet-flying";
    private static final ModContainer MOD_CONTAINER = FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow();
    private static final String MOD_NAME = MOD_CONTAINER.getMetadata().getName();
    private static final String MOD_VERSION = MOD_CONTAINER.getMetadata().getVersion().getFriendlyString();
    private static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static SettingsManager settingsManager;

    public CarpetFlyingExtension() {
        CarpetServer.manageExtension(this);
    }

    @Override
    public void onInitialize() {
        settingsManager = new SettingsManager(MOD_VERSION, MOD_ID, MOD_NAME);
    }

    @Override
    public void onGameStarted() {
        settingsManager.parseSettingsClass(CarpetFlyingSettings.class);
        LOGGER.info("Cleared for takeoff.");
    }

    @Override
    public SettingsManager extensionSettingsManager() {
        return settingsManager;
    }

    @Override
    public String version() {
        return MOD_ID + " " + MOD_VERSION;
    }

    @Override
    public Map<String, String> canHasTranslations(String lang) {
        return Translations.getTranslationFromResourcePath("assets/%s/carpet/lang/%s.json".formatted(MOD_ID, lang));
    }
}
