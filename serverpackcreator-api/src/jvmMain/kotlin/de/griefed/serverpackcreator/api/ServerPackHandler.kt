/* Copyright (C) 2023  Griefed
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
package de.griefed.serverpackcreator.api

import de.griefed.serverpackcreator.api.modscanning.ModScanner
import de.griefed.serverpackcreator.api.utilities.SimpleStopWatch
import de.griefed.serverpackcreator.api.utilities.common.*
import de.griefed.serverpackcreator.api.versionmeta.VersionMeta
import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.model.ExcludeFileFilter
import net.lingala.zip4j.model.ZipParameters
import org.apache.logging.log4j.kotlin.logger
import java.awt.Image
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import java.net.MalformedURLException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import javax.imageio.ImageIO

/**
 * Everything revolving around creating a server pack. The intended workflow is to create a [PackConfig] and run
 * it through any of the available [ConfigurationHandler.checkConfiguration]-variants, and then call [run] with the
 * previously checked configuration model. You may run with an unchecked configuration model, but no guarantees or
 * promises, yes not even support, is given for running a model without checking it first.
 *
 * This class also gives you access to the methods which are responsible for creating the server pack, in case you want
 * to do things manually.
 *
 * The methods in question are:
 *  * [cleanupEnvironment] and [cleanupEnvironment]
 *  * [ApiPlugins.runPreZipExtensions]
 *  * [copyFiles] and [copyFiles]
 *  * [provideImprovedFabricServerLauncher] and [provideImprovedFabricServerLauncher] if Fabric is the chosen Modloader
 *  * [copyIcon] and [copyIcon]
 *  * [copyProperties] and [copyProperties]
 *  * [ApiPlugins.runPreZipExtensions]
 *  * [zipBuilder] and [zipBuilder]
 *  * [createStartScripts] and [createStartScripts]
 *  * [installServer] and [installServer]
 *  * [ApiPlugins.runPostGenExtensions]
 *
 * If you want to execute extensions, see
 * * [ApiPlugins.runPreGenExtensions]},
 * * [ApiPlugins.runPreZipExtensions]} and
 * * [ApiPlugins.runPostGenExtensions].
 *
 * @param apiProperties Base settings of ServerPackCreator needed for server pack generation, such as access to the
 * directories, script templates and so on.
 * @param versionMeta   Meta for modloader and version specific checks and information gathering, such as modloader
 * installer downloads.
 * @param utilities     Common utilities used across ServerPackCreator.
 * @param apiPlugins    Any addons which a user may want to execute during the generation of a server pack.
 * @param modScanner    In case a user enabled automatic sideness detection, this will exclude clientside-only mods
 * from a server pack.
 *
 * @author Griefed
 */
actual class ServerPackHandler actual constructor(
    private val apiProperties: ApiProperties,
    private val versionMeta: VersionMeta,
    private val utilities: Utilities,
    private val apiPlugins: ApiPlugins,
    private val modScanner: ModScanner
) : ServerPack<File, TreeSet<String>, TreeSet<File>>() {
    private val installerLog = logger("InstallerLogger")

    override fun getServerPackDestination(packConfig: Pack<*, *, *>): String {
        var serverPackToBe = File(packConfig.modpackDir).name + packConfig.serverPackSuffix
        serverPackToBe = utilities.stringUtilities.pathSecureText(serverPackToBe.replace(" ", "_"))
        return File(apiProperties.serverPacksDirectory, serverPackToBe).path
    }

    override fun run(packConfig: PackConfig): Boolean {
        val destination = getServerPackDestination(packConfig)/*
        * Check whether the server pack for the specified modpack already exists and whether overwrite is disabled.
        * If the server pack exists and overwrite is disabled, no new server pack will be generated.
        */
        if (!apiProperties.isServerPacksOverwriteEnabled && File(destination).exists()) {
            log.info("Server pack already exists and overwrite disabled.")
        } else {

            // Make sure no files from previously generated server packs interrupt us.
            cleanupEnvironment(true, destination)
            val generationStopWatch = SimpleStopWatch().start()
            try {
                File(destination).createDirectories(create = true, directory = true)
            } catch (ignored: IOException) {
            }
            apiPlugins.runPreGenExtensions(packConfig, destination)

            // Recursively copy all specified directories and files, excluding clientside-only mods, to server pack.
            copyFiles(packConfig)

            // If true, copy the server-icon.png from server_files to the server pack.
            if (packConfig.isServerIconInclusionDesired) {
                copyIcon(packConfig)
            } else {
                log.info("Not including servericon.")
            }

            // If true, copy the server.properties from server_files to the server pack.
            if (packConfig.isServerPropertiesInclusionDesired) {
                copyProperties(packConfig)
            } else {
                log.info("Not including server.properties.")
            }
            apiPlugins.runPreZipExtensions(packConfig, destination)

            // If true, create a ZIP-archive excluding the Minecraft server JAR of the server pack.
            if (packConfig.isZipCreationDesired) {

                /*
                * Create the start scripts for this server pack. Ignores custom SPC_JAVA_SPC setting if one
                * is present. This is because a ZIP-archive, if one is created, is supposed to be uploaded
                * to platforms like CurseForge. We must not have scripts with custom Java paths there.
                */
                createStartScripts(packConfig, false)
                zipBuilder(packConfig)
            } else {
                log.info("Not creating zip archive of serverpack.")
            }

            /*
             * If modloader is fabric, try and replace the old server-launch.jar with the new and improved
             * one which also downloads the Minecraft server.
             */
            if (packConfig.modloader.equals("Fabric", ignoreCase = true)) {
                provideImprovedFabricServerLauncher(packConfig)
            }

            /*
            * Create the start scripts for this server pack to be used for local testing.
            * The difference to the previous call is that these scripts respect the SPC_JAVA_SPC
            * placeholder setting, if the user has set one
            */
            createStartScripts(packConfig, true)

            // If true, Install the modloader software for the specified Minecraft version, modloader, modloader version
            if (packConfig.isServerInstallationDesired) {
                installServer(packConfig)
            } else {
                log.info("Not installing modded server.")
            }

            // Inform user about location of newly generated server pack.
            log.info("Server pack available at: $destination")
            log.info("Server pack archive available at: ${destination}_server_pack.zip")
            log.info("Done!")
            apiPlugins.runPostGenExtensions(packConfig, destination)
            log.debug("Generation took ${generationStopWatch.stop().getTime()}")
        }
        return true
    }

    override fun cleanupEnvironment(deleteZip: Boolean, destination: String) {
        log.info("Found old server pack at $destination. Cleaning up...")
        File(destination).deleteQuietly()
        if (deleteZip) {
            File(destination + "_server_pack.zip").deleteQuietly()
        }
    }

    override fun copyFiles(
        modpackDir: String,
        directoriesToCopy: ArrayList<String>,
        clientMods: List<String>,
        minecraftVersion: String,
        destination: String,
        modloader: String
    ) {
        try {
            File(destination).createDirectories()
        } catch (ex: IOException) {
            log.error("Failed to create directory $destination")
        }
        if (directoriesToCopy.size == 1 && directoriesToCopy[0] == "lazy_mode") {
            log.warn("!!!WARNING!!!WARNING!!!WARNING!!!WARNING!!!WARNING!!!WARNING!!!WARNING!!!")
            log.warn("Lazy mode specified. This will copy the WHOLE modpack to the server pack. No exceptions.")
            log.warn("You will not receive any support for a server pack generated this way.")
            log.warn("Do not open an issue on GitHub if this configuration errors or results in a broken server pack.")
            log.warn("!!!WARNING!!!WARNING!!!WARNING!!!WARNING!!!WARNING!!!WARNING!!!WARNING!!!")
            try {
                File(modpackDir).copyRecursively(File(destination), true)
            } catch (ex: IOException) {
                log.error("An error occurred copying the modpack to the server pack in lazy mode.", ex)
            }
        } else {
            val exclusions = TreeSet<String>()
            val serverPackFiles: MutableList<ServerPackFile> = ArrayList(100000)
            directoriesToCopy.removeIf {
                if (it.startsWith("!")) {
                    exclusions.add(it.substring(1))
                    return@removeIf true
                } else {
                    return@removeIf false
                }
            }
            for (directory in directoriesToCopy) {
                val clientDir = modpackDir + File.separator + directory
                val serverDir = destination + File.separator + directory
                log.info("Gathering $directory file(s) and folder(s).")
                if (directory.contains(";")) {

                    /*
                     * If a semicolon is found, it means a user specified a source/path/to_file.foo;destination/path/to_file.bar-combination
                     * for a file they specifically want to include in their server pack.
                     */
                    serverPackFiles.addAll(
                        getExplicitFiles(
                            directory.split(";").dropLastWhile { it.isEmpty() }.toTypedArray(),
                            modpackDir,
                            destination
                        )
                    )
                } else if (directory.startsWith("saves/")) {

                    /*
                    * Check whether the entry starts with saves, and if it does, change the destination path to NOT include
                    * saves in it, so when a world is specified inside the saves-directory, it is copied to the base-directory
                    * of the server pack, instead of a saves-directory inside the modpack.
                    */
                    serverPackFiles.addAll(getSaveFiles(clientDir, directory, destination))
                } else if (directory.startsWith("mods")) {

                    /*
                    * If the entry starts with mods, we need to run our checks for clientside-only mods as well as exclude any
                    * user-specified clientside-only mods from the list of mods in the mods-directory.
                    */
                    try {
                        File(serverDir).createDirectories()
                    } catch (ignored: IOException) {
                    }
                    for (mod in getModsToInclude(clientDir, clientMods, minecraftVersion, modloader)) {
                        serverPackFiles.add(
                            ServerPackFile(
                                mod, File(serverDir, mod.name)
                            )
                        )
                    }

                    /*
                    * The user wants to add files to the server pack based on a regex-filter.
                    * Every match will be added.
                    */
                } else if (directory.contains("==")) {
                    serverPackFiles.addAll(getRegexMatches(modpackDir, destination, directory))
                } else if (File(directory).isFile) {
                    serverPackFiles.add(
                        ServerPackFile(
                            File(directory), File(destination, File(directory).name)
                        )
                    )
                } else if (File(directory).isDirectory) {
                    serverPackFiles.addAll(getDirectoryFiles(directory, destination + File.separator + File(directory).name))
                } else {
                    serverPackFiles.addAll(getDirectoryFiles(clientDir, destination + File.separator + File(clientDir).name))
                }
            }
            log.info("Ensuring files and/or directories are properly excluded.")
            serverPackFiles.removeIf { serverPackFile: ServerPackFile ->
                excludeFileOrDirectory(
                    modpackDir, serverPackFile.sourceFile, exclusions
                )
            }
            log.info("Copying files to the server pack. This may take a while...")
            for (serverPackFile in serverPackFiles) {
                try {
                    serverPackFile.copy()
                } catch (ex: IOException) {
                    log.error(
                        "An error occurred trying to copy " + serverPackFile.sourceFile + " to " + serverPackFile.destinationFile + ".",
                        ex
                    )
                }
            }
        }
    }

    override fun provideImprovedFabricServerLauncher(
        minecraftVersion: String, fabricVersion: String, destination: String
    ) {
        val fileDestination = File(destination, "fabric-server-launcher.jar")
        if (versionMeta.fabric.launcherFor(minecraftVersion, fabricVersion).isPresent) {
            versionMeta.fabric.launcherFor(minecraftVersion, fabricVersion).get().copyTo(fileDestination)
            log.info("Successfully provided improved Fabric Server Launcher.")
            val text = """
                |If you are using this server pack on a managed server, meaning you can not execute scripts, please use the fabric-server-launcher.jar instead of the fabric-server-launch.jar. Note the extra "er" at the end of "launcher".
                |This is the improved Fabric Server Launcher, which will take care of downloading and installing the Minecraft server and any and all libraries needed for running the Fabric server.
                |
                |The downside of this method is the occasional incompatibility of mods with the Fabric version, as the new Fabric Server Launcher always uses the latest available Fabric version.
                |If a mod is incompatible with said latest Fabric version, contact the mod-author and ask them to remedy the situation.
                |The official Fabric Discord had the following to add to this:
                |    Fabric loader however is cross version, so unless there is a mod incompatibility (which usually involves the mod being broken / using non-api internals)
                |    there is no good reason to use anything but the latest. I.e. the latest loader on any Minecraft version works with the new server launcher.
            """.trimMargin()
            File(destination, "SERVER_PACK_INFO.txt").writeText(text)
        }
    }

    override fun copyIcon(destination: String, pathToServerIcon: String) {
        log.info("Copying server-icon.png...")
        val customIcon = File(destination, apiProperties.defaultServerIcon.name)
        if (File(pathToServerIcon).exists()) {
            try {
                val originalImage: BufferedImage = ImageIO.read(File(pathToServerIcon))
                if (originalImage.height == 64 && originalImage.width == 64) {
                    try {
                        File(pathToServerIcon).copyTo(customIcon, true)
                    } catch (e: IOException) {
                        log.error("An error occurred trying to copy the server-icon.", e)
                    }
                } else {
                    val scaledImage: Image = originalImage.getScaledInstance(64, 64, Image.SCALE_SMOOTH)
                    val outputImage = BufferedImage(
                        scaledImage.getWidth(null), scaledImage.getHeight(null), BufferedImage.TYPE_INT_ARGB
                    )
                    outputImage.graphics.drawImage(scaledImage, 0, 0, null)
                    try {
                        ImageIO.write(outputImage, "png", customIcon)
                    } catch (ex: IOException) {
                        log.error("Error scaling image.", ex)
                    }
                }
            } catch (ex: Exception) {
                log.error("Error reading server-icon image.", ex)
            }
        } else if (pathToServerIcon.isEmpty()) {
            log.info("No custom icon specified or the file doesn't exist.")
            apiProperties.defaultServerIcon.copyTo(customIcon, true)
        } else {
            log.error("The specified server-icon does not exist: $pathToServerIcon")
        }
    }

    override fun copyProperties(destination: String, pathToServerProperties: String) {
        log.info("Copying server.properties...")
        val customProperties = File(destination, apiProperties.defaultServerProperties.name)
        if (File(pathToServerProperties).exists()) {
            File(pathToServerProperties).copyTo(customProperties, true)
        } else if (pathToServerProperties.isEmpty()) {
            log.info("No custom properties specified or the file doesn't exist.")
            apiProperties.defaultServerProperties.copyTo(customProperties, true)
        } else {
            log.error("The specified server.properties does not exist: $pathToServerProperties")
        }
    }

    override fun createStartScripts(scriptSettings: HashMap<String, String>, destination: String, isLocal: Boolean) {
        for (template in apiProperties.scriptTemplates) {
            try {
                val fileEnding = template.toString().substring(template.toString().lastIndexOf(".") + 1)
                val destinationScript = File(destination, "start.$fileEnding")
                var scriptContent: String = template.readText()
                scriptContent = replacePlaceholders(isLocal, scriptContent, scriptSettings)
                destinationScript.writeText(scriptContent.replace("\r", ""))
            } catch (ex: Exception) {
                log.error("File not accessible: $template.", ex)
            }
        }
        try {
            val destinationVariables = File(destination, "variables.txt")
            var variablesContent = variables
            variablesContent = replacePlaceholders(isLocal, variablesContent, scriptSettings)
            destinationVariables.writeText(variablesContent.replace("\r", ""))
        } catch (ex: Exception) {
            log.error("File not accessible: ${File(destination, "variables.txt")}.", ex)
        }
    }

    override fun zipBuilder(
        minecraftVersion: String,
        includeServerInstallation: Boolean,
        destination: String,
        modloader: String,
        modloaderVersion: String
    ) {
        log.info("Creating zip archive of serverpack...")
        val zipParameters = ZipParameters()
        val filesToExclude: MutableList<File> = ArrayList(100)
        if (apiProperties.isZipFileExclusionEnabled) {
            for (entry in apiProperties.zipArchiveExclusions) {
                filesToExclude.add(
                    File(
                        destination,
                        entry.replace("MINECRAFT_VERSION", minecraftVersion).replace("MODLOADER", modloader)
                            .replace("MODLOADER_VERSION", modloaderVersion)
                    )
                )
            }
            val excludeFileFilter = ExcludeFileFilter { o: File -> filesToExclude.contains(o) }
            zipParameters.excludeFileFilter = excludeFileFilter
        } else {
            log.info("File exclusion from ZIP-archives deactivated.")
        }
        val comment = ("Server pack made with ServerPackCreator ${apiProperties.apiVersion} by Griefed.")
        zipParameters.isIncludeRootFolder = false
        zipParameters.fileComment = comment
        try {
            ZipFile("${destination}_server_pack.zip").use {
                it.addFolder(File(destination), zipParameters)
                it.comment = comment
            }
        } catch (ex: IOException) {
            log.error("There was an error during zip creation.", ex)
        }
        if (includeServerInstallation) {
            log.warn("!!!-------NOTE: The minecraft_server.jar will not be included in the zip-archive.-------!!!")
            log.warn("!!!-Mojang strictly prohibits the distribution of their software through third parties.-!!!")
            log.warn("!!!---Tell your users to execute the download scripts to get the Minecraft server jar.--!!!")
        }
        log.info("Finished creation of zip archive.")
    }

    override fun preInstallationCleanup(destination: String) {
        File(destination, "libraries").deleteQuietly()
        File(destination, "server.jar").deleteQuietly()
        File(destination, "forge-installer.jar").deleteQuietly()
        File(destination, "quilt-installer.jar").deleteQuietly()
        File(destination, "installer.log").deleteQuietly()
        File(destination, "forge-installer.jar.log").deleteQuietly()
        File(destination, "legacyfabric-installer.jar").deleteQuietly()
        File(destination, "run.bat").deleteQuietly()
        File(destination, "run.sh").deleteQuietly()
        File(destination, "user_jvm_args.txt").deleteQuietly()
        File(destination, "quilt-server-launch.jar").deleteQuietly()
        File(destination, "minecraft_server.1.16.5.jar").deleteQuietly()
        File(destination, "forge.jar").deleteQuietly()
    }

    override fun installServer(
        modLoader: String, minecraftVersion: String, modLoaderVersion: String, destination: String
    ) {
        if (!serverDownloadable(minecraftVersion, modLoader, modLoaderVersion)) {
            log.error("The servers for $minecraftVersion, $modLoader $modLoaderVersion are currently unreachable. Skipping server installation.")
            return
        }
        preInstallationCleanup(destination)
        val commandArguments: MutableList<String> = ArrayList(10)
        commandArguments.add(apiProperties.javaPath)
        commandArguments.add("-jar")
        var process: Process? = null
        when (modLoader) {
            "Fabric" -> {
                log.info { "Installing Fabric server." }
                installerLog.info("Starting Fabric installation.")
                if (versionMeta.fabric.installerFor(versionMeta.fabric.releaseInstaller()).isPresent) {
                    log.info("Fabric installer successfully downloaded.")
                    commandArguments.add(
                        versionMeta.fabric.installerFor(versionMeta.fabric.releaseInstaller()).get().absolutePath
                    )
                    commandArguments.add("server")
                    commandArguments.add("-mcversion")
                    commandArguments.add(minecraftVersion)
                    commandArguments.add("-loader")
                    commandArguments.add(modLoaderVersion)
                    commandArguments.add("-downloadMinecraft")
                } else {
                    log.error(
                        "Something went wrong during the installation of Fabric. Maybe the Fabric servers are down or unreachable? Skipping..."
                    )
                    return
                }
            }

            "Forge" -> {
                log.info { "Installing Forge server." }
                installerLog.info("Starting Forge installation.")
                if (versionMeta.forge.installerFor(modLoaderVersion, minecraftVersion).isPresent) {
                    log.info("Forge installer successfully downloaded.")
                    commandArguments.add(
                        versionMeta.forge.installerFor(modLoaderVersion, minecraftVersion).get().absolutePath
                    )
                    commandArguments.add("--installServer")
                } else {
                    log.error(
                        "Something went wrong during the installation of Forge. Maybe the Forge servers are down or unreachable? Skipping..."
                    )
                    return
                }
            }

            "Quilt" -> {
                log.info { "Installing Quilt server." }
                installerLog.info("Starting Quilt installation.")
                if (versionMeta.quilt.installerFor(versionMeta.quilt.releaseInstaller()).isPresent) {
                    log.info("Quilt installer successfully downloaded.")
                    commandArguments.add(
                        versionMeta.quilt.installerFor(versionMeta.quilt.releaseInstaller()).get().absolutePath
                    )
                    commandArguments.add("install")
                    commandArguments.add("server")
                    commandArguments.add(minecraftVersion)
                    commandArguments.add("--download-server")
                    commandArguments.add("--install-dir=.")
                } else {
                    log.error(
                        "Something went wrong during the installation of Quilt. Maybe the Quilt servers are down or unreachable? Skipping..."
                    )
                    return
                }
            }

            "LegacyFabric" -> {
                log.info { "Installing LegacyFabric server." }
                installerLog.info("Starting Legacy Fabric installation.")
                try {
                    if (versionMeta.legacyFabric.installerFor(versionMeta.legacyFabric.releaseInstaller()).isPresent) {
                        log.info("LegacyFabric installer successfully downloaded.")
                        commandArguments.add(
                            versionMeta.legacyFabric.installerFor(versionMeta.legacyFabric.releaseInstaller())
                                .get().absolutePath
                        )
                        commandArguments.add("server")
                        commandArguments.add("-mcversion")
                        commandArguments.add(minecraftVersion)
                        commandArguments.add("-loader")
                        commandArguments.add(modLoaderVersion)
                        commandArguments.add("-downloadMinecraft")
                    } else {
                        log.error(
                            "Something went wrong during the installation of LegacyFabric. Maybe the LegacyFabric servers are down or unreachable? Skipping..."
                        )
                        return
                    }
                } catch (ex: MalformedURLException) {
                    log.error("Couldn't acquire LegacyFabric installer URL.", ex)
                }
            }

            else -> log.error("Invalid modloader specified. Modloader must be either Forge, Fabric or Quilt. Specified: $modLoader")
        }
        try {
            log.info("Starting server installation for Minecraft $minecraftVersion, $modLoader $modLoaderVersion.")
            val processBuilder =
                ProcessBuilder(commandArguments).directory(File(destination).absoluteFile).redirectErrorStream(true)
            log.debug("ProcessBuilder command: ${processBuilder.command()}")
            log.debug("Executing in: ${File(destination)}")
            process = processBuilder.start()
            process.inputStream.use { input ->
                input.bufferedReader().use { buff ->
                    while (true) {
                        val line: String = buff.readLine() ?: break
                        installerLog.info(line)
                    }
                }
            }
            installerLog.info("Server for Minecraft $minecraftVersion, $modLoader $modLoaderVersion installed.")
            log.info("Server for Minecraft $minecraftVersion, $modLoader $modLoaderVersion installed.")
            log.info("For details regarding the installation of this modloader server, see logs/modloader_installer.log.")
        } catch (ex: IOException) {
            log.error(
                "Something went wrong during the installation of Forge. Maybe the Forge servers are down or unreachable? Skipping...",
                ex
            )
        } finally {
            try {
                process!!.destroy()
            } catch (ignored: Exception) {
            }
        }
        if (modLoader.equals("Forge", ignoreCase = true)) {
            try {
                val file = File(destination, "forge-$minecraftVersion-$modLoaderVersion.jar")
                if (file.exists()) {
                    file.copyTo(File(destination, "forge.jar"), true)
                    file.deleteQuietly()
                }
            } catch (ex: IOException) {
                log.error("Could not rename forge-$minecraftVersion-$modLoaderVersion.jar to forge.jar", ex)
            }
        }
        if (apiProperties.isServerPackCleanupEnabled) {
            postInstallCleanup(destination)
        } else {
            log.info("Server pack cleanup disabled.")
        }
    }

    override fun getExplicitFiles(
        combination: Array<String>, modpackDir: String, destination: String
    ): MutableList<ServerPackFile> {
        val serverPackFiles: MutableList<ServerPackFile> = ArrayList(100)
        if (File(modpackDir, combination[0]).isFile) {
            serverPackFiles.add(
                ServerPackFile(
                    File(modpackDir, combination[0]), File(destination, combination[1])
                )
            )
        } else if (File(modpackDir, combination[0]).isDirectory) {
            serverPackFiles.addAll(
                getDirectoryFiles(
                    modpackDir + File.separator + combination[0], destination + File.separator + combination[1]
                )
            )
        } else if (File(combination[0]).isFile) {
            serverPackFiles.add(
                ServerPackFile(
                    File(combination[0]), File(destination, combination[1])
                )
            )
        } else if (File(combination[0]).isDirectory) {
            serverPackFiles.addAll(
                getDirectoryFiles(
                    combination[0], destination + File.separator + combination[1]
                )
            )
        }
        return serverPackFiles
    }

    override fun getSaveFiles(clientDir: String, directory: String, destination: String): List<ServerPackFile> {
        val serverPackFiles: MutableList<ServerPackFile> = ArrayList(2000)
        try {
            Files.walk(Paths.get(clientDir)).use {
                for (path in it) {
                    try {
                        serverPackFiles.add(
                            ServerPackFile(
                                path,
                                Paths.get(destination + File.separator + directory.substring(6))
                                    .resolve(Paths.get(clientDir).relativize(path))
                            )
                        )
                    } catch (ex: UnsupportedOperationException) {
                        log.error("Couldn't gather file $path from directory $clientDir.", ex)
                    }
                }
            }
        } catch (ex: IOException) {
            log.error("An error occurred during the copy-procedure to the server pack.", ex)
        }
        return serverPackFiles
    }

    override fun getModsToInclude(
        modsDir: String, userSpecifiedClientMods: List<String>, minecraftVersion: String, modloader: String
    ): List<File> {
        log.info("Preparing a list of mods to include in server pack...")
        val filesInModsDir: Collection<File> = File(modsDir).filteredWalk(modFileEndings, FilterType.ENDS_WITH)
        val modsInModpack = TreeSet(filesInModsDir)
        val autodiscoveredClientMods: MutableList<File> = ArrayList(100)

        // Check whether scanning mods for sideness is activated.
        if (apiProperties.isAutoExcludingModsEnabled) {
            val scanningStopWatch = SimpleStopWatch().start()
            when (modloader) {
                "LegacyFabric", "Fabric" -> autodiscoveredClientMods.addAll(modScanner.fabricScanner.scan(filesInModsDir))

                "Forge" -> if (minecraftVersion.split(".").dropLastWhile { it.isEmpty() }
                        .toTypedArray()[1].toInt() > 12) {
                    autodiscoveredClientMods.addAll(modScanner.tomlScanner.scan(filesInModsDir))
                } else {
                    autodiscoveredClientMods.addAll(modScanner.annotationScanner.scan(filesInModsDir))
                }

                "Quilt" -> {
                    val discoMods = TreeSet<File>()
                    discoMods.addAll(modScanner.fabricScanner.scan(filesInModsDir))
                    discoMods.addAll(modScanner.quiltScanner.scan(filesInModsDir))
                    autodiscoveredClientMods.addAll(discoMods)
                    discoMods.clear()
                }
            }

            // Exclude scanned mods from copying if said functionality is enabled.
            excludeMods(autodiscoveredClientMods, modsInModpack)
            log.debug(
                "Scanning and excluding of ${filesInModsDir.size} mods took ${scanningStopWatch.stop().getTime()}"
            )
        } else {
            log.info("Automatic clientside-only mod detection disabled.")
        }

        // Exclude user-specified mods from copying.
        excludeUserSpecifiedMod(userSpecifiedClientMods, modsInModpack)
        return ArrayList(modsInModpack)
    }

    override fun getRegexMatches(modpackDir: String, destination: String, entry: String): List<ServerPackFile> {
        val serverPackFiles: MutableList<ServerPackFile> = ArrayList(100)
        if (entry.startsWith("==") && entry.length > 2) {
            regexWalk(
                File(modpackDir), destination, entry.substring(2).toRegex(), serverPackFiles
            )
        } else if (entry.contains("==") && entry.split("==").dropLastWhile { it.isEmpty() }.toTypedArray().size == 2) {
            val regexInclusion = entry.split("==").dropLastWhile { it.isEmpty() }.toTypedArray()
            if (File(modpackDir, regexInclusion[0]).isDirectory) {
                regexWalk(
                    File(
                        modpackDir, regexInclusion[0]
                    ), destination, regexInclusion[1].toRegex(), serverPackFiles
                )
            } else if (File(regexInclusion[0]).isDirectory) {
                regexWalk(
                    File(regexInclusion[0]), destination, regexInclusion[1].toRegex(), serverPackFiles
                )
            }
        }
        return serverPackFiles
    }

    override fun getDirectoryFiles(
        source: String, destination: String
    ): List<ServerPackFile> {
        val serverPackFiles: MutableList<ServerPackFile> = ArrayList(100)
        /*File(source).listFiles().forEach {
            val destFile = File(
                destination, (it.absolutePath.replace(File(source).absolutePath, ""))
            )
            serverPackFiles.add(
                ServerPackFile(
                    it,
                    destFile
                )
            )
        }*/
        try {
            Files.walk(Paths.get(source)).use {
                for (path in it) {
                    try {
                        val pathFile = path.toFile().absolutePath
                        val sourceFile = File(source).absolutePath
                        val destFile = File(
                            destination,pathFile.replace(sourceFile,"")
                        )
                        serverPackFiles.add(
                            ServerPackFile(
                                path.toFile(),
                                destFile
                            )
                        )
                    } catch (ex: UnsupportedOperationException) {
                        log.error("Couldn't gather file $path from directory $source.", ex)
                    }
                }
            }
        } catch (ex: IOException) {
            log.error("An error occurred gathering files to copy to the server pack.", ex)
        }

        /*try {
            Files.walk(Paths.get(source)).use {
                for (path in it) {
                    try {
                        serverPackFiles.add(
                            ServerPackFile(
                                path,
                                Paths.get(destination + File.separator + File(source).name)
                                    .resolve(Paths.get(source).relativize(path))
                            )
                        )
                    } catch (ex: UnsupportedOperationException) {
                        log.error("Couldn't gather file $path from directory $source.", ex)
                    }
                }
            }
        } catch (ex: IOException) {
            log.error("An error occurred gathering files to copy to the server pack.", ex)
        }*/
        return serverPackFiles
    }

    override fun excludeFileOrDirectory(
        modpackDir: String, fileToCheckFor: File, exclusions: TreeSet<String>
    ): Boolean {
        exclusions.addAll(apiProperties.directoriesToExclude)
        for (exclusion in exclusions) {

            // Exclude based on regex matches. Scary stuff.
            if (exclusion.contains("==")) {

                // Tell a user to use !==.* and watch the world burn, hehehe. No, don't do that.
                if (exclusion.startsWith("==") && fileToCheckFor.absolutePath.matches(
                        exclusion.substring(2).toRegex()
                    )
                ) {
                    log.debug("Excluding file/directory: ${fileToCheckFor.absolutePath}")
                    return true
                } else if (exclusion.split("==").dropLastWhile { it.isEmpty() }.toTypedArray().size == 2) {
                    val regexclusion = exclusion.split("==").dropLastWhile { it.isEmpty() }.toTypedArray()
                    var toMatch: String
                    if (File(modpackDir, regexclusion[0]).isDirectory) {
                        toMatch = fileToCheckFor.absolutePath.replace(
                            File(modpackDir, regexclusion[0]).absolutePath, ""
                        )
                        if (toMatch.startsWith(File.separator)) {
                            toMatch = toMatch.substring(1)
                        }
                        if (toMatch.matches(regexclusion[1].toRegex())) {
                            log.debug("Excluding file/directory: ${fileToCheckFor.absolutePath}")
                            return true
                        }
                    } else if (File(regexclusion[0]).isDirectory) {
                        toMatch = fileToCheckFor.absolutePath.replace(File(regexclusion[0]).absolutePath, "")
                        if (toMatch.startsWith(File.separator)) {
                            toMatch = toMatch.substring(1)
                        }
                        if (toMatch.matches(regexclusion[1].toRegex())) {
                            log.debug("Excluding file/directory: ${fileToCheckFor.absolutePath}")
                            return true
                        }
                    }
                    if (fileToCheckFor.absolutePath.startsWith(File(regexclusion[0]).absolutePath) && fileToCheckFor.absolutePath.replace(
                            File(regexclusion[0]).absolutePath, ""
                        ).matches(regexclusion[1].toRegex())
                    ) {
                        log.debug("Excluding file/directory: ${fileToCheckFor.absolutePath}")
                        return true
                    }
                }

                // Exclude files with a specific file-ending.
            } else if (exclusion.matches(ending) && fileToCheckFor.absolutePath.endsWith(exclusion)) {
                log.debug("Excluding file/directory: ${fileToCheckFor.absolutePath}")
                return true
                // Exclude specific file/directory inside modpack from server pack
            } else if (fileToCheckFor.absolutePath.startsWith(File(modpackDir, exclusion).absolutePath)) {
                log.debug("Excluding file/directory: ${fileToCheckFor.absolutePath}")
                return true
            }
        }
        return false
    }

    override fun serverDownloadable(mcVersion: String, modloader: String, modloaderVersion: String) = when (modloader) {
        "Fabric" -> utilities.webUtilities.isReachable(versionMeta.fabric.releaseInstallerUrl())

        "Forge" -> (versionMeta.forge.getForgeInstance(
            mcVersion, modloaderVersion
        ).isPresent && utilities.webUtilities.isReachable(
            versionMeta.forge.getForgeInstance(mcVersion, modloaderVersion).get().installerUrl
        ))

        "Quilt" -> utilities.webUtilities.isReachable(versionMeta.quilt.releaseInstallerUrl())

        "LegacyFabric" -> {
            try {
                utilities.webUtilities.isReachable(versionMeta.legacyFabric.releaseInstallerUrl())
            } catch (e: MalformedURLException) {
                false
            }
        }

        else -> false
    }

    override fun postInstallCleanup(destination: String) {
        log.info("Cleanup after modloader server installation.")
        File(destination, "fabric-installer.jar").deleteQuietly()
        File(destination, "forge-installer.jar").deleteQuietly()
        File(destination, "quilt-installer.jar").deleteQuietly()
        File(destination, "installer.log").deleteQuietly()
        File(destination, "forge-installer.jar.log").deleteQuietly()
        File(destination, "legacyfabric-installer.jar").deleteQuietly()
        File(destination, "run.bat").deleteQuietly()
        File(destination, "run.sh").deleteQuietly()
        File(destination, "user_jvm_args.txt").deleteQuietly()
    }

    override fun excludeMods(autodiscoveredClientMods: List<File>, modsInModpack: TreeSet<File>) {
        if (autodiscoveredClientMods.isNotEmpty()) {
            log.info("Automatically detected mods: ${autodiscoveredClientMods.size}")
            for (discoveredMod in autodiscoveredClientMods) {
                modsInModpack.removeIf {
                    if (it.name.contains(discoveredMod.name)) {
                        log.warn("Automatically excluding mod: ${discoveredMod.name}")
                        return@removeIf true
                    } else {
                        return@removeIf false
                    }
                }
            }
        } else {
            log.info("No clientside-only mods detected.")
        }
    }

    override fun excludeUserSpecifiedMod(userSpecifiedExclusions: List<String>, modsInModpack: TreeSet<File>) {
        if (userSpecifiedExclusions.isNotEmpty()) {
            log.info("Performing ${apiProperties.exclusionFilter}-type checks for user-specified clientside-only mod exclusion.")
            for (userSpecifiedExclusion in userSpecifiedExclusions) {
                exclude(userSpecifiedExclusion, modsInModpack)
            }
        } else {
            log.warn("User specified no clientside-only mods.")
        }
    }

    override fun regexWalk(
        source: File, destination: String, regex: Regex, serverPackFiles: MutableList<ServerPackFile>
    ) {
        var toMatch: String
        try {
            Files.walk(source.toPath()).use {
                for (path in it) {
                    toMatch = path.toFile().absolutePath.replace(source.absolutePath, "")
                    if (toMatch.startsWith(File.separator)) {
                        toMatch = toMatch.substring(1)
                    }
                    if (toMatch.matches(regex)) {
                        val add = Paths.get(destination + File.separator + source.name)
                            .resolve(source.toPath().relativize(path))
                        serverPackFiles.add(
                            ServerPackFile(
                                path, add
                            )
                        )
                        log.debug("Including through regex-match:")
                        log.debug("    SOURCE: $path")
                        log.debug("    DESTINATION: $add")
                    }
                }
            }
        } catch (ex: IOException) {
            log.error("Couldn't gather all files from ${source.absolutePath} for filter \"$regex\".", ex)
        }
    }

    override fun exclude(userSpecifiedExclusion: String, modsInModpack: TreeSet<File>) {
        modsInModpack.removeIf {
            val excluded: Boolean
            val check = it.name
            excluded = when (apiProperties.exclusionFilter) {
                ExclusionFilter.END -> check.endsWith(userSpecifiedExclusion)
                ExclusionFilter.CONTAIN -> check.contains(userSpecifiedExclusion)
                ExclusionFilter.REGEX -> check.matches(userSpecifiedExclusion.toRegex())
                ExclusionFilter.EITHER -> (check.startsWith(userSpecifiedExclusion) || check.endsWith(
                    userSpecifiedExclusion
                ) || check.contains(userSpecifiedExclusion) || check.matches(userSpecifiedExclusion.toRegex()))

                ExclusionFilter.START -> check.startsWith(userSpecifiedExclusion)
            }
            if (excluded) {
                log.debug("Removed ${it.name} as per user-specified check: $userSpecifiedExclusion")
            }
            excluded
        }
    }
}