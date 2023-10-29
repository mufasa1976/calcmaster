import { Component, EventEmitter, Input, Output } from '@angular/core';
import { AdditionProperties, initialAdditionProperties } from "../../../../shared/addition-properties";
import { UNLIMITED_TRANSACTIONS } from "../../../../shared/constants";
import { MatCheckboxChange } from "@angular/material/checkbox";

@Component({
  selector: 'app-addition-options',
  templateUrl: './addition-options.component.html',
  styleUrls: ['./addition-options.component.scss']
})
export class AdditionOptionsComponent {
  @Input("properties") properties: AdditionProperties = { ...initialAdditionProperties };
  @Output("propertiesChange") private _propertiesEmitter = new EventEmitter<AdditionProperties>();

  readonly ONE = 1 << 0;
  readonly TEN = 1 << 1;
  readonly HUNDRED = 1 << 2;
  readonly THOUSAND = 1 << 3;
  readonly TENTHOUSAND = 1 << 4;

  constructor() {}

  get maxSum() {
    return this.properties.maxSum;
  }

  set maxSum(maxSum: number) {
    let secondAddendRounding = this.properties.secondAddendRounding;
    let transgression = this.properties.transgression;
    if (maxSum <= 20) {
      secondAddendRounding = 1;
    } else if (maxSum <= 100 && secondAddendRounding > 10) {
      secondAddendRounding = 10;
    } else if (maxSum <= 1000 && secondAddendRounding > 100) {
      secondAddendRounding = 100;
    }

    let minOperand = this.properties.minOperand;
    if (maxSum <= 20 && minOperand > 1) {
      minOperand = 1;
    } else if (maxSum <= 100 && minOperand > 10) {
      minOperand = 10;
    } else if (maxSum <= 1000 && minOperand > 100) {
      minOperand = 100;
    } else if (maxSum <= 10000 && minOperand > 1000) {
      minOperand = 1000;
    } else if (maxSum <= 100000 && minOperand > 10000) {
      minOperand = 10000;
    }

    if (maxSum < 20 && transgression > UNLIMITED_TRANSACTIONS) {
      transgression = UNLIMITED_TRANSACTIONS;
    } else if (maxSum <= 100 && transgression >= this.ONE) {
      transgression &= this.ONE;
    } else if (maxSum <= 1000 && transgression > this.TEN) {
      transgression &= this.ONE | this.TEN;
    } else if (maxSum <= 10000 && transgression > this.HUNDRED) {
      transgression &= this.ONE | this.TEN | this.HUNDRED;
    } else if (maxSum <= 100000 && transgression > this.THOUSAND) {
      transgression &= this.ONE | this.TEN | this.HUNDRED | this.THOUSAND;
    }
    if (secondAddendRounding > 1) {
      transgression = UNLIMITED_TRANSACTIONS;
    }

    this._propertiesEmitter.emit({
      ...this.properties,
      maxSum: maxSum,
      secondAddendRounding: secondAddendRounding,
      transgression: transgression,
      minOperand: minOperand
    })
  }

  get secondAddendRounding() {
    return this.properties.secondAddendRounding;
  }

  set secondAddendRounding(secondAddendRounding: number) {
    let transgression = this.properties.transgression;
    let minOperand = this.properties.minOperand;
    if (secondAddendRounding === 0 || secondAddendRounding > 1) {
      transgression = -1;
      if (minOperand > 1) {
        minOperand = 1;
      }
    }
    this._propertiesEmitter.emit({
      ...this.properties,
      secondAddendRounding: secondAddendRounding,
      transgression: transgression,
      minOperand: minOperand
    })
  }

  get transgression() {
    return this.properties.transgression;
  }

  changeUnlimitedTransgression(event: MatCheckboxChange) {
    const maxSum = this.properties.maxSum;
    let transgression = this.ONE;
    if (maxSum > 100) {
      transgression |= this.TEN;
    }
    if (maxSum > 1000) {
      transgression |= this.HUNDRED;
    }
    if (maxSum > 10000) {
      transgression |= this.THOUSAND;
    }
    if (maxSum > 100000) {
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

  get minOperand() {
    return this.properties.minOperand;
  }

  set minOperand(minOperand: number) {
    this._propertiesEmitter.emit({
      ...this.properties,
      minOperand: minOperand
    })
  }
}
