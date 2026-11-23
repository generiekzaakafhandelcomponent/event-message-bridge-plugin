# Release notes

Overzicht van wijzigingen per versie van de Event Message Bridge-plugin.

## 1.0.0

Eerste versie.

- Actie **EMB-event ontvangen** (`receive-emb-event`) op een message start event: de proceskoppeling abonneert het
  proces op een event item van de Event Message Bridge.
- Endpoint `POST /api/v1/plugin/event-message-bridge/event` waarmee de Event Message Bridge een ontvangen event meldt.
  Voor elke koppeling op het event item van het event wordt een dossier gestart; het antwoord bevat per dossier de
  business key (het id van het dossierdocument).
- Een event wordt maar één keer verwerkt: het event-id wordt vastgelegd in de tabel `emb_processed_event`. Een event
  waarvoor geen dossier gestart is, wordt niet vastgelegd, zodat de EMB het opnieuw kan sturen zodra de koppeling
  klopt. Een vastgelegd event zonder gestarte dossiers geldt niet als dubbel.
- Een verkeerd ingestelde koppeling wordt overgeslagen met een melding in de log, zonder de andere koppelingen te
  blokkeren.
- Configuratie met de URL van de EMB-applicatie (`embAppUrl`) en het EMB API-secret (`embApiSecret`, versleuteld
  opgeslagen en nooit naar de browser gestuurd).
- In de proceskoppeling worden bron en event item gekozen uit lijsten. GZAC haalt die op bij de EMB met het API-secret,
  via `GET /api/v1/plugin/event-message-bridge/configuration/{configurationId}/event-origins` en
  `…/event-origins/{originId}/event-items` (alleen voor beheerders).
