package de.griefed.serverpackcreator;

import com.therandomlabs.curseapi.CurseException;
import de.griefed.serverpackcreator.curseforge.CurseCreateModpack;
import de.griefed.serverpackcreator.curseforge.InvalidFileException;
import de.griefed.serverpackcreator.curseforge.InvalidModpackException;
import de.griefed.serverpackcreator.i18n.LocalizationManager;
import de.griefed.serverpackcreator.utilities.*;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import java.util.*;

class ConfigurationHandlerTest {

    private final ConfigurationHandler CONFIGURATIONHANDLER;
    private final CurseCreateModpack CURSECREATEMODPACK;
    private final LocalizationManager LOCALIZATIONMANAGER;
    private final DefaultFiles DEFAULTFILES;
    private final VersionLister VERSIONLISTER;
    private final BooleanUtilities BOOLEANUTILITIES;
    private final ListUtilities LISTUTILITIES;
    private final StringUtilities STRINGUTILITIES;
    private final ConfigUtilities CONFIGUTILITIES;
    private final SystemUtilities SYSTEMUTILITIES;
    private final ApplicationProperties APPLICATIONPROPERTIES;

    ConfigurationHandlerTest() {
        try {
            FileUtils.copyFile(new File("backend/main/resources/serverpackcreator.properties"),new File("serverpackcreator.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.APPLICATIONPROPERTIES = new ApplicationProperties();

        LOCALIZATIONMANAGER = new LocalizationManager(APPLICATIONPROPERTIES);
        LOCALIZATIONMANAGER.initialize();
        DEFAULTFILES = new DefaultFiles(LOCALIZATIONMANAGER, APPLICATIONPROPERTIES);
        DEFAULTFILES.filesSetup();
        VERSIONLISTER = new VersionLister(APPLICATIONPROPERTIES);
        LISTUTILITIES = new ListUtilities();
        STRINGUTILITIES = new StringUtilities();
        SYSTEMUTILITIES = new SystemUtilities();
        BOOLEANUTILITIES = new BooleanUtilities(LOCALIZATIONMANAGER, APPLICATIONPROPERTIES);
        CONFIGUTILITIES = new ConfigUtilities(LOCALIZATIONMANAGER, BOOLEANUTILITIES, LISTUTILITIES, APPLICATIONPROPERTIES, STRINGUTILITIES, VERSIONLISTER);
        CURSECREATEMODPACK = new CurseCreateModpack(LOCALIZATIONMANAGER, APPLICATIONPROPERTIES, VERSIONLISTER, BOOLEANUTILITIES, LISTUTILITIES, STRINGUTILITIES, CONFIGUTILITIES, SYSTEMUTILITIES);
        CONFIGURATIONHANDLER = new ConfigurationHandler(LOCALIZATIONMANAGER, CURSECREATEMODPACK, VERSIONLISTER, APPLICATIONPROPERTIES, BOOLEANUTILITIES, LISTUTILITIES, STRINGUTILITIES, SYSTEMUTILITIES, CONFIGUTILITIES);

    }

    @Test
    void getFallbackModsListTest() {
        Assertions.assertNotNull(APPLICATIONPROPERTIES.getListFallbackMods());
    }

    @Test
    void getFallbackModsListTestEquals() {
        String FALLBACK_MODS_DEFAULT_ASSTRING =
                "3dSkinLayers-," +
                "AdvancementPlaques-," +
                "AmbientSounds_," +
                "armorchroma-," +
                "backtools-," +
                "BetterAdvancements-," +
                "BetterAnimationsCollection-," +
                "BetterDarkMode-," +
                "BetterF3-," +
                "BetterF3-," +
                "BetterFoliage-," +
                "BetterPingDisplay-," +
                "BetterPlacement-," +
                "Blur-," +
                "catalogue-," +
                "cherishedworlds-," +
                "classicbar-," +
                "clickadv-," +
                "ClientTweaks_," +
                "configured-," +
                "Controlling-," +
                "CraftPresence-," +
                "CTM-," +
                "customdiscordrpc-," +
                "CustomMainMenu-," +
                "DefaultOptions_," +
                "defaultoptions-," +
                "desiredservers-," +
                "Ding-," +
                "drippyloadingscreen_," +
                "drippyloadingscreen-," +
                "Durability101-," +
                "dynamic-music-," +
                "DynamicSurroundings-," +
                "DynamicSurroundingsHuds-," +
                "dynmus-," +
                "EiraMoticons_," +
                "eiramoticons-," +
                "EnchantmentDescriptions-," +
                "EquipmentCompare-," +
                "extremesoundmuffler-," +
                "extremeSoundMuffler-," +
                "Fallingleaves-," +
                "fallingleaves-," +
                "fancymenu_," +
                "findme-," +
                "flickerfix-," +
                "FpsReducer-," +
                "FullscreenWindowed-," +
                "InventoryEssentials_," +
                "InventorySpam-," +
                "InventoryTweaks-," +
                "invtweaks-," +
                "ItemBorders-," +
                "itemzoom," +
                "itlt-," +
                "jeed-," +
                "jehc-," +
                "jeiintegration_," +
                "JEITweaker-," +
                "just-enough-harvestcraft-," +
                "justenoughbeacons-," +
                "JustEnoughCalculation-," +
                "JustEnoughProfessions-," +
                "JustEnoughProfessions-," +
                "JustEnoughResources-," +
                "keywizard-," +
                "konkrete_," +
                "lazydfu-," +
                "LegendaryTooltips-," +
                "light-overlay-," +
                "LightOverlay-," +
                "LLOverlayReloaded-," +
                "loadmyresources_," +
                "lootbeams-," +
                "mcbindtype-," +
                "medievalmusic-," +
                "modnametooltip_," +
                "modnametooltip-," +
                "moreoverlays-," +
                "MouseTweaks-," +
                "multihotbar-," +
                "MyServerIsCompatible-," +
                "Neat ," +
                "NotifMod-," +
                "OldJavaWarning-," +
                "OptiFine," +
                "OptiForge," +
                "ornaments-," +
                "overloadedarmorbar-," +
                "PackMenu-," +
                "PickUpNotifier-," +
                "Ping-," +
                "preciseblockplacing-," +
                "presencefootsteps-," +
                "PresenceFootsteps-," +
                "ReAuth-," +
                "ResourceLoader-," +
                "shutupexperimentalsettings-," +
                "SimpleDiscordRichPresence-," +
                "smoothboot-," +
                "sounddeviceoptions-," +
                "SpawnerFix-," +
                "spoticraft-," +
                "tconplanner-," +
                "timestamps-," +
                "Tips-," +
                "TipTheScales-," +
                "Toast Control-," +
                "Toast-Control-," +
                "ToastControl-," +
                "torchoptimizer-," +
                "torohealth-," +
                "toughnessbar-," +
                "TravelersTitles-," +
                "WindowedFullscreen-," +
                "WorldNameRandomizer-," +
                "yisthereautojump-";
        //noinspection Convert2Diamond
        List<String> fallbackMods = new ArrayList<String>(
                Arrays.asList(FALLBACK_MODS_DEFAULT_ASSTRING.split(","))
        );
        Assertions.assertEquals(fallbackMods, APPLICATIONPROPERTIES.getListFallbackMods());
    }

    @Test
    void checkConfigFileTest() {
        Assertions.assertFalse(CONFIGURATIONHANDLER.checkConfiguration(new File("./backend/test/resources/testresources/serverpackcreator.conf"), false));
    }

    @Test
    void isDirTestCopyDirs() {
        Assertions.assertTrue(CONFIGURATIONHANDLER.checkConfiguration(new File("./backend/test/resources/testresources/serverpackcreator_copydirs.conf"), false));
    }

    @Test
    void isDirTestJavaPath() {
        Assertions.assertFalse(CONFIGURATIONHANDLER.checkConfiguration(new File("./backend/test/resources/testresources/serverpackcreator_javapath.conf"), false));
    }

    @Test
    void isDirTestMinecraftVersion() {
        Assertions.assertTrue(CONFIGURATIONHANDLER.checkConfiguration(new File("./backend/test/resources/testresources/serverpackcreator_minecraftversion.conf"), false));

    }

    @Test
    void isDirTestModLoader() {
        Assertions.assertFalse(CONFIGURATIONHANDLER.checkConfiguration(new File("./backend/test/resources/testresources/serverpackcreator_modloader.conf"), false));
    }

    @Test
    void isDirTestModLoaderFalse() {
        Assertions.assertTrue(CONFIGURATIONHANDLER.checkConfiguration(new File("./backend/test/resources/testresources/serverpackcreator_modloaderfalse.conf"), false));
    }

    @Test
    void isDirTestModLoaderVersion() {
        Assertions.assertTrue(CONFIGURATIONHANDLER.checkConfiguration(new File("./backend/test/resources/testresources/serverpackcreator_modloaderversion.conf"), false));
    }

    @Disabled
    @Test
    void isCurseTest() {
        // TODO: Check config when re-activating CurseForge module
        Assertions.assertTrue(CONFIGURATIONHANDLER.checkConfiguration(new File("./backend/test/resources/testresources/serverpackcreator_curseforge.conf"), false));
    }

    @Disabled
    @Test
    void isCurseTestProjectIDFalse() {
        // TODO: Check config when re-activating CurseForge module
        Assertions.assertTrue(CONFIGURATIONHANDLER.checkConfiguration(new File("./backend/test/resources/testresources/serverpackcreator_curseforgefalse.conf"), false));
    }

    @Disabled
    @Test
    void isCurseTestProjectFileIDFalse() {
        // TODO: Check config when re-activating CurseForge module
        Assertions.assertTrue(CONFIGURATIONHANDLER.checkConfiguration(new File("./backend/test/resources/testresources/serverpackcreator_curseforgefilefalse.conf"), false));
    }


    @Disabled
    @Test
    void checkCurseForgeTest() throws InvalidModpackException, InvalidFileException, CurseException {
        String valid = "430517,3266321";
        ConfigurationModel configurationModel = new ConfigurationModel();
        Assertions.assertTrue(CONFIGURATIONHANDLER.checkCurseForge(valid, configurationModel, new ArrayList<>(100)));
    }

    @Disabled
    @Test
    void checkCurseForgeTestFalse() throws InvalidModpackException, InvalidFileException, CurseException {
        String invalid = "1,1234";
        ConfigurationModel configurationModel = new ConfigurationModel();
        Assertions.assertFalse(CONFIGURATIONHANDLER.checkCurseForge(invalid, configurationModel, new ArrayList<>(100)));
    }

    @Disabled
    @Test
    void checkCurseForgeTestNotMinecraft() {
        String invalid = "10,60018";
        ConfigurationModel configurationModel = new ConfigurationModel();
        Assertions.assertThrows(InvalidModpackException.class, () -> CONFIGURATIONHANDLER.checkCurseForge(invalid, configurationModel, new ArrayList<>(100)));
    }

    @Test
    void checkModpackDirTest() {
        String modpackDirCorrect = "./backend/test/resources/forge_tests";
        Assertions.assertTrue(CONFIGURATIONHANDLER.checkModpackDir(modpackDirCorrect, new ArrayList<>(100)));
    }

    @Test
    void checkModpackDirTestFalse() {
        Assertions.assertFalse(CONFIGURATIONHANDLER.checkModpackDir("modpackDir", new ArrayList<>(100)));
    }

    @Test
    void checkCopyDirsTest() {
        String modpackDir = "backend/test/resources/forge_tests";
        List<String> copyDirs = new ArrayList<>(Arrays.asList(
                "config",
                "mods",
                "scripts",
                "seeds",
                "defaultconfigs"
        ));
        Assertions.assertTrue(CONFIGURATIONHANDLER.checkCopyDirs(copyDirs, modpackDir, new ArrayList<>(100)));
    }

    @Test
    void checkCopyDirsTestFalse() {
        String modpackDir = "backend/test/resources/forge_tests";
        List<String> copyDirsInvalid = new ArrayList<>(Arrays.asList(
                "configs",
                "modss",
                "scriptss",
                "seedss",
                "defaultconfigss"
        ));
        Assertions.assertFalse(CONFIGURATIONHANDLER.checkCopyDirs(copyDirsInvalid, modpackDir, new ArrayList<>(100)));
    }

    @Test
    void checkCopyDirsTestFiles() {
        String modpackDir = "backend/test/resources/forge_tests";
        List<String> copyDirsAndFiles = new ArrayList<>(Arrays.asList(
                "config",
                "mods",
                "scripts",
                "seeds",
                "defaultconfigs",
                "test.txt;test.txt",
                "test2.txt;test2.txt"
        ));
        Assertions.assertTrue(CONFIGURATIONHANDLER.checkCopyDirs(copyDirsAndFiles, modpackDir, new ArrayList<>(100)));
    }

    @Test
    void checkCopyDirsTestFilesFalse() {
        String modpackDir = "backend/test/resources/forge_tests";
        List<String> copyDirsAndFilesFalse = new ArrayList<>(Arrays.asList(
                "configs",
                "modss",
                "scriptss",
                "seedss",
                "defaultconfigss",
                "READMEee.md;README.md",
                "LICENSEee;LICENSE",
                "LICENSEee;test/LICENSE",
                "LICENSEee;test/license.md"
        ));
        Assertions.assertFalse(CONFIGURATIONHANDLER.checkCopyDirs(copyDirsAndFilesFalse, modpackDir, new ArrayList<>(100)));
    }

    @Test
    void checkJavaPathTest() {
        String javaPath;
        String autoJavaPath = System.getProperty("java.home").replace("\\", "/") + "/bin/java";
        if (autoJavaPath.startsWith("C:")) {
            autoJavaPath = String.format("%s.exe", autoJavaPath);
        }
        if (new File("/usr/bin/java").exists()) {
            javaPath = "/usr/bin/java";
        } else if (new File("/opt/hostedtoolcache/jdk/8.0.282/x64/bin/java").exists()) {
            javaPath = "/opt/hostedtoolcache/jdk/8.0.282/x64/bin/java";
        } else {
            javaPath = autoJavaPath;
        }
        Assertions.assertNotNull(CONFIGURATIONHANDLER.checkJavaPath(javaPath));
        Assertions.assertTrue(new File(CONFIGURATIONHANDLER.checkJavaPath(javaPath)).exists());
    }

    @Test
    void checkModloaderTestForge() {
        Assertions.assertTrue(CONFIGURATIONHANDLER.checkModloader("Forge"));
    }

    @Test
    void checkModloaderTestForgeCase() {
        Assertions.assertTrue(CONFIGURATIONHANDLER.checkModloader("fOrGe"));
    }

    @Test
    void checkModloaderTestFabric() {
        Assertions.assertTrue(CONFIGURATIONHANDLER.checkModloader("Fabric"));
    }

    @Test
    void checkModloaderTestFabricCase() {
        Assertions.assertTrue(CONFIGURATIONHANDLER.checkModloader("fAbRiC"));
    }

    @Test
    void checkModLoaderTestFalse() {
        Assertions.assertFalse(CONFIGURATIONHANDLER.checkModloader("modloader"));
    }

    @Test
    void checkModloaderVersionTestForge() {
        Assertions.assertTrue(CONFIGURATIONHANDLER.checkModloaderVersion("Forge", "36.1.2", "1.16.5"));
    }

    @Test
    void checkModloaderVersionTestForgeFalse() {
        Assertions.assertFalse(CONFIGURATIONHANDLER.checkModloaderVersion("Forge", "90.0.0", "1.16.5"));
    }

    @Test
    void checkModloaderVersionTestFabric() {
        Assertions.assertTrue(CONFIGURATIONHANDLER.checkModloaderVersion("Fabric", "0.11.3", "1.16.5"));
    }

    @Test
    void checkModloaderVersionTestFabricFalse() {
        Assertions.assertFalse(CONFIGURATIONHANDLER.checkModloaderVersion("Fabric", "0.90.3", "1.16.5"));
    }

    @Test
    void isMinecraftVersionCorrectTest() {
        Assertions.assertTrue(CONFIGURATIONHANDLER.isMinecraftVersionCorrect("1.16.5"));
    }

    @Test
    void isMinecraftVersionCorrectTestFalse() {
        Assertions.assertFalse(CONFIGURATIONHANDLER.isMinecraftVersionCorrect("1.99.5"));
    }

    @Test
    void isFabricVersionCorrectTest() {
        Assertions.assertTrue(CONFIGURATIONHANDLER.isFabricVersionCorrect("0.11.3"));
    }

    @Test
    void isFabricVersionCorrectTestFalse() {
        Assertions.assertFalse(CONFIGURATIONHANDLER.isFabricVersionCorrect("0.90.3"));
    }

    @Test
    void isForgeVersionCorrectTest() {
        Assertions.assertTrue(CONFIGURATIONHANDLER.isForgeVersionCorrect("36.1.2", "1.16.5"));
    }

    @Test
    void isForgeVersionCorrectTestFalse() {
        Assertions.assertFalse(CONFIGURATIONHANDLER.isForgeVersionCorrect("99.0.0","1.16.5"));
    }

    @Test
    void checkConfigModelTest() {
        List<String> clientMods = new ArrayList<>(Arrays.asList(
                "AmbientSounds",
                "BackTools",
                "BetterAdvancement",
                "BetterPing",
                "cherished",
                "ClientTweaks",
                "Controlling",
                "DefaultOptions",
                "durability",
                "DynamicSurroundings",
                "itemzoom",
                "jei-professions",
                "jeiintegration",
                "JustEnoughResources",
                "MouseTweaks",
                "Neat",
                "OldJavaWarning",
                "PackMenu",
                "preciseblockplacing",
                "SimpleDiscordRichPresence",
                "SpawnerFix",
                "TipTheScales",
                "WorldNameRandomizer"
        ));
        List<String> copyDirs = new ArrayList<>(Arrays.asList(
                "config",
                "mods",
                "scripts",
                "seeds",
                "defaultconfigs"
        ));
        ConfigurationModel configurationModel = new ConfigurationModel();
        configurationModel.setModpackDir("./backend/test/resources/forge_tests");
        configurationModel.setClientMods(clientMods);
        configurationModel.setCopyDirs(copyDirs);
        configurationModel.setJavaPath("");
        configurationModel.setIncludeServerInstallation(true);
        configurationModel.setIncludeServerIcon(true);
        configurationModel.setIncludeServerProperties(true);
        configurationModel.setIncludeZipCreation(true);
        configurationModel.setModLoader("Forge");
        configurationModel.setModLoaderVersion("36.1.2");
        configurationModel.setMinecraftVersion("1.16.5");
        Assertions.assertFalse(CONFIGURATIONHANDLER.checkConfiguration(configurationModel, false));
    }

    @Test
    void zipArchiveTest() {
        ConfigurationModel configurationModel = new ConfigurationModel();
        configurationModel.setModpackDir("backend/test/resources/testresources/Survive_Create_Prosper_4_valid.zip");
        Assertions.assertFalse(CONFIGURATIONHANDLER.checkConfiguration(configurationModel,true));
        configurationModel = new ConfigurationModel();
        configurationModel.setModpackDir("backend/test/resources/testresources/Survive_Create_Prosper_4_invalid.zip");
        Assertions.assertTrue(CONFIGURATIONHANDLER.checkConfiguration(configurationModel,true));
    }

    @Test
    void checkConfigurationFileTest() {
        Assertions.assertFalse(CONFIGURATIONHANDLER.checkConfiguration(new File("backend/test/resources/testresources/serverpackcreator.conf"), new ArrayList<>(),true));
    }

    @Test
    void checkConfigurationFileNoDownloadTest() {
        Assertions.assertFalse(CONFIGURATIONHANDLER.checkConfiguration(new File("backend/test/resources/testresources/serverpackcreator.conf"),false,true));
    }

    @Test
    void checkConfigurationFileAndModelTest() {
        ConfigurationModel configurationModel = new ConfigurationModel();
        Assertions.assertFalse(CONFIGURATIONHANDLER.checkConfiguration(new File("backend/test/resources/testresources/serverpackcreator.conf"), configurationModel, false,true));
        Assertions.assertEquals("./backend/test/resources/forge_tests", configurationModel.getModpackDir());
        Assertions.assertEquals("1.16.5",configurationModel.getMinecraftVersion());
        Assertions.assertEquals("Forge",configurationModel.getModLoader());
        Assertions.assertEquals("36.1.2",configurationModel.getModLoaderVersion());
    }

    @Test
    void checkConfigurationFileModelExtendedParamsTest() {
        ConfigurationModel configurationModel = new ConfigurationModel();
        Assertions.assertFalse(CONFIGURATIONHANDLER.checkConfiguration(new File("backend/test/resources/testresources/serverpackcreator.conf"), configurationModel,new ArrayList<>(),false,false));
        Assertions.assertEquals("./backend/test/resources/forge_tests", configurationModel.getModpackDir());
        Assertions.assertEquals("1.16.5",configurationModel.getMinecraftVersion());
        Assertions.assertEquals("Forge",configurationModel.getModLoader());
        Assertions.assertEquals("36.1.2",configurationModel.getModLoaderVersion());
    }

    @Test
    void checkConfigurationNoFileTest() {
        ConfigurationModel configurationModel = new ConfigurationModel();
        List<String> clientMods = new ArrayList<>(Arrays.asList(
                "AmbientSounds",
                "BackTools",
                "BetterAdvancement",
                "BetterPing",
                "cherished",
                "ClientTweaks",
                "Controlling",
                "DefaultOptions",
                "durability",
                "DynamicSurroundings",
                "itemzoom",
                "jei-professions",
                "jeiintegration",
                "JustEnoughResources",
                "MouseTweaks",
                "Neat",
                "OldJavaWarning",
                "PackMenu",
                "preciseblockplacing",
                "SimpleDiscordRichPresence",
                "SpawnerFix",
                "TipTheScales",
                "WorldNameRandomizer"
        ));
        List<String> copyDirs = new ArrayList<>(Arrays.asList(
                "config",
                "mods",
                "scripts",
                "seeds",
                "defaultconfigs"
        ));
        configurationModel.setModpackDir("./backend/test/resources/forge_tests");
        configurationModel.setClientMods(clientMods);
        configurationModel.setCopyDirs(copyDirs);
        configurationModel.setJavaPath("");
        configurationModel.setIncludeServerInstallation(true);
        configurationModel.setIncludeServerIcon(true);
        configurationModel.setIncludeServerProperties(true);
        configurationModel.setIncludeZipCreation(true);
        configurationModel.setModLoader("Forge");
        configurationModel.setModLoaderVersion("36.1.2");
        configurationModel.setMinecraftVersion("1.16.5");
        Assertions.assertFalse(CONFIGURATIONHANDLER.checkConfiguration(configurationModel,new ArrayList<>(),false,true));
        Assertions.assertEquals("./backend/test/resources/forge_tests", configurationModel.getModpackDir());
        Assertions.assertEquals("1.16.5",configurationModel.getMinecraftVersion());
        Assertions.assertEquals("Forge",configurationModel.getModLoader());
        Assertions.assertEquals("36.1.2",configurationModel.getModLoaderVersion());
    }

    @Test
    void checkIconAndPropertiesTest() {
        Assertions.assertTrue(CONFIGURATIONHANDLER.checkIconAndProperties(""));
        Assertions.assertFalse(CONFIGURATIONHANDLER.checkIconAndProperties("/some/path"));
        Assertions.assertTrue(CONFIGURATIONHANDLER.checkIconAndProperties("img/prosper.png"));
    }
}