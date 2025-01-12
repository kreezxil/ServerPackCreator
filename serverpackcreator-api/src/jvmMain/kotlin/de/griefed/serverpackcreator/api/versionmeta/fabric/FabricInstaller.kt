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
package de.griefed.serverpackcreator.api.versionmeta.fabric

import de.griefed.serverpackcreator.api.utilities.common.Utilities
import org.w3c.dom.Document
import org.xml.sax.SAXException
import java.io.File
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL
import java.util.*
import javax.xml.parsers.ParserConfigurationException

/**
 * Information about the Fabric installer.
 *
 * @param installerManifest Fabric installer information
 * @param utilities         Commonly used utilities across ServerPackCreator.
 *
 * @author Griefed
 */
internal class FabricInstaller(
    private val installerManifest: File,
    private val utilities: Utilities
) {
    @Suppress("MemberVisibilityCanBePrivate")
    val installerUrlTemplate =
        "https://maven.fabricmc.net/net/fabricmc/fabric-installer/%s/fabric-installer-%s.jar"

    /**
     * Available installer versions for Fabric.
     */
    val installers: MutableList<String> = ArrayList(100)

    /**
     * Meta for the Fabric-Version-to-Installer-URL.
     * * key: [String] Fabric version.
     * * value: [URL] Fabric installer URL.
     */
    val installerUrlMeta = HashMap<String, URL>(100)

    @Suppress("MemberVisibilityCanBePrivate")
    var latestInstaller: String? = null
        private set

    @Suppress("MemberVisibilityCanBePrivate")
    var releaseInstaller: String? = null
        private set

    @Suppress("MemberVisibilityCanBePrivate")
    var latestInstallerUrl: URL? = null
        private set

    @Suppress("MemberVisibilityCanBePrivate")
    var releaseInstallerUrl: URL? = null
        private set

    /**
     * Update the Fabric installer version by parsing the Fabric installer manifest.
     *
     * @author Griefed
     */
    @Suppress("DuplicatedCode")
    @Throws(ParserConfigurationException::class, IOException::class, SAXException::class)
    fun update() {
        val document: Document = utilities.xmlUtilities.getXml(installerManifest)
        latestInstaller = document
            .getElementsByTagName("latest")
            .item(0)
            .childNodes
            .item(0)
            .nodeValue
        releaseInstaller = document
            .getElementsByTagName("release")
            .item(0)
            .childNodes
            .item(0)
            .nodeValue
        try {
            latestInstallerUrl = URL(installerUrlTemplate.format(latestInstaller, latestInstaller))
        } catch (ignored: MalformedURLException) {
        }
        try {
            releaseInstallerUrl = URL(installerUrlTemplate.format(releaseInstaller, releaseInstaller))
        } catch (ignored: MalformedURLException) {
        }
        installers.clear()
        for (i in 0 until document.getElementsByTagName("version").length) {
            installers.add(
                document
                    .getElementsByTagName("version")
                    .item(i)
                    .childNodes
                    .item(0)
                    .nodeValue
            )
        }
        installerUrlMeta.clear()
        for (version in installers) {
            try {
                installerUrlMeta[version] = installerUrl(version)
            } catch (ignored: MalformedURLException) {
            }
        }
    }

    /**
     * Acquire the URL for the given Fabric version.
     *
     * @param fabricInstallerVersion Fabric version.
     * @return URL to the installer for the given Fabric version.
     * @throws MalformedURLException if the URL could not be formed.
     * @author Griefed
     */
    @Throws(MalformedURLException::class)
    private fun installerUrl(fabricInstallerVersion: String) =
        URL(installerUrlTemplate.format(fabricInstallerVersion, fabricInstallerVersion))

    /**
     * Get the latest Fabric installer version.
     *
     * @return The latest Fabric installer version.
     * @author Griefed
     */
    fun latestInstallerVersion() = latestInstaller!!

    /**
     * Get the release Fabric installer version.
     *
     * @return The release Fabric installer version.
     * @author Griefed
     */
    fun releaseInstallerVersion() = releaseInstaller!!

    /**
     * Get the [URL] to the latest Fabric installer.
     *
     * @return URL to the latest Fabric installer.
     * @author Griefed
     */
    fun latestInstallerUrl() = latestInstallerUrl!!

    /**
     * Get the [URL] to the release Fabric installer.
     *
     * @return URL to the release Fabric installer.
     * @author Griefed
     */
    fun releaseInstallerUrl() = releaseInstallerUrl!!

    /**
     * Get the [URL] to the improved Fabric launcher for the given Minecraft and Fabric
     * versions.
     *
     * @param minecraftVersion Minecraft version.
     * @param fabricVersion    Fabric version.
     * @return URL to the improved Fabric launcher, wrapped in an [Optional].
     * @author Griefed
     */
    fun improvedLauncherUrl(
        minecraftVersion: String,
        fabricVersion: String
    ) = URL(
        "https://meta.fabricmc.net/v2/versions/loader/%s/%s/%s/server/jar".format(
            minecraftVersion,
            fabricVersion,
            releaseInstaller
        )
    )
}