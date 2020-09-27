package com.github.mrbean355.admiralbulldog.home

import com.github.mrbean355.admiralbulldog.APP_VERSION
import com.github.mrbean355.admiralbulldog.arch.AppViewModel
import com.github.mrbean355.admiralbulldog.arch.logAnalyticsProperties
import com.github.mrbean355.admiralbulldog.arch.repo.DiscordBotRepository
import com.github.mrbean355.admiralbulldog.assets.SoundBites
import com.github.mrbean355.admiralbulldog.common.*
import com.github.mrbean355.admiralbulldog.discord.DiscordBotScreen
import com.github.mrbean355.admiralbulldog.game.monitorGameStateUpdates
import com.github.mrbean355.admiralbulldog.installation.InstallationWizard
import com.github.mrbean355.admiralbulldog.mod.ChooseModTypeScreen
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import com.github.mrbean355.admiralbulldog.persistence.DotaMod
import com.github.mrbean355.admiralbulldog.persistence.DotaPath
import com.github.mrbean355.admiralbulldog.persistence.GameStateIntegration
import com.github.mrbean355.admiralbulldog.settings.UpdateViewModel
import com.github.mrbean355.admiralbulldog.sounds.ViewSoundTriggersScreen
import com.github.mrbean355.admiralbulldog.sounds.sync.SyncSoundBitesScreen
import javafx.beans.binding.BooleanBinding
import javafx.beans.binding.StringBinding
import javafx.beans.property.StringProperty
import javafx.scene.control.ButtonType
import kotlinx.coroutines.launch
import tornadofx.*
import tornadofx.error
import kotlin.concurrent.timer
import kotlin.system.exitProcess

private const val HEARTBEAT_FREQUENCY_MS = 30 * 1_000L
private const val ANALYTICS_FREQUENCY_MS = 5 * 60 * 1_000L

class MainViewModel : AppViewModel() {
    private val discordBotRepository = DiscordBotRepository()
    private val updateViewModel by inject<UpdateViewModel>()
    private val hasHeardFromDota = booleanProperty(false)

    val heading: StringBinding = hasHeardFromDota.stringBinding {
        if (it == true) getString("msg_connected") else getString("msg_not_connected")
    }
    val progressBarVisible: BooleanBinding = hasHeardFromDota.not()
    val infoMessage: StringBinding = hasHeardFromDota.stringBinding {
        if (it == true) getString("dsc_connected") else getString("dsc_not_connected")
    }
    val version: StringProperty = stringProperty(getString("lbl_app_version", APP_VERSION.value, getDistributionName()))

    override fun onReady() {
        sendHeartbeats()

        ensureValidDotaPath()
        ensureGsiInstalled()

        checkForNewSounds()
        checkForAppUpdate()

        monitorGameStateUpdates {
            runLater {
                hasHeardFromDota.set(true)
            }
        }
    }

    override fun onUndock() {
        updateViewModel.onUndock()
        super.onUndock()
    }

    private fun ensureValidDotaPath() {
        if (DotaPath.hasValidSavedPath()) {
            return
        }
        find<InstallationWizard>(scope = Scope()).openModal(block = true, resizable = false)
        if (!DotaPath.hasValidSavedPath()) {
            error(getString("install_header"), getString("msg_installer_fail"))
            exitProcess(-1)
        }
    }

    private fun ensureGsiInstalled() {
        val alreadyInstalled = GameStateIntegration.isInstalled()
        GameStateIntegration.install()
        if (!alreadyInstalled) {
            information(getString("install_header"), getString("msg_installer_success"), ButtonType.FINISH)
        }
    }

    fun onChangeSoundsClicked() {
        find<ViewSoundTriggersScreen>().openModal(resizable = false)
    }

    fun onDiscordBotClicked() {
        find<DiscordBotScreen>().openModal(resizable = false)
    }

    fun onDotaModClicked() {
        find<ChooseModTypeScreen>().openModal(resizable = false)
    }

    fun onDiscordCommunityClicked() {
        hostServices.showDocument(URL_DISCORD_SERVER_INVITE)
    }

    fun onTelegramChannelClicked() {
        hostServices.showDocument(URL_TELEGRAM_CHANNEL)
    }

    private fun checkForNewSounds() {
        if (updateViewModel.shouldCheckForNewSounds()) {
            find<SyncSoundBitesScreen>().openModal(escapeClosesWindow = false, block = true, resizable = false)
        } else {
            SoundBites.checkForInvalidSounds()
        }
    }

    private fun checkForAppUpdate() {
        if (!updateViewModel.shouldCheckForAppUpdate()) {
            checkForModUpdate()
            return
        }
        coroutineScope.launch {
            updateViewModel.checkForAppUpdate(
                    onError = { checkForModUpdate() },
                    onUpdateSkipped = { checkForModUpdate() },
                    onNoUpdate = { checkForModUpdate() }
            )
        }
    }

    private fun checkForModUpdate() {
        if (ConfigPersistence.isModEnabled()) {
            val modUninstalled = !ConfigPersistence.isModTempDisabled() && !DotaMod.isModInGameInfoFile()
            DotaMod.onModEnabled()
            if (modUninstalled) {
                // If the user has enabled the mod, but it isn't in the game info file, warn them.
                warning(getString("header_mod_uninstalled"), getString("content_mod_missing_from_game_info"))
            }
            if (updateViewModel.shouldCheckForModUpdate()) {
                coroutineScope.launch {
                    updateViewModel.checkForModUpdate()
                }
            }
        } else {
            DotaMod.onModDisabled()
        }
    }

    private fun sendHeartbeats() {
        timer(daemon = true, period = HEARTBEAT_FREQUENCY_MS) {
            coroutineScope.launch {
                discordBotRepository.sendHeartbeat()
            }
        }
        timer(daemon = true, period = ANALYTICS_FREQUENCY_MS) {
            coroutineScope.launch {
                logAnalyticsProperties()
            }
        }
    }
}