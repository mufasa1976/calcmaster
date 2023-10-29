import { Component, EventEmitter, Input, Output } from '@angular/core';
import { initialSubtractionProperties, SubtractionProperties } from "../../../../shared/subtraction-properties";
import { MatCheckboxChange } from "@angular/material/checkbox";
import { UNLIMITED_TRANSACTIONS } from "../../../../shared/constants";

@Component({
  selector: 'app-subtraction-options',
  templateUrl: './subtraction-options.component.html',
  styleUrls: ['./subtraction-options.component.scss']
})
export class SubtractionOptionsComponent {
  @Input("properties") properties: SubtractionProperties = { ...initialSubtractionProperties };
  @Output("propertiesChange") private _propertiesEmitter = new EventEmitter<SubtractionProperties>();

  readonly ONE = 1 << 0;
  readonly TEN = 1 << 1;
  readonly HUNDRED = 1 << 2;
  readonly THOUSAND = 1 << 3;
  readonly TENTHOUSAND = 1 << 4;

  constructor() {
  }

  get maxDifference() {
    return this.properties.maxDifference;
  }

  set maxDifference(maxDifference: number) {
    let subtrahendRounding = this.properties.subtrahendRounding;
    let transgression = this.properties.transgression;
    if (maxDifference <= 20) {
      subtrahendRounding = 1;
    } else if (maxDifference <= 100 && subtrahendRounding > 10) {
      subtrahendRounding = 10;
    } else if (maxDifference <= 1000 && subtrahendRounding > 100) {
      subtrahendRounding = 100;
    }

    let minSubtrahend = this.properties.minSubtrahend;
    if (maxDifference <= 20 && minSubtrahend > 1) {
      minSubtrahend = 1;
    } else if (maxDifference <= 100 && minSubtrahend > 10) {
      minSubtrahend = 10;
    } else if (maxDifference <= 1000 && minSubtrahend > 100) {
      minSubtrahend = 100;
    } else if (maxDifference <= 10000 && minSubtrahend > 1000) {
      minSubtrahend = 1000;
    } else if (maxDifference <= 100000 && minSubtrahend > 10000) {
      minSubtrahend = 10000;
    }

    if (maxDifference < 20 && transgression > UNLIMITED_TRANSACTIONS) {
      transgression = UNLIMITED_TRANSACTIONS;
    } else if (maxDifference <= 100 && transgression >= this.ONE) {
      transgression &= this.ONE;
    } else if (maxDifference <= 1000 && transgression > this.TEN) {
      transgression &= this.ONE | this.TEN;
    } else if (maxDifference <= 10000 && transgression > this.HUNDRED) {
      transgression &= this.ONE | this.TEN | this.HUNDRED;
    } else if (maxDifference <= 100000 && transgression > this.THOUSAND) {
      transgression &= this.ONE | this.TEN | this.HUNDRED | this.THOUSAND;
    }
    if (subtrahendRounding > 1) {
      transgression = UNLIMITED_TRANSACTIONS;
    }

    this._propertiesEmitter.emit({
      ...this.properties,
      maxDifference: maxDifference,
      subtrahendRounding: subtrahendRounding,
      transgression: transgression,
      minSubtrahend: minSubtrahend
    })
  }

  get minSubtrahend() {
    return this.properties.minSubtrahend;
  }

  set minSubtrahend(minSubtrahend: number) {
    this._propertiesEmitter.emit({
      ...this.properties,
      minSubtrahend: minSubtrahend
    })
  }

  get subtrahendRounding() {
    return this.properties.subtrahendRounding;
  }

  set subtrahendRounding(subtrahendRounding: number) {
    let transgression = this.properties.transgression;
    let minSubtrahend = this.properties.minSubtrahend;
    if (subtrahendRounding === 0 || subtrahendRounding > 1) {
      transgression = -1;
      if (minSubtrahend > 1) {
        minSubtrahend = 1;
      }
    }
    this._propertiesEmitter.emit({
      ...this.properties,
      subtrahendRounding: subtrahendRounding,
      transgression: transgression,
      minSubtrahend: minSubtrahend
    })
  }

  get transgression() {
    return this.properties.transgression;
  }

  changeUnlimitedTransgression(event: MatCheckboxChange) {
    const maxDifference = this.properties.maxDifference;
    let transgression = this.ONE;
    if (maxDifference > 100) {
      transgression |= this.TEN;
    }
    if (maxDifference > 1000) {
      transgression |= this.HUNDRED;
    }
    if (maxDifference > 10000) {
      transgression |= this.THOUSAND;
    }
    if (maxDifference > 100000) {
      transgression |= this.TENTHOUSAND;
    }
    this._propertiesEmitter.emit({
      ...this.properties,
      transgression: event.checked ? transgression : -1
    })
  }

  isTransgression(digit: number): boolean {
    return (this.properties.transgression & digit) === digit;
  }

  setTransgression(digit: number) {
    this._propertiesEmitter.emit({
      ...this.properties,
      transgression: this.properties.transgression ^ digit,
    })
  }
}
