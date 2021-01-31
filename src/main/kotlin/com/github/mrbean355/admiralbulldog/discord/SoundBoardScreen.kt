/*
 * Copyright 2021 Michael Johnston
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.mrbean355.admiralbulldog.discord

import com.github.mrbean355.admiralbulldog.assets.SoundBite
import com.github.mrbean355.admiralbulldog.common.PADDING_LARGE
import com.github.mrbean355.admiralbulldog.common.PADDING_MEDIUM
import com.github.mrbean355.admiralbulldog.common.PADDING_SMALL
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.common.rateSpinner
import javafx.collections.ObservableList
import javafx.event.EventTarget
import javafx.geometry.Pos.CENTER_LEFT
import javafx.scene.control.ButtonBar.ButtonData.NEXT_FORWARD
import javafx.scene.control.Tooltip
import javafx.scene.layout.FlowPane
import tornadofx.Fragment
import tornadofx.Scope
import tornadofx.action
import tornadofx.attachTo
import tornadofx.button
import tornadofx.buttonbar
import tornadofx.hbox
import tornadofx.label
import tornadofx.managedWhen
import tornadofx.onChange
import tornadofx.paddingAll
import tornadofx.paddingRight
import tornadofx.paddingVertical
import tornadofx.spacer
import tornadofx.vbox
import tornadofx.visibleWhen
import tornadofx.whenUndocked

class SoundBoardScreen : Fragment(getString("title_sound_board")) {
    private val viewModel by inject<SoundBoardViewModel>(Scope())

    override val root = vbox(spacing = PADDING_SMALL) {
        paddingAll = PADDING_MEDIUM
        label(getString("label_sound_board_description"))
        label(getString("label_sound_board_empty")) {
            visibleWhen(viewModel.isEmpty)
            managedWhen(visibleProperty())
        }
        hbox(alignment = CENTER_LEFT) {
            paddingVertical = PADDING_SMALL
            label(getString("label_playback_speed")) {
                paddingRight = PADDING_LARGE
            }
            rateSpinner(viewModel.playbackRate)
        }
        soundBoard(viewModel.soundBoard, viewModel::onSoundClicked)
        spacer {
            prefHeight = PADDING_SMALL
        }
        buttonbar {
            button(getString("btn_customise"), NEXT_FORWARD) {
                action { onChooseSoundsClicked() }
            }
        }
    }

    init {
        whenUndocked {
            viewModel.onUndock()
        }
    }

    private fun onChooseSoundsClicked() {
        find<ConfigureSoundBoardScreen>().openModal(block = true, resizable = false)
        viewModel.refresh()
    }
}

private fun EventTarget.soundBoard(items: ObservableList<SoundBite>, onClick: (SoundBite) -> Unit): FlowPane {
    return FlowPane(PADDING_SMALL, PADDING_SMALL).apply {
        addButtons(items, onClick)
        items.onChange {
            addButtons(items, onClick)
        }
    }.attachTo(this)
}

private fun FlowPane.addButtons(items: ObservableList<SoundBite>, onClick: (SoundBite) -> Unit) {
    children.clear()
    items.forEach {
        button(it.name) {
            tooltip = Tooltip(getString("tooltip_play_through_discord"))
            action { onClick(it) }
        }
    }
    scene?.window?.sizeToScene()
}
