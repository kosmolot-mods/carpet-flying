package pl.kosma.carpetflying;

import carpet.CarpetExtension;
import carpet.CarpetServer;
import carpet.api.settings.SettingsManager;
import carpet.utils.Translations;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class CarpetFlying implements ModInitializer, CarpetExtension {
    private static final String MOD_ID = "carpet-flying";
    private static final Logger LOGGER = LoggerFactory.getLogger(CarpetFlying.class);

    private static final String modVersion;
    private static final SettingsManager settingsManager;

    static {
        ModMetadata metadata = FabricLoader.getInstance().getModContainer(MOD_ID).get().getMetadata();
        String modName = metadata.getName();
        modVersion = metadata.getVersion().getFriendlyString();
        settingsManager = new SettingsManager(modVersion, MOD_ID, modName);
    }

    @Override
    public void onInitialize() {
        CarpetServer.manageExtension(new CarpetFlying());
    }

    @Override
    public void onGameStarted() {
        settingsManager.parseSettingsClass(CarpetFlyingSettings.class);
    }

    @Override
    public String version() {
        return modVersion;
    }

    @Override
    public Map<String, String> canHasTranslations(String lang) {
        return Translations.getTranslationFromResourcePath("assets/%s/lang/%s.json".formatted(MOD_ID, lang));
    }
}
