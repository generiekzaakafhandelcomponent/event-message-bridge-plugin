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

import {PluginSpecification} from "@valtimo/plugin";
import {EventMessageBridgeConfigurationComponent} from "./components/event-message-bridge-configuration/event-message-bridge-configuration.component";
import {ReceiveEmbEventConfigurationComponent} from "./components/receive-emb-event-configuration/receive-emb-event-configuration.component";
import {EVENT_MESSAGE_BRIDGE_PLUGIN_LOGO_BASE64} from "./assets";

const eventMessageBridgePluginSpecification: PluginSpecification = {
  pluginId: "event-message-bridge",
  pluginConfigurationComponent: EventMessageBridgeConfigurationComponent,
  pluginLogoBase64: EVENT_MESSAGE_BRIDGE_PLUGIN_LOGO_BASE64,
  functionConfigurationComponents: {
    "receive-emb-event": ReceiveEmbEventConfigurationComponent,
  },
  pluginTranslations: {
    nl: {
      title: "Event Message Bridge",
      description: "Start een zaak wanneer de Event Message Bridge een event ontvangt.",
      configurationTitle: "Configuratienaam",
      configurationTitleTooltip:
        "De naam van de huidige plugin-configuratie. Onder deze naam kan de configuratie in de rest van de applicatie teruggevonden worden.",
      embAppUrl: "EMB-applicatie URL",
      embAppUrlTooltip: "De URL van de Event Message Bridge-applicatie.",
      embApiSecret: "EMB API-secret",
      embApiSecretTooltip:
        "Het secret waarmee GZAC de bronnen en event items van de Event Message Bridge ophaalt (EMB_API_SECRET van de EMB-applicatie). Laat leeg bij wijzigen om het opgeslagen secret te behouden.",
      "receive-emb-event": "EMB-event ontvangen",
      receiveEmbEventDescription:
        "Start een zaak van dit proces bij dit message start event wanneer de Event Message Bridge een event voor het gekozen event item ontvangt.",
      originId: "Bron",
      originIdTooltip: "De bron (event origin) in de Event Message Bridge waaruit het event item komt.",
      eventItemId: "Event item",
      eventItemIdTooltip: "Het event item van de gekozen bron waarvoor een zaak van dit proces gestart wordt.",
    },
    en: {
      title: "Event Message Bridge",
      description: "Starts a case when the Event Message Bridge receives an event.",
      configurationTitle: "Configuration name",
      configurationTitleTooltip:
        "The name of the current plugin configuration. Under this name, the configuration can be found in the rest of the application.",
      embAppUrl: "EMB application URL",
      embAppUrlTooltip: "The URL of the Event Message Bridge application.",
      embApiSecret: "EMB API secret",
      embApiSecretTooltip:
        "The secret GZAC uses to fetch the origins and event items from the Event Message Bridge (EMB_API_SECRET of the EMB application). Leave empty when editing to keep the stored secret.",
      "receive-emb-event": "Receive EMB event",
      receiveEmbEventDescription:
        "Starts a case of this process at this message start event when the Event Message Bridge receives an event for the chosen event item.",
      originId: "Origin",
      originIdTooltip: "The event origin in the Event Message Bridge the event item belongs to.",
      eventItemId: "Event item",
      eventItemIdTooltip: "The event item of the chosen origin for which a case of this process is started.",
    },
  },
};

export {eventMessageBridgePluginSpecification};
