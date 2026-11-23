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

package com.ritense.valtimoplugins.eventmessagebridge.plugin

import com.ritense.plugin.annotation.Plugin
import com.ritense.plugin.annotation.PluginAction
import com.ritense.plugin.annotation.PluginActionProperty
import com.ritense.plugin.annotation.PluginProperty
import com.fasterxml.jackson.databind.JsonNode
import com.ritense.processlink.domain.ActivityTypeWithEventName.MESSAGE_START_EVENT_START
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.web.client.RestClient
import java.util.UUID

/**
 * Starts a case when the Event Message Bridge (EMB) receives an event for one of its event items.
 * The key must equal the pluginId in the plugin's frontend specification.
 */
@Plugin(
    key = "event-message-bridge",
    title = "Event Message Bridge",
    description = "Starts a case when the Event Message Bridge receives an event",
)
open class EventMessageBridgePlugin {
    @PluginProperty(key = "embAppUrl", secret = false)
    lateinit var embAppUrl: String

    /** Sent to EMB in the [API_KEY_HEADER] header; must equal EMB's EMB_API_SECRET. Stored encrypted, never sent to the browser. */
    @PluginProperty(key = "embApiSecret", secret = true)
    lateinit var embApiSecret: String

    /** EMB's event origins (id, name, type, description), for the origin select in the process link editor. */
    open fun eventOrigins(): JsonNode = getFromEmb("/api/v1/event-origins")

    /** The event items of one EMB origin, for the event item select in the process link editor. */
    open fun eventItems(originId: UUID): JsonNode = getFromEmb("/api/v1/event-origins/$originId/event-items")

    private fun getFromEmb(path: String): JsonNode =
        restClient.get()
            .uri(embAppUrl.trimEnd('/') + path)
            .header(API_KEY_HEADER, embApiSecret)
            .retrieve()
            .body(JsonNode::class.java)!!

    /**
     * Never runs a process: the process link itself is the subscription. EventCaseStartService
     * starts the linked process when EMB reports an event for [eventItemId].
     */
    @PluginAction(
        key = RECEIVE_EVENT_ACTION_KEY,
        title = "Receive EMB event",
        description = "Starts a case of this process when the Event Message Bridge receives an event for an event item",
        activityTypes = [MESSAGE_START_EVENT_START],
    )
    open fun receiveEvent(
        // Only used to prefill the origin select in the process link editor; matching uses eventItemId.
        @PluginActionProperty originId: String?,
        @PluginActionProperty eventItemId: String?,
    ) {
        logger.debug { "Receive EMB event action registered for event item $eventItemId" }
    }

    companion object {
        // Specific to this plugin: links are looked up by action key alone.
        const val RECEIVE_EVENT_ACTION_KEY = "receive-emb-event"
        const val API_KEY_HEADER = "X-EMB-API-Key"
        private val restClient =
            RestClient.builder()
                .requestFactory(SimpleClientHttpRequestFactory().apply {
                    setConnectTimeout(5_000)
                    setReadTimeout(10_000)
                })
                .build()
        private val logger = KotlinLogging.logger {}
    }
}
