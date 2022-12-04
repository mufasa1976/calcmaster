import { Component, EventEmitter, Input, Output } from '@angular/core';
import { AdditionProperties, initialAdditionProperties } from "../../../../shared/addition-properties";
import { MatSlideToggleChange } from "@angular/material/slide-toggle";

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
    let secondAddendRounding = this.secondAddendRounding;
    if (maxSum <= 20) {
      secondAddendRounding = 1;
    }
    if (maxSum <= 100 && secondAddendRounding === 100) {
      secondAddendRounding = 10;
    }
    if (maxSum <= 1000 && secondAddendRounding === 1000) {
      secondAddendRounding = 100;
    }

    let minSum = this.minSum;
    if (maxSum < 20) {
      minSum = 0;
    }

    this._propertiesEmitter.emit({
      ...this.properties,
      minSum: minSum,
      maxSum: maxSum,
      secondAddendRounding: secondAddendRounding
    })
  }

  get secondAddendRounding() {
    return this.properties.secondAddendRounding;
  }

  set secondAddendRounding(secondAddendRounding: number) {
    let includeZeroOnOperand = this.properties.includeZeroOnOperand;
    if (secondAddendRounding > 1) {
      includeZeroOnOperand = false;
    }
    this._propertiesEmitter.emit({
      ...this.properties,
      secondAddendRounding: secondAddendRounding,
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
