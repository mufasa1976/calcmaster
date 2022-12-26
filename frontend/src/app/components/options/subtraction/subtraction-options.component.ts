import { Component, EventEmitter, Input, Output } from '@angular/core';
import { initialSubtractionProperties, SubtractionProperties } from "../../../../shared/subtraction-properties";
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
    let transgression = this.properties.transgression;
    if (maxDifference <= 10 && transgression > -1) {
      transgression = -1;
    }
    if (maxDifference <= 20) {
      subtrahendRounding = 1;
    }
    if (maxDifference <= 100) {
      if (subtrahendRounding > 10) {
        subtrahendRounding = 10;
      }
      if (transgression > 2) {
        transgression = 2;
      }
    }
    if (maxDifference <= 1000) {
      if (subtrahendRounding > 100) {
        subtrahendRounding = 100;
      }
      if (transgression > 6) {
        transgression = 6;
      }
    }
    if (subtrahendRounding > 1 || maxDifference > 10000) {
      transgression = -1;
    }

    let minDifference = this.properties.minDifference;
    if (minDifference < 20) {
      minDifference = 0;
    }

    this._propertiesEmitter.emit({
      ...this.properties,
      minDifference: minDifference,
      maxDifference: maxDifference,
      subtrahendRounding: subtrahendRounding,
      transgression: transgression
    })
  }

  get subtrahendRounding() {
    return this.properties.subtrahendRounding;
  }

  set subtrahendRounding(subtrahendRounding: number) {
    let includeZeroOnOperand = this.properties.includeZeroOnOperand;
    let transgression = this.properties.transgression;
    if (subtrahendRounding > 1) {
      includeZeroOnOperand = false;
      transgression = -1;
    }
    this._propertiesEmitter.emit({
      ...this.properties,
      subtrahendRounding: subtrahendRounding,
      includeZeroOnOperand: includeZeroOnOperand,
      transgression: transgression
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

  get transgression() {
    return this.properties.transgression;
  }

  set transgression(transgression: number) {
    this._propertiesEmitter.emit({
      ...this.properties,
      transgression: transgression
    })
  }
}
