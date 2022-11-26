import { Component, OnInit } from '@angular/core';
import { ThemeService } from "../services/theme.service";
import { Observable } from "rxjs";
import { Theme, ThemeName } from "../../shared/theme";
import { MatButtonToggleChange } from "@angular/material/button-toggle";
import { CalculationProperties } from "../../shared/calculationProperties";
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

  private _title = "Arbeitsblätter Mathematik";
  private _operators = new Set<string>();

  calculationProperties: CalculationProperties = {
    operators: [],
    additionProperties: {
      minSum: 0,
      maxSum: 10,
      secondAddendRounding: 1,
      includeZeroOnOperand: true,
    },
    subtractionProperties: {
      minDifference: 0,
      maxDifference: 10,
      subtrahendRounding: 1,
      includeZeroOnOperand: true,
    },
    multiplicationProperties: {
      maxProduct: 100,
      exclusions: [],
      fixedMultiplicands: []
    },
    divisionProperties: {
      maxDividend: 100,
      maxRemainder: 0,
      exclusions: [],
      fixedDivisors: []
    },
    roundingProperties: {},
    toggleHide: true,
    numberOfCalculations: 15,
    verticalDisplay: false
  }
  fileEnding: string = "pdf"

  constructor(
    private readonly _titleService: Title,
    private readonly _themeService: ThemeService,
    private readonly _calculationService: CalculationService) {
    this.themes$ = _themeService.getThemes();
    this._title = $localize`Arbeitsblätter Mathematik`;
    this._titleService.setTitle(this.title);
  }

  ngOnInit(): void {
    this._themeService.setTheme(ThemeName.INDIGO_PINK);
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
    return _.isEmpty(this._operators);
  }

  generate() {
    let subscription = this._calculationService.generate({
      ...this.calculationProperties,
      operators: [...this._operators]
    }, this.fileEnding).subscribe(response => {
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
      subscription.unsubscribe();
    });
  }
}
