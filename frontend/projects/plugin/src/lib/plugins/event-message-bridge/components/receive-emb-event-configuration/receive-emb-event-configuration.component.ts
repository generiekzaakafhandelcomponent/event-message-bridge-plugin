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

import {Component, EventEmitter, inject, Input, OnDestroy, OnInit, Output} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {FunctionConfigurationComponent, FunctionConfigurationData} from "@valtimo/plugin";
import {SelectItem} from "@valtimo/components";
import {ConfigService} from "@valtimo/shared";
import {
  BehaviorSubject,
  catchError,
  combineLatest,
  distinctUntilChanged,
  filter,
  map,
  Observable,
  of,
  shareReplay,
  startWith,
  Subject,
  Subscription,
  switchMap,
  take,
} from "rxjs";
import {EventMessageBridgePluginConfig, ReceiveEmbEventConfig} from "../../models";

interface EmbOption {
  id: string;
  name: string;
}

@Component({
  standalone: false,
  selector: "valtimo-receive-emb-event-configuration",
  templateUrl: "./receive-emb-event-configuration.component.html",
})
export class ReceiveEmbEventConfigurationComponent implements FunctionConfigurationComponent, OnInit, OnDestroy {
  @Input() save$!: Observable<void>;
  @Input() disabled$!: Observable<boolean>;
  @Input() pluginId!: string;
  @Input() prefillConfiguration$!: Observable<ReceiveEmbEventConfig>;
  @Input() selectedPluginConfigurationData$!: Observable<EventMessageBridgePluginConfig>;
  @Output() valid: EventEmitter<boolean> = new EventEmitter<boolean>();
  @Output() configuration: EventEmitter<FunctionConfigurationData> = new EventEmitter<FunctionConfigurationData>();

  private saveSubscription!: Subscription;
  private readonly formValue$ = new BehaviorSubject<ReceiveEmbEventConfig | null>(null);
  private readonly valid$ = new BehaviorSubject<boolean>(false);
  private readonly originId$ = new BehaviorSubject<string | null>(null);
  public readonly clearEventItem$ = new Subject<void>();

  // GZAC calls EMB with the API secret of the selected plugin configuration; the browser never holds it.
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${inject(ConfigService).config.valtimoApi.endpointUri}v1/plugin/event-message-bridge/configuration`;

  private optionsUrl$!: Observable<string>;
  public origins$!: Observable<SelectItem[] | null>;
  public eventItems$!: Observable<SelectItem[] | null>;

  public ngOnInit(): void {
    this.optionsUrl$ = this.selectedPluginConfigurationData$.pipe(
      map(config => config?.configurationId),
      filter((id): id is string => !!id),
      map(id => `${this.apiUrl}/${encodeURIComponent(id)}`),
      distinctUntilChanged(),
      shareReplay(1)
    );
    // null while loading
    this.origins$ = this.optionsUrl$.pipe(
      switchMap(url => this.getOptions(`${url}/event-origins`).pipe(startWith(null))),
      shareReplay(1)
    );
    this.eventItems$ = combineLatest([this.optionsUrl$, this.originId$]).pipe(
      switchMap(([url, originId]) =>
        originId
          ? this.getOptions(`${url}/event-origins/${encodeURIComponent(originId)}/event-items`).pipe(startWith(null))
          : of([])
      ),
      shareReplay(1)
    );
    this.openSaveSubscription();
  }

  public ngOnDestroy() {
    this.saveSubscription?.unsubscribe();
  }

  public formValueChange(formValue: ReceiveEmbEventConfig): void {
    this.formValue$.next(formValue);
    const previousOriginId = this.originId$.value;
    if (formValue.originId !== previousOriginId) {
      // An event item of the previous origin no longer applies; skipped for the first (prefill) value.
      if (previousOriginId) {
        this.clearEventItem$.next();
      }
      this.originId$.next(formValue.originId || null);
    }
    // Required: a message start event link without an event item never starts a case.
    const valid = !!formValue.originId && !!formValue.eventItemId;
    this.valid$.next(valid);
    this.valid.emit(valid);
  }

  private getOptions(url: string): Observable<SelectItem[]> {
    return this.http.get<EmbOption[]>(url).pipe(
      map(options => options.map(option => ({id: option.id, text: option.name}))),
      catchError(error => {
        console.error(`Could not load options from the Event Message Bridge via ${url}`, error);
        return of([]);
      })
    );
  }

  private openSaveSubscription(): void {
    this.saveSubscription = this.save$
      ?.pipe(
        switchMap(() => combineLatest([this.formValue$, this.valid$]).pipe(take(1)))
      )
      .subscribe(([formValue, valid]) => {
        if (valid) {
          this.configuration.emit(formValue!);
        }
      });
  }
}
