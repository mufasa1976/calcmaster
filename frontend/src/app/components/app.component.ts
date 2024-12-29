import { Component, OnInit } from '@angular/core';
import { ThemeService } from "../services/theme.service";
import {BehaviorSubject, Observable, Subscription, tap} from "rxjs";
import { DEFAULT_THEME, Theme, ThemeName } from "../../shared/theme";
import { MatButtonToggleChange } from "@angular/material/button-toggle";
import { CalculationProperties, initialCalculationProperties } from "../../shared/calculation-properties";
import { CalculationService } from "../services/calculation.service";
import * as _ from "lodash";
import { saveAs } from "file-saver";
import { Title } from "@angular/platform-browser";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})
export class AppComponent implements OnInit {
  readonly themes$: Observable<Array<Theme>>;

  readonly ADD = "ADD";
  readonly SUBTRACT = "SUBTRACT";
  readonly MULTIPLY = "MULTIPLY";
  readonly DIVIDE = "DIVIDE";
  readonly ROUND = "ROUND";
  readonly CONVERT = "CONVERT";

  private _title = "Arbeitsblätter Mathematik";
  private _operators = new Set<string>();
  private _generationInProgress = false;

  calculationProperties: CalculationProperties = { ...initialCalculationProperties };
  fileEnding: string = "pdf";

  constructor(
    private readonly _titleService: Title,
    private readonly _themeService: ThemeService,
    private readonly _calculationService: CalculationService) {
    this.themes$ = _themeService.getThemes();
    this._title = $localize`Arbeitsblätter Mathematik`;
    this._titleService.setTitle(this.title);
  }

  ngOnInit(): void {
    this._themeService.setTheme(DEFAULT_THEME);
  }

  get title(): string {
    return this._title;
  }

  changeTheme(themeName: ThemeName) {
    this._themeService.setTheme(themeName);
  }

  get logo() {
    return this._themeService.getCurrentLogo();
  }

  toggleButton(change: MatButtonToggleChange) {
    if (change.source.checked) {
      this._operators.add(change.value);
    } else {
      this._operators.delete(change.value);
    }
  }

  notChecked(operator: string): boolean {
    return !this._operators.has(operator);
  }

  generateButtonDisable() {
    return _.isEmpty(this._operators) || this._generationInProgress;
  }

  generate() {
    this._generationInProgress = true;
    let subscription = this._calculationService.generate({
      ...this.calculationProperties,
      operators: [...this._operators]
    }, this.fileEnding).subscribe({
      next: response => {
        if (response.body) {
          const contentDisposition = response.headers.get("content-disposition");
          let fileName: string | undefined = undefined;
          if (contentDisposition && contentDisposition.split(';')[0] === "attachment") {
            fileName = contentDisposition.split(';')[1].split('=')[1].trim().replace(/["]+/g, '');
          }
          if (fileName) {
            saveAs(response.body, fileName);
          } else {
            saveAs(response.body);
          }
        }
      },
      error: error => this._unsubscribe(subscription),
      complete: () => this._unsubscribe(subscription)
    }) as Subscription;
  }

  private _unsubscribe(subscription: Subscription) {
    this._generationInProgress = false;
    subscription.unsubscribe();
  }
}
