/* Copyright (C) 2022  Griefed
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
 * USA
 *
 * The full license can be found at https:github.com/Griefed/ServerPackCreator/blob/main/LICENSE
 */
package de.griefed.serverpackcreator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

/**
 * Our properties-class. Extends {@link java.util.Properties}. Sets up default properties loaded
 * from the local serverpackcreator.properties and allows reloading of said properties if the file
 * has changed.
 *
 * @author Griefed
 */
@Component
public class ApplicationProperties extends Properties {

  private static final Logger LOG = LogManager.getLogger(ApplicationProperties.class);

  // ServerPackHandler related
  private final File SERVERPACKCREATOR_PROPERTIES = new File("serverpackcreator.properties");
  private final File START_SCRIPT_WINDOWS = new File("start.bat");
  private final File START_SCRIPT_LINUX = new File("start.sh");
  private final File USER_JVM_ARGS = new File("user_jvm_args.txt");

  private final String FALLBACK_MODS_DEFAULT_ASSTRING =
      "3dSkinLayers-,"
          + "3dskinlayers-,"
          + "Absolutely-Not-A-Zoom-Mod-,"
          + "AdvancementPlaques-,"
          + "AmbientEnvironment-,"
          + "AmbientSounds_,"
          + "antighost-,"
          + "armorchroma-,"
          + "armorpointspp-,"
          + "ArmorSoundTweak-,"
          + "authme-,"
          + "autoreconnect-,"
          + "auto-reconnect-,"
          + "axolotl-item-fix-,"
          + "backtools-,"
          + "BetterAdvancements-,"
          + "BetterAnimationsCollection-,"
          + "betterbiomeblend-,"
          + "BetterDarkMode-,"
          + "BetterF3-,"
          + "BetterFoliage-,"
          + "BetterPingDisplay-,"
          + "BetterPlacement-,"
          + "BetterTaskbar-,"
          + "bhmenu-,"
          + "BH-Menu-,"
          + "blur-,"
          + "Blur-,"
          + "borderless-mining-,"
          + "catalogue-,"
          + "charmonium-,"
          + "Charmonium-,"
          + "chat_heads-,"
          + "cherishedworlds-,"
          + "classicbar-,"
          + "clickadv-,"
          + "ClientTweaks_,"
          + "configured-,"
          + "Controlling-,"
          + "CraftPresence-,"
          + "CTM-,"
          + "cullleaves-,"
          + "customdiscordrpc-,"
          + "CustomMainMenu-,"
          + "dashloader-,"
          + "DefaultOptions_,"
          + "defaultoptions-,"
          + "DefaultSettings-,"
          + "DeleteWorldsToTrash-,"
          + "desiredservers-,"
          + "Ding-,"
          + "drippyloadingscreen_,"
          + "drippyloadingscreen-,"
          + "DripSounds-,"
          + "Durability101-,"
          + "DurabilityNotifier-,"
          + "dynamic-fps-,"
          + "dynamic-music-,"
          + "DynamicSurroundings-,"
          + "DynamicSurroundingsHuds-,"
          + "dynmus-,"
          + "effective-,"
          + "eggtab-,"
          + "EiraMoticons_,"
          + "eiramoticons-,"
          + "EnchantmentDescriptions-,"
          + "entity-texture-features-,"
          + "EquipmentCompare-,"
          + "extremesoundmuffler-,"
          + "extremeSoundMuffler-,"
          + "fabricemotes-,"
          + "Fallingleaves-,"
          + "fallingleaves-,"
          + "fancymenu_,"
          + "findme-,"
          + "flickerfix-,"
          + "FPS-Monitor-,"
          + "FpsReducer-,"
          + "FullscreenWindowed-,"
          + "InventoryEssentials_,"
          + "InventorySpam-,"
          + "InventoryTweaks-,"
          + "invtweaks-,"
          + "ItemBorders-,"
          + "ItemStitchingFix-,"
          + "itemzoom,"
          + "itlt-,"
          + "jeed-,"
          + "jehc-,"
          + "jeiintegration_,"
          + "just-enough-harvestcraft-,"
          + "justenoughbeacons-,"
          + "JustEnoughCalculation-,"
          + "JustEnoughProfessions-,"
          + "JustEnoughProfessions-,"
          + "JustEnoughResources-,"
          + "keymap-,"
          + "keywizard-,"
          + "konkrete_,"
          + "lazydfu-,"
          + "LegendaryTooltips-,"
          + "light-overlay-,"
          + "LightOverlay-,"
          + "LLOverlayReloaded-,"
          + "loadmyresources_,"
          + "lootbeams-,"
          + "mcbindtype-,"
          + "medievalmusic-,"
          + "modcredits-,"
          + "modmenu-,"
          + "modnametooltip_,"
          + "modnametooltip-,"
          + "moreoverlays-,"
          + "MouseTweaks-,"
          + "movement-vision-,"
          + "multihotbar-,"
          + "musicdr-,"
          + "music-duration-reducer-,"
          + "MyServerIsCompatible-,"
          + "Neat ,"
          + "ngrok-lan-expose-mod-,"
          + "NotifMod-,"
          + "OldJavaWarning-,"
          + "OptiFine,"
          + "OptiForge,"
          + "ornaments-,"
          + "overloadedarmorbar-,"
          + "PackMenu-,"
          + "PickUpNotifier-,"
          + "Ping-,"
          + "preciseblockplacing-,"
          + "presencefootsteps-,"
          + "PresenceFootsteps-,"
          + "ReAuth-,"
          + "rebrand-,"
          + "ResourceLoader-,"
          + "shutupexperimentalsettings-,"
          + "SimpleDiscordRichPresence-,"
          + "smoothboot-,"
          + "sounddeviceoptions-,"
          + "SpawnerFix-,"
          + "spoticraft-,"
          + "tconplanner-,"
          + "timestamps-,"
          + "Tips-,"
          + "TipTheScales-,"
          + "Toast Control-,"
          + "Toast-Control-,"
          + "ToastControl-,"
          + "torchoptimizer-,"
          + "torohealth-,"
          + "toughnessbar-,"
          + "TravelersTitles-,"
          + "WindowedFullscreen-,"
          + "WorldNameRandomizer-,"
          + "yisthereautojump-";
  private final String SERVERPACKCREATOR_VERSION;
  private final String[] SUPPORTED_MODLOADERS = new String[] {"Fabric", "Forge", "Quilt"};

  private final List<String> FALLBACK_CLIENTSIDE_MODS =
      new ArrayList<>(Arrays.asList(FALLBACK_MODS_DEFAULT_ASSTRING.split(",")));

  private final String FALLBACK_DIRECTORIES_INCLUDE_ASSTRING = "mods,config,defaultconfigs,scripts";
  private final List<String> FALLBACK_DIRECTORIES_INCLUDE =
      new ArrayList<>(Arrays.asList(FALLBACK_DIRECTORIES_INCLUDE_ASSTRING.split(",")));

  private final String FALLBACK_DIRECTORIES_EXCLUDE_ASSTRING =
      "overrides,packmenu,resourcepacks,server_pack,fancymenu,libraries";
  private final List<String> FALLBACK_DIRECTORIES_EXCLUDE =
      new ArrayList<>(Arrays.asList(FALLBACK_DIRECTORIES_EXCLUDE_ASSTRING.split(",")));

  // DefaultFiles related
  private final File DEFAULT_CONFIG = new File("serverpackcreator.conf");
  private final File OLD_CONFIG = new File("creator.conf");
  private final File DEFAULT_SERVER_PROPERTIES = new File("server.properties");
  private final File DEFAULT_SERVER_ICON = new File("server-icon.png");
  private final File MINECRAFT_VERSION_MANIFEST = new File("minecraft-manifest.json");
  private final File FORGE_VERSION_MANIFEST = new File("forge-manifest.json");
  private final File FABRIC_VERSION_MANIFEST = new File("fabric-manifest.xml");
  private final File FABRIC_INSTALLER_VERSION_MANIFEST = new File("fabric-installer-manifest.xml");
  private final File QUILT_VERSION_MANIFEST = new File("quilt-manifest.xml");
  private final File QUILT_INSTALLER_VERSION_MANIFEST = new File("quilt-installer-manifest.xml");
  private final File SERVERPACKCREATOR_DATABASE = new File("serverpackcreator.db");

  // VersionLister related
  private final File MINECRAFT_VERSION_MANIFEST_LOCATION =
      new File("./work/minecraft-manifest.json");
  private final File FORGE_VERSION_MANIFEST_LOCATION = new File("./work/forge-manifest.json");
  private final File FABRIC_VERSION_MANIFEST_LOCATION = new File("./work/fabric-manifest.xml");
  private final File FABRIC_INSTALLER_VERSION_MANIFEST_LOCATION =
      new File("./work/fabric-installer-manifest.xml");
  private final File QUILT_VERSION_MANIFEST_LOCATION = new File("./work/quilt-manifest.xml");
  private final File QUILT_INSTALLER_VERSION_MANIFEST_LOCATION =
      new File("./work/quilt-installer-manifest.xml");

  /**
   * The directory in which server packs will be generated and stored in, as well as server pack
   * ZIP-archives. Default is ./server-packs
   */
  private String directoryServerPacks;

  /** List of mods which should be excluded from server packs. */
  private List<String> listFallbackMods;

  /**
   * List of directories which should be excluded from server packs. Default is overrides, packmenu,
   * resourcepacks, server_pack, fancymenu.
   */
  private List<String> directoriesToExclude;

  /**
   * List of directories which must not be excluded from server packs. Default is mods, config,
   * defaultconfigs, scripts, saves, seeds, libraries.
   */
  private List<String> directoriesToInclude;

  /**
   * When running as a webservice: Maximum disk usage in % at which JMS/Artemis will stop storing
   * message in the queue saved on disk. Default is 90%.
   */
  private int queueMaxDiskUsage;

  /**
   * Whether the manually loaded configuration file should be saved as well as the default
   * serverpackcreator.conf. Setting this to true will make ServerPackCreator save
   * serverpackcreator.conf as well as the last loaded configuration-file. Default is false.
   */
  private boolean saveLoadedConfiguration;

  /**
   * Whether ServerPackCreator should check for available PreReleases. Set to <code>true</code> to
   * get notified about available PreReleases. Set to <code>false</code> if you only want stable
   * releases.
   */
  private boolean versioncheck_prerelease;

  /**
   * Aikars flags recommended for running a Minecraft server, from <a
   * href=https://aikar.co/mcflags.html>aikar.co</a>
   */
  private String aikarsFlags;

  /**
   * Constructor for our properties. Sets a couple of default values for use in ServerPackCreator.
   *
   * @author Griefed
   */
  @Autowired
  public ApplicationProperties() {

    // Load the properties file from the classpath, providing default values.
    try (InputStream inputStream =
        new ClassPathResource("serverpackcreator.properties").getInputStream()) {
      load(inputStream);
    } catch (IOException ex) {
      LOG.error("Couldn't read properties file.", ex);
    }
    /*
     * Now load the properties file from the local filesystem. This overwrites previously loaded properties
     * but has the advantage of always providing default values if any property in the applications.properties
     * on the filesystem should be commented out.
     */
    if (new File("serverpackcreator.properties").exists()) {
      try (InputStream inputStream =
          Files.newInputStream(Paths.get("serverpackcreator.properties"))) {
        load(inputStream);
      } catch (IOException ex) {
        LOG.error("Couldn't read properties file.", ex);
      }
    }

    // Set the directory where the generated server packs will be stored in.
    String tempDir = null;
    try {

      // Try to use the directory specified in the
      // de.griefed.serverpackcreator.configuration.directories.serverpacks property.
      tempDir =
          this.getProperty(
              "de.griefed.serverpackcreator.configuration.directories.serverpacks", "server-packs");

    } catch (NullPointerException npe) {

      // If setting the directory via property fails, set the property to the default value
      // server-packs.
      this.setProperty(
          "de.griefed.serverpackcreator.configuration.directories.serverpacks", "server-packs");
      tempDir = "server-packs";

    } finally {

      // Check tempDir for correctness. Set property and directory if it is correct and overwrite
      // serverpackcreator.properties
      if (tempDir != null && !tempDir.isEmpty() && new File(tempDir).isDirectory()) {
        this.setProperty(
            "de.griefed.serverpackcreator.configuration.directories.serverpacks", tempDir);
        this.directoryServerPacks = tempDir;

        try (OutputStream outputStream =
            Files.newOutputStream(this.SERVERPACKCREATOR_PROPERTIES.toPath())) {
          this.store(outputStream, null);
        } catch (IOException ex) {
          LOG.error("Couldn't write properties-file.", ex);
        }

        // Use directory server-packs
      } else {
        this.directoryServerPacks = "server-packs";
      }
    }

    if (this.getProperty("de.griefed.serverpackcreator.configuration.fallbackmodslist") == null) {

      this.listFallbackMods = this.FALLBACK_CLIENTSIDE_MODS;
      LOG.debug("Fallbackmodslist property null. Using fallback: " + this.FALLBACK_CLIENTSIDE_MODS);

    } else if (this.getProperty("de.griefed.serverpackcreator.configuration.fallbackmodslist")
        .contains(",")) {

      this.listFallbackMods =
          new ArrayList<>(
              Arrays.asList(
                  this.getProperty(
                          "de.griefed.serverpackcreator.configuration.fallbackmodslist",
                          this.FALLBACK_MODS_DEFAULT_ASSTRING)
                      .split(",")));
      LOG.debug("Fallbackmodslist set to: " + this.listFallbackMods);

    } else {

      this.listFallbackMods =
          Collections.singletonList(
              (this.getProperty("de.griefed.serverpackcreator.configuration.fallbackmodslist")));
      LOG.debug("Fallbackmodslist set to: " + this.listFallbackMods);
    }

    // List of directories which can be excluded from server packs
    if (this.getProperty("de.griefed.serverpackcreator.configuration.directories.shouldexclude")
        == null) {

      this.directoriesToExclude = FALLBACK_DIRECTORIES_EXCLUDE;
      LOG.debug(
          "directories.shouldexclude-property null. Using fallback: "
              + this.directoriesToExclude);

    } else if (this.getProperty(
            "de.griefed.serverpackcreator.configuration.directories.shouldexclude")
        .contains(",")) {

      this.directoriesToExclude =
          new ArrayList<>(
              Arrays.asList(
                  this.getProperty(
                          "de.griefed.serverpackcreator.configuration.directories.shouldexclude",
                          FALLBACK_DIRECTORIES_EXCLUDE_ASSTRING)
                      .split(",")));
      LOG.debug("Directories to exclude set to: " + this.directoriesToExclude);

    } else {

      this.directoriesToExclude =
          Collections.singletonList(
              this.getProperty(
                  "de.griefed.serverpackcreator.configuration.directories.shouldexclude"));
      LOG.debug("Directories to exclude set to: " + this.directoriesToExclude);
    }

    // List of directories which should always be included in a server pack, no matter what the
    // users specify
    if (this.getProperty("de.griefed.serverpackcreator.configuration.directories.mustinclude")
        == null) {

      this.directoriesToInclude = FALLBACK_DIRECTORIES_INCLUDE;
      LOG.debug(
          "directories.mustinclude-property null. Using fallback: "
              + this.directoriesToInclude);

    } else if (this.getProperty(
            "de.griefed.serverpackcreator.configuration.directories.mustinclude")
        .contains(",")) {

      this.directoriesToInclude =
          new ArrayList<>(
              Arrays.asList(
                  this.getProperty(
                          "de.griefed.serverpackcreator.configuration.directories.mustinclude",
                          FALLBACK_DIRECTORIES_INCLUDE_ASSTRING)
                      .split(",")));
      LOG.debug(
          "Directories which must always be included set to: " + this.directoriesToInclude);

    } else {

      this.directoriesToInclude =
          Collections.singletonList(
              this.getProperty(
                  "de.griefed.serverpackcreator.configuration.directories.mustinclude"));
      LOG.debug(
          "Directories which must always be included set to: " + this.directoriesToInclude);
    }

    this.queueMaxDiskUsage =
        Integer.parseInt(
            getProperty("de.griefed.serverpackcreator.spring.artemis.queue.max_disk_usage", "90"));

    this.saveLoadedConfiguration =
        Boolean.parseBoolean(
            getProperty("de.griefed.serverpackcreator.configuration.saveloadedconfig", "false"));

    this.versioncheck_prerelease =
        Boolean.parseBoolean(
            getProperty("de.griefed.serverpackcreator.versioncheck.prerelease", "false"));

    String version = ApplicationProperties.class.getPackage().getImplementationVersion();
    if (version != null) {
      this.SERVERPACKCREATOR_VERSION = version;
    } else {
      this.SERVERPACKCREATOR_VERSION = "dev";
    }

    this.aikarsFlags = this.getProperty("de.griefed.serverpackcreator.configuration.aikar");
  }

  /**
   * Reload serverpackcreator.properties.
   *
   * @author Griefed
   */
  public void reload() {

    try (InputStream inputStream =
        Files.newInputStream(Paths.get("serverpackcreator.properties"))) {
      load(inputStream);
    } catch (IOException ex) {
      LOG.error("Couldn't read properties file.", ex);
    }

    String tempDir = null;
    try {

      tempDir =
          this.getProperty(
              "de.griefed.serverpackcreator.configuration.directories.serverpacks", "server-packs");

    } catch (NullPointerException npe) {

      this.setProperty(
          "de.griefed.serverpackcreator.configuration.directories.serverpacks", "server-packs");
      tempDir = "server-packs";

    } finally {

      if (tempDir != null && !tempDir.isEmpty() && new File(tempDir).isDirectory()) {
        this.setProperty(
            "de.griefed.serverpackcreator.configuration.directories.serverpacks", tempDir);
        this.directoryServerPacks = tempDir;

        try (OutputStream outputStream =
            Files.newOutputStream(this.SERVERPACKCREATOR_PROPERTIES.toPath())) {
          this.store(outputStream, null);
        } catch (IOException ex) {
          LOG.error("Couldn't write properties-file.", ex);
        }

      } else {
        this.directoryServerPacks = "server-packs";
      }
    }

    if (this.getProperty("de.griefed.serverpackcreator.configuration.fallbackmodslist") == null) {

      this.listFallbackMods = this.FALLBACK_CLIENTSIDE_MODS;
      LOG.debug("Fallbackmodslist property null. Using fallback: " + this.FALLBACK_CLIENTSIDE_MODS);

    } else if (this.getProperty("de.griefed.serverpackcreator.configuration.fallbackmodslist")
        .contains(",")) {

      this.listFallbackMods =
          new ArrayList<>(
              Arrays.asList(
                  this.getProperty(
                          "de.griefed.serverpackcreator.configuration.fallbackmodslist",
                          this.FALLBACK_MODS_DEFAULT_ASSTRING)
                      .split(",")));
      LOG.debug("Fallbackmodslist set to: " + this.listFallbackMods);

    } else {

      this.listFallbackMods =
          Collections.singletonList(
              (this.getProperty("de.griefed.serverpackcreator.configuration.fallbackmodslist")));
      LOG.debug("Fallbackmodslist set to: " + this.listFallbackMods);
    }

    // List of directories which can be excluded from server packs
    if (this.getProperty("de.griefed.serverpackcreator.configuration.directories.shouldexclude")
        == null) {

      this.directoriesToExclude = FALLBACK_DIRECTORIES_EXCLUDE;
      LOG.debug(
          "directories.shouldexclude-property null. Using fallback: "
              + this.directoriesToExclude);

    } else if (this.getProperty(
            "de.griefed.serverpackcreator.configuration.directories.shouldexclude")
        .contains(",")) {

      this.directoriesToExclude =
          new ArrayList<>(
              Arrays.asList(
                  this.getProperty(
                          "de.griefed.serverpackcreator.configuration.directories.shouldexclude",
                          FALLBACK_DIRECTORIES_EXCLUDE_ASSTRING)
                      .split(",")));
      LOG.debug("Directories to exclude set to: " + this.directoriesToExclude);

    } else {

      this.directoriesToExclude =
          Collections.singletonList(
              this.getProperty(
                  "de.griefed.serverpackcreator.configuration.directories.shouldexclude"));
      LOG.debug("Directories to exclude set to: " + this.directoriesToExclude);
    }

    // List of directories which should always be included in a server pack, no matter what the
    // users specify
    if (this.getProperty("de.griefed.serverpackcreator.configuration.directories.mustinclude")
        == null) {

      this.directoriesToInclude = FALLBACK_DIRECTORIES_INCLUDE;
      LOG.debug(
          "directories.mustinclude-property null. Using fallback: "
              + this.directoriesToInclude);

    } else if (this.getProperty(
            "de.griefed.serverpackcreator.configuration.directories.mustinclude")
        .contains(",")) {

      this.directoriesToInclude =
          new ArrayList<>(
              Arrays.asList(
                  this.getProperty(
                          "de.griefed.serverpackcreator.configuration.directories.mustinclude",
                          FALLBACK_DIRECTORIES_INCLUDE_ASSTRING)
                      .split(",")));
      LOG.debug(
          "Directories which must always be included set to: " + this.directoriesToInclude);

    } else {

      this.directoriesToInclude =
          Collections.singletonList(
              this.getProperty(
                  "de.griefed.serverpackcreator.configuration.directories.mustinclude"));
      LOG.debug(
          "Directories which must always be included set to: " + this.directoriesToInclude);
    }

    this.queueMaxDiskUsage =
        Integer.parseInt(
            getProperty("de.griefed.serverpackcreator.spring.artemis.queue.max_disk_usage", "90"));

    this.saveLoadedConfiguration =
        Boolean.parseBoolean(
            getProperty("de.griefed.serverpackcreator.configuration.saveloadedconfig", "false"));

    this.versioncheck_prerelease =
        Boolean.parseBoolean(
            getProperty("de.griefed.serverpackcreator.versioncheck.prerelease", "false"));

    this.aikarsFlags = this.getProperty("de.griefed.serverpackcreator.configuration.aikar");
  }

  /**
   * Properties file used by ServerPackCreator, containing the configuration for this instance of
   * it.
   *
   * @return {@link File} serverpackcreator.properties-file.
   * @author Griefed
   */
  public File SERVERPACKCREATOR_PROPERTIES() {
    return SERVERPACKCREATOR_PROPERTIES;
  }

  /**
   * Start script for server packs, used by Windows.
   *
   * @return {@link File} start.bat-file.
   * @author Griefed
   */
  public File START_SCRIPT_WINDOWS() {
    return START_SCRIPT_WINDOWS;
  }

  /**
   * Start script for server packs, used by UNIX/Linux.
   *
   * @return {@link File} start.sh-file.
   * @author Griefed
   */
  public File START_SCRIPT_LINUX() {
    return START_SCRIPT_LINUX;
  }

  /**
   * JVM args file used by Forge MC 1.17+
   *
   * @return {@link File} user_jvm_args.txt-file.
   * @author Griefed
   */
  public File USER_JVM_ARGS() {
    return USER_JVM_ARGS;
  }

  /**
   * String-list of fallback clientside-only mods.
   *
   * @return {@link String}-list of fallback clientside-only mods.
   * @author Griefed
   */
  public List<String> FALLBACK_CLIENTSIDE_MODS() {
    return FALLBACK_CLIENTSIDE_MODS;
  }

  /**
   * Default configuration-file for a server pack generation.
   *
   * @return {@link File} serverpackcreator.conf-file.
   * @author Griefed
   */
  public File DEFAULT_CONFIG() {
    return DEFAULT_CONFIG;
  }

  /**
   * Old configuration-file used for automated migration in case anyone upgrades from 1.x.
   *
   * @return {@link File} creator.conf-file.
   * @author Griefed
   */
  public File OLD_CONFIG() {
    return OLD_CONFIG;
  }

  /**
   * Default server.properties-file used by Minecraft servers.
   *
   * @return {@link File} server.properties-file.
   * @author Griefed
   */
  public File DEFAULT_SERVER_PROPERTIES() {
    return DEFAULT_SERVER_PROPERTIES;
  }

  /**
   * Default server-icon.png-file used by Minecraft servers.
   *
   * @return {@link File} server-icon.png-file.
   * @author Griefed
   */
  public File DEFAULT_SERVER_ICON() {
    return DEFAULT_SERVER_ICON;
  }

  /**
   * Minecraft version manifest-file.
   *
   * @return {@link File} minecraft-manifest.json-file.
   * @author Griefed
   */
  public File MINECRAFT_VERSION_MANIFEST() {
    return MINECRAFT_VERSION_MANIFEST;
  }

  /**
   * Forge version manifest-file.
   *
   * @return {@link File} forge-manifest.json-file.
   * @author Griefed
   */
  public File FORGE_VERSION_MANIFEST() {
    return FORGE_VERSION_MANIFEST;
  }

  /**
   * Fabric version manifest-file.
   *
   * @return {@link File} fabric-manifest.xml-file
   * @author Griefed
   */
  public File FABRIC_VERSION_MANIFEST() {
    return FABRIC_VERSION_MANIFEST;
  }

  /**
   * Fabric installer version manifest-file.
   *
   * @return {@link File} fabric-installer-manifest.xml-file.
   * @author Griefed
   */
  public File FABRIC_INSTALLER_VERSION_MANIFEST() {
    return FABRIC_INSTALLER_VERSION_MANIFEST;
  }

  /**
   * Quilt version manifest-file.
   *
   * @return {@link File} quilt-manifest.xml-file
   * @author Griefed
   */
  public File QUILT_VERSION_MANIFEST() {
    return QUILT_VERSION_MANIFEST;
  }

  /**
   * Quilt installer version manifest-file.
   *
   * @return {@link File} quilt-installer-manifest.xml-file.
   * @author Griefed
   */
  public File QUILT_INSTALLER_VERSION_MANIFEST() {
    return QUILT_INSTALLER_VERSION_MANIFEST;
  }

  /**
   * ServerPackCreator-database when running as a webservice.
   *
   * @return {@link File} serverpackcreator.db-file.
   * @author Griefed
   */
  public File SERVERPACKCREATOR_DATABASE() {
    return SERVERPACKCREATOR_DATABASE;
  }

  /**
   * Path to the Minecraft version manifest-file, as a file.
   *
   * @return {@link File} ./work/minecraft-manifest.json
   * @author Griefed
   */
  public File MINECRAFT_VERSION_MANIFEST_LOCATION() {
    return MINECRAFT_VERSION_MANIFEST_LOCATION;
  }

  /**
   * Path to the Forge version manifest-file, as a file.
   *
   * @return {@link File} ./work/forge-manifest.json
   * @author Griefed
   */
  public File FORGE_VERSION_MANIFEST_LOCATION() {
    return FORGE_VERSION_MANIFEST_LOCATION;
  }

  /**
   * Path to the Fabric version manifest-file, as a file.
   *
   * @return {@link File} ./work/fabric-manifest.xml
   * @author Griefed
   */
  public File FABRIC_VERSION_MANIFEST_LOCATION() {
    return FABRIC_VERSION_MANIFEST_LOCATION;
  }

  /**
   * Path to the Fabric installer version manifest-file, as a file.
   *
   * @return {@link File} ./work/fabric-installer-manifest.xml
   * @author Griefed
   */
  public File FABRIC_INSTALLER_VERSION_MANIFEST_LOCATION() {
    return FABRIC_INSTALLER_VERSION_MANIFEST_LOCATION;
  }

  /**
   * Path to the Quilt version manifest-file, as a file.
   *
   * @return {@link File} ./work/quilt-manifest.xml
   * @author Griefed
   */
  public File QUILT_VERSION_MANIFEST_LOCATION() {
    return QUILT_VERSION_MANIFEST_LOCATION;
  }

  /**
   * Path to the Quilt installer version manifest-file, as a file.
   *
   * @return {@link File} ./work/quilt-installer-manifest.xml
   * @author Griefed
   */
  public File QUILT_INSTALLER_VERSION_MANIFEST_LOCATION() {
    return QUILT_INSTALLER_VERSION_MANIFEST_LOCATION;
  }

  /**
   * Getter for the version of ServerPackCreator.<br>
   * If a JAR-file compiled from a release-job from a CI/CD-pipeline is used, it should contain a
   * VERSION.txt-file which contains the version of said release. If a non-release-version is used,
   * from a regular pipeline or local dev-build, then this will be set to <code>dev</code>.
   *
   * @return String. Returns the version of ServerPackCreator.
   * @author Griefed
   */
  public String SERVERPACKCREATOR_VERSION() {
    return SERVERPACKCREATOR_VERSION;
  }

  /**
   * String-array of modloaders supported by ServerPackCreator.
   *
   * @return {@link String}-array of modloaders supported by ServerPackCreator.
   * @author Griefed
   */
  public String[] SUPPORTED_MODLOADERS() {
    return SUPPORTED_MODLOADERS;
  }

  /**
   * Directory where server-files are stored in, for example the default server-icon and
   * server.properties.
   *
   * @author Griefed
   * @return {@link String} server-files directory.
   */
  public String DIRECTORY_SERVER_FILES() {
    return "server_files";
  }

  /**
   * Directory where plugins are stored in.
   *
   * @author Griefed
   * @return {@link String} plugins directory.
   */
  public String DIRECTORY_PLUGINS() {
    return "plugins";
  }

  /**
   * Getter for the directory in which the server packs are stored/generated in.
   *
   * @return String. Returns the directory in which the server packs are stored/generated in.
   * @author Griefed
   */
  public String getDirectoryServerPacks() {
    return directoryServerPacks;
  }

  /**
   * Getter for the fallback list of clientside-only mods.
   *
   * @return List String. Returns the fallback list of clientside-only mods.
   * @author Griefed
   */
  public List<String> getListFallbackMods() {
    return listFallbackMods;
  }

  /**
   * Getter for the default list of directories to include in a server pack.
   *
   * @return {@link List} {@link String} containing default directories to include in a server pack.
   * @author Griefed
   */
  public List<String> getDirectoriesToInclude() {
    return directoriesToInclude;
  }

  /**
   * Getter for the list of directories to exclude from server packs.
   *
   * @return List String. Returns the list of directories to exclude from server packs.
   * @author Griefed
   */
  public List<String> getDirectoriesToExclude() {
    return directoriesToExclude;
  }

  /**
   * Adder for the list of directories to exclude from server packs.
   *
   * @param entry String. The directory to add to the list of directories to exclude from server
   *     packs.
   * @author Griefed
   */
  public void addDirectoryToExclude(String entry) {
    if (!this.directoriesToExclude.contains(entry)
        && !this.directoriesToInclude.contains(entry)) {
      LOG.debug("Adding " + entry + " to list of files or directories to exclude.");
      this.directoriesToExclude.add(entry);
    }
  }

  /**
   * Getter for whether the last loaded configuration file should be saved to as well.
   *
   * @return Boolean. Whether the last loaded configuration file should be saved to as well.
   * @author Griefed
   */
  public boolean getSaveLoadedConfiguration() {
    return saveLoadedConfiguration;
  }

  /**
   * Getter for the maximum disk usage at which JMS/Artemis will stop storing queues on disk.
   *
   * @return Integer. The maximum disk usage at which JMS/Artemis will stop storing queues on disk.
   * @author Griefed
   */
  public int getQueueMaxDiskUsage() {
    return queueMaxDiskUsage;
  }

  /**
   * Getter for whether the search for available PreReleases is enabled or disabled.<br>
   * Depending on <code>de.griefed.serverpackcreator.versioncheck.prerelease</code>, returns <code>
   * true</code> if checks for available PreReleases are enabled, <code>false</code> if no checks
   * for available PreReleases should be made.
   *
   * @return Boolean. Whether checks for available PreReleases are enabled.
   * @author Griefed
   */
  public boolean checkForAvailablePreReleases() {
    return versioncheck_prerelease;
  }

  /**
   * Update the fallback clientside-only modlist of our <code>serverpackcreator.properties</code>
   * from the main-repository or one of its mirrors.
   *
   * @return <code>true</code> if the fallback-property was updated.
   * @author Griefed
   */
  public boolean updateFallback() {

    Properties properties;

    try (InputStream github =
        new URL(
                "https://raw.githubusercontent.com/Griefed/ServerPackCreator/main/backend/main/resources/serverpackcreator.properties")
            .openStream()) {

      properties = new Properties();
      properties.load(github);

    } catch (IOException e) {

      LOG.debug("GitHub could not be reached. Checking GitLab.", e);
      try (InputStream gitlab =
          new URL(
                  "https://gitlab.com/Griefed/ServerPackCreator/-/raw/main/backend/main/resources/serverpackcreator.properties")
              .openStream()) {

        properties = new Properties();
        properties.load(gitlab);

      } catch (IOException ex) {
        LOG.debug("GitLab could not be reached. Checking GitGriefed", ex);
        try (InputStream gitgriefed =
            new URL(
                    "https://git.griefed.de/Griefed/ServerPackCreator/-/raw/main/backend/main/resources/serverpackcreator.properties")
                .openStream()) {

          properties = new Properties();
          properties.load(gitgriefed);

        } catch (IOException exe) {
          LOG.debug("GitGriefed could not be reached.", exe);
          properties = null;
        }
      }
    }

    if (properties != null
        && !getProperty("de.griefed.serverpackcreator.configuration.fallbackmodslist")
            .equals(
                properties.getProperty(
                    "de.griefed.serverpackcreator.configuration.fallbackmodslist"))) {

      setProperty(
          "de.griefed.serverpackcreator.configuration.fallbackmodslist",
          properties.getProperty("de.griefed.serverpackcreator.configuration.fallbackmodslist"));

      try (OutputStream outputStream =
          Files.newOutputStream(this.SERVERPACKCREATOR_PROPERTIES.toPath())) {
        this.store(outputStream, null);
      } catch (IOException ex) {
        LOG.error("Couldn't write properties-file.", ex);
      }

      this.listFallbackMods =
          new ArrayList<>(
              Arrays.asList(
                  this.getProperty(
                          "de.griefed.serverpackcreator.configuration.fallbackmodslist",
                          this.FALLBACK_MODS_DEFAULT_ASSTRING)
                      .split(",")));
      LOG.debug("Fallbackmodslist set to: " + this.listFallbackMods);
      LOG.info("The fallback-list for clientside only mods has been updated.");
      return true;

    } else {
      LOG.info("No fallback-list updates available.");
      return false;
    }
  }

  /**
   * Get this configurations AikarsFlags
   *
   * @return {@link String} Aikars flags.
   */
  public String getAikarsFlags() {
    return aikarsFlags;
  }

  @Override
  public synchronized String toString() {
    return "ApplicationProperties{"
        + "SERVERPACKCREATOR_PROPERTIES="
        + SERVERPACKCREATOR_PROPERTIES
        + ", START_SCRIPT_WINDOWS="
        + START_SCRIPT_WINDOWS
        + ", START_SCRIPT_LINUX="
        + START_SCRIPT_LINUX
        + ", USER_JVM_ARGS="
        + USER_JVM_ARGS
        + ", FALLBACK_CLIENTSIDE_MODS="
        + FALLBACK_CLIENTSIDE_MODS
        + ", DEFAULT_CONFIG="
        + DEFAULT_CONFIG
        + ", OLD_CONFIG="
        + OLD_CONFIG
        + ", DEFAULT_SERVER_PROPERTIES="
        + DEFAULT_SERVER_PROPERTIES
        + ", DEFAULT_SERVER_ICON="
        + DEFAULT_SERVER_ICON
        + ", MINECRAFT_VERSION_MANIFEST="
        + MINECRAFT_VERSION_MANIFEST
        + ", FORGE_VERSION_MANIFEST="
        + FORGE_VERSION_MANIFEST
        + ", FABRIC_VERSION_MANIFEST="
        + FABRIC_VERSION_MANIFEST
        + ", FABRIC_INSTALLER_VERSION_MANIFEST="
        + FABRIC_INSTALLER_VERSION_MANIFEST
        + ", SERVERPACKCREATOR_DATABASE="
        + SERVERPACKCREATOR_DATABASE
        + ", MINECRAFT_VERSION_MANIFEST_LOCATION="
        + MINECRAFT_VERSION_MANIFEST_LOCATION
        + ", FORGE_VERSION_MANIFEST_LOCATION="
        + FORGE_VERSION_MANIFEST_LOCATION
        + ", FABRIC_VERSION_MANIFEST_LOCATION="
        + FABRIC_VERSION_MANIFEST_LOCATION
        + ", FABRIC_INSTALLER_VERSION_MANIFEST_LOCATION="
        + FABRIC_INSTALLER_VERSION_MANIFEST_LOCATION
        + ", directoryServerPacks='"
        + getDirectoryServerPacks()
        + '\''
        + ", listFallbackMods="
        + getListFallbackMods()
        + ", listDirectoriesExclude="
        + getDirectoriesToExclude()
        + ", listCheckAgainstNewEntry="
        + getDirectoriesToInclude()
        + ", queueMaxDiskUsage="
        + getQueueMaxDiskUsage()
        + ", saveLoadedConfiguration="
        + getSaveLoadedConfiguration()
        + ", serverPackCreatorVersion='"
        + SERVERPACKCREATOR_VERSION()
        + '\''
        + ", versioncheck_prerelease="
        + checkForAvailablePreReleases()
        + '}';
  }
}
