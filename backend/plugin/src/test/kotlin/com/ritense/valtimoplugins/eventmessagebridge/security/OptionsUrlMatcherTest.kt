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

package com.ritense.valtimoplugins.eventmessagebridge.security

import com.ritense.valtimoplugins.eventmessagebridge.web.rest.EmbOptionsResource.Companion.OPTIONS_URL
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.http.HttpMethod.GET
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher

/** The security rule uses OPTIONS_URL as is: its {configurationId} must match any one path segment. */
class OptionsUrlMatcherTest {
    private val matcher = antMatcher(GET, "$OPTIONS_URL/**")

    @Test
    fun `should match the options endpoints of any configuration`() {
        assertTrue(matcher.matches(get("/api/v1/plugin/event-message-bridge/configuration/4ac9a49f-4aeb-4f91-86d9-853fd95a1b33/event-origins")))
        assertTrue(matcher.matches(get("/api/v1/plugin/event-message-bridge/configuration/abc/event-origins/def/event-items")))
    }

    @Test
    fun `should not match other paths`() {
        assertFalse(matcher.matches(get("/api/v1/plugin/event-message-bridge/event")))
    }

    private fun get(path: String) = MockHttpServletRequest("GET", path).apply { servletPath = path }
}
