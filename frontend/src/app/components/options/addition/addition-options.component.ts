import { Component, EventEmitter, Input, Output } from '@angular/core';
import { AdditionProperties, initialAdditionProperties } from "../../../../shared/addition-properties";
import { MatSlideToggleChange } from "@angular/material/slide-toggle";
import { UNLIMITED_TRANSACTIONS } from "../../../../shared/calculation-properties";

@Component({
  selector: 'app-addition-options',
  templateUrl: './addition-options.component.html',
  styleUrls: ['./addition-options.component.scss']
})
export class AdditionOptionsComponent {
  @Input("properties") properties: AdditionProperties = { ...initialAdditionProperties };
  @Output("propertiesChange") private _propertiesEmitter = new EventEmitter<AdditionProperties>();

  constructor() {}

  get minSum() {
    return this.properties.minSum;
  }

  set minSum(minSum: number) {
    this._propertiesEmitter.emit({
      ...this.properties,
      minSum: minSum
    })
  }

  get maxSum() {
    return this.properties.maxSum;
  }

  set maxSum(maxSum: number) {
    let secondAddendRounding = this.properties.secondAddendRounding;
    let transgression = this.properties.transgression;
    if (maxSum <= 10 && transgression > UNLIMITED_TRANSACTIONS) {
      transgression = UNLIMITED_TRANSACTIONS;
    }
    if (maxSum <= 20) {
      secondAddendRounding = 1;
    }
    if (maxSum <= 100) {
      if (secondAddendRounding > 10) {
        secondAddendRounding = 10;
      }
      if (transgression > 2) {
        transgression = 2;
      }
    }
    if (maxSum <= 1000) {
      if (secondAddendRounding > 100) {
        secondAddendRounding = 100;
      }
      if (transgression > 6) {
        transgression = 6;
      }
    }
    if (secondAddendRounding > 1 || maxSum > 10000) {
      transgression = UNLIMITED_TRANSACTIONS;
    }

    let minSum = this.properties.minSum;
    if (maxSum < 20 || transgression > UNLIMITED_TRANSACTIONS) {
      minSum = 0;
    }

    this._propertiesEmitter.emit({
      ...this.properties,
      minSum: minSum,
      maxSum: maxSum,
      secondAddendRounding: secondAddendRounding,
      transgression: transgression
    })
  }

  get secondAddendRounding() {
    return this.properties.secondAddendRounding;
  }

  set secondAddendRounding(secondAddendRounding: number) {
    let includeZeroOnOperand = this.properties.includeZeroOnOperand;
    let transgression = this.properties.transgression;
    if (secondAddendRounding > 1) {
      includeZeroOnOperand = false;
      transgression = -1;
    }
    this._propertiesEmitter.emit({
      ...this.properties,
      secondAddendRounding: secondAddendRounding,
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
    let minSum = this.properties.minSum;
    if (transgression > -1) {
      minSum = 0;
    }
    this._propertiesEmitter.emit({
      ...this.properties,
      minSum: minSum,
      transgression: transgression
    })
  }
}
