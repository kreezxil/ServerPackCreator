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
package de.griefed.serverpackcreator.api.plugins.swinggui

import de.griefed.serverpackcreator.api.PackConfig

/**
 * Force every server pack configuration tab to provide a certain set of methods. Said set of methods give plugins
 * access to various configurable values, such as
 * * the modpack directory
 * * the list of clientside-only mods
 * * the list of files and folders to include or exclude
 * and more.
 * @author Griefed
 */
@Suppress("unused")
interface ServerPackConfigTab {
    fun setClientSideMods(entries: MutableList<String>)
    fun setCopyDirectories(entries: MutableList<String>)
    fun setIconInclusionTicked(ticked: Boolean)
    fun setJavaArguments(javaArguments: String)
    fun setMinecraftVersion(version: String)
    fun setModloader(modloader: String)
    fun setModloaderVersion(version: String)
    fun setModpackDirectory(directory: String)
    fun setPropertiesInclusionTicked(ticked: Boolean)
    fun setScriptVariables(variables: HashMap<String, String>)
    fun setServerIconPath(path: String)
    fun setServerInstallationTicked(ticked: Boolean)
    fun setServerPackSuffix(suffix: String)
    fun setServerPropertiesPath(path: String)
    fun setZipArchiveCreationTicked(ticked: Boolean)

    fun getClientSideMods(): String
    fun getClientSideModsList(): MutableList<String>
    fun getCopyDirectories(): String
    fun getCopyDirectoriesList(): MutableList<String>
    fun getCurrentConfiguration(): PackConfig
    fun getJavaArguments(): String
    fun getMinecraftVersion(): String
    fun getModloader(): String
    fun getModloaderVersion(): String
    fun getModpackDirectory(): String
    fun getScriptSettings(): HashMap<String, String>
    fun getServerIconPath(): String
    fun getServerPackSuffix(): String
    fun getServerPropertiesPath(): String

    fun isMinecraftServerAvailable(): Boolean
    fun isServerInstallationTicked(): Boolean
    fun isServerIconInclusionTicked(): Boolean
    fun isServerPropertiesInclusionTicked(): Boolean
    fun isZipArchiveCreationTicked(): Boolean

    fun clearScriptVariables()
    fun setAikarsFlagsAsJavaArguments()
    fun validateInputFields()
    fun acquireRequiredJavaVersion(): String
}
