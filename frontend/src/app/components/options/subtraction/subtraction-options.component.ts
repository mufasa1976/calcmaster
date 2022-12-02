import { Component, EventEmitter, Input, Output } from '@angular/core';
import { initialSubtractionProperties, SubtractionProperties } from "../../../../shared/subtractionProperties";
import { MatSlideToggleChange } from "@angular/material/slide-toggle";

@Component({
  selector: 'app-subtraction-options',
  templateUrl: './subtraction-options.component.html',
  styleUrls: ['./subtraction-options.component.scss']
})
export class SubtractionOptionsComponent {
  @Input("properties") properties: SubtractionProperties = { ...initialSubtractionProperties };
  @Output("propertiesChange") private _propertiesEmitter = new EventEmitter<SubtractionProperties>();

  constructor() {
  }

  get minDifference() {
    return this.properties.minDifference;
  }

  set minDifference(minDifference: number) {
    this._propertiesEmitter.emit({
      ...this.properties,
      minDifference: minDifference
    })
  }

  get maxDifference() {
    return this.properties.maxDifference;
  }

  set maxDifference(maxDifference: number) {
    let subtrahendRounding = this.properties.subtrahendRounding;
    if (maxDifference <= 20) {
      subtrahendRounding = 1;
    }
    if (maxDifference <= 100 && subtrahendRounding === 100) {
      subtrahendRounding = 10;
    }
    if (maxDifference <= 1000 && subtrahendRounding === 1000) {
      subtrahendRounding = 100;
    }

    let minDifference = this.properties.minDifference;
    if (minDifference < 20) {
      minDifference = 0;
    }

    this._propertiesEmitter.emit({
      ...this.properties,
      minDifference: minDifference,
      maxDifference: maxDifference,
      subtrahendRounding: subtrahendRounding
    })
  }

  get subtrahendRounding() {
    return this.properties.subtrahendRounding;
  }

  set subtrahendRounding(subtrahendRounding: number) {
    let includeZeroOnOperand = this.properties.includeZeroOnOperand;
    if (subtrahendRounding > 1) {
      includeZeroOnOperand = false;
    }
    this._propertiesEmitter.emit({
      ...this.properties,
      subtrahendRounding: subtrahendRounding,
      includeZeroOnOperand: includeZeroOnOperand
    })
  }

  get includeZeroOnOperand() {
    return this.properties.includeZeroOnOperand;
  }

  toggleIncludeZeroOnOperand(event: MatSlideToggleChange) {
    this._propertiesEmitter.emit({
      ...this.properties,
      includeZeroOnOperand: event.checked
    })
  }
}
