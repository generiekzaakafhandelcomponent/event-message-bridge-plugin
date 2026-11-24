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

import com.ritense.plugin.domain.PluginConfigurationId
import com.ritense.plugin.service.PluginService
import jakarta.persistence.EntityNotFoundException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.web.server.ResponseStatusException
import java.util.UUID

class EmbOptionsResourceTest {
    private val pluginService = mock<PluginService>()
    private val resource = EmbOptionsResource(pluginService)

    @Test
    fun `should return 404 for an unknown configuration`() {
        whenever(pluginService.createInstance(any<PluginConfigurationId>())).thenThrow(EntityNotFoundException())

        val error = assertThrows<ResponseStatusException> { resource.eventOrigins(UUID.randomUUID()) }

        assertEquals(NOT_FOUND, error.statusCode)
    }

    @Test
    fun `should return 404 for a configuration of another plugin`() {
        whenever(pluginService.createInstance(any<PluginConfigurationId>())).thenReturn(Any())

        val error = assertThrows<ResponseStatusException> { resource.eventOrigins(UUID.randomUUID()) }

        assertEquals(NOT_FOUND, error.statusCode)
    }
}
