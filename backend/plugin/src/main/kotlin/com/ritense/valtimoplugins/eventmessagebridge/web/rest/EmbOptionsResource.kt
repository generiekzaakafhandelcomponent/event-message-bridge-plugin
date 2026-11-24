/*
 * Copyright 2026 Ritense BV, the Netherlands.
 *
 * Licensed under EUPL, Version 1.2 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ritense.valtimoplugins.eventmessagebridge.web.rest

import com.fasterxml.jackson.databind.JsonNode
import com.ritense.plugin.domain.PluginConfigurationId
import com.ritense.plugin.service.PluginService
import com.ritense.valtimo.contract.annotation.SkipComponentScan
import com.ritense.valtimoplugins.eventmessagebridge.plugin.EventMessageBridgePlugin
import jakarta.persistence.EntityNotFoundException
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.util.UUID

/**
 * The EMB origins and event items for the process link editor. The editor calls these instead of EMB itself,
 * so EMB's API secret stays in GZAC: the plugin configuration's EMB is called server to server with it.
 */
@SkipComponentScan
@RestController
@RequestMapping(EmbOptionsResource.OPTIONS_URL)
class EmbOptionsResource(
    private val pluginService: PluginService,
) {
    @GetMapping("/event-origins")
    fun eventOrigins(
        @PathVariable configurationId: UUID,
    ): JsonNode = plugin(configurationId).eventOrigins()

    @GetMapping("/event-origins/{originId}/event-items")
    fun eventItems(
        @PathVariable configurationId: UUID,
        @PathVariable originId: UUID,
    ): JsonNode = plugin(configurationId).eventItems(originId)

    // a configuration of another plugin is not found here
    private fun plugin(configurationId: UUID): EventMessageBridgePlugin {
        val instance =
            try {
                pluginService.createInstance(PluginConfigurationId.existingId(configurationId))
            } catch (_: EntityNotFoundException) {
                null // createInstance loads the configuration with getReferenceById, which throws for an unknown id
            }
        return instance as? EventMessageBridgePlugin ?: throw ResponseStatusException(NOT_FOUND)
    }

    companion object {
        const val OPTIONS_URL = "/api/v1/plugin/event-message-bridge/configuration/{configurationId}"
    }
}
