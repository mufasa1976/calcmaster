import { Component, EventEmitter, Input, Output } from '@angular/core';
import { AdditionProperties, initialAdditionProperties } from "../../../../shared/addition-properties";
import { MatSlideToggleChange } from "@angular/material/slide-toggle";
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
    if (maxSum <= 20) {
      secondAddendRounding = 1;
    } else if (maxSum <= 100 && secondAddendRounding > 10) {
      secondAddendRounding = 10;
    } else if (maxSum <= 1000 && secondAddendRounding > 100) {
      secondAddendRounding = 100;
    }

    if (maxSum < 20 && transgression > UNLIMITED_TRANSACTIONS) {
      transgression = UNLIMITED_TRANSACTIONS;
    } else if (maxSum < 100 && transgression >= this.ONE) {
      transgression &= this.ONE;
    } else if (maxSum < 1000 && transgression > this.TEN) {
      transgression &= this.ONE | this.TEN;
    } else if (maxSum < 10000 && transgression > this.HUNDRED) {
      transgression &= this.ONE | this.TEN | this.HUNDRED;
    } else if (maxSum < 100000 && transgression > this.THOUSAND) {
      transgression &= this.ONE | this.TEN | this.HUNDRED | this.THOUSAND;
    } else if (maxSum > 100000) {
      transgression = UNLIMITED_TRANSACTIONS;
    }
    if (secondAddendRounding > 1) {
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

  changeUnlimitedTransgression(event: MatCheckboxChange) {
    const maxSum = this.properties.maxSum;
    let transgression = this.ONE;
    let includeZeroAsOperand = this.properties.includeZeroOnOperand;
    if (maxSum >= 100) {
      transgression |= this.TEN;
    }
    if (maxSum >= 1000) {
      transgression |= this.HUNDRED;
    }
    if (maxSum >= 10000) {
      transgression |= this.THOUSAND;
    }
    if (maxSum >= 100000) {
      transgression |= this.TENTHOUSAND;
    }
    this._propertiesEmitter.emit({
      ...this.properties,
      transgression: event.checked ? transgression : -1,
      includeZeroOnOperand: event.checked ? true : includeZeroAsOperand
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
