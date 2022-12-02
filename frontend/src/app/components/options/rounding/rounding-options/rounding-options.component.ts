import { Component, EventEmitter, Input, Output } from '@angular/core';
import { initialRoundingProperties, RoundingProperties } from "../../../../../shared/roundingProperties";

@Component({
  selector: 'app-rounding-options',
  templateUrl: './rounding-options.component.html',
  styleUrls: ['./rounding-options.component.scss']
})
export class RoundingOptionsComponent {
  @Input("properties") properties: RoundingProperties = { ...initialRoundingProperties };
  @Output("propertiesChange") private _propertiesEmitter = new EventEmitter<RoundingProperties>();

  constructor() { }

  get maxPower() {
    return this.properties.maxPower;
  }

  set maxPower(maxPower: number) {
    let minPower = this.properties.minPower;
    if (minPower >= maxPower) {
      minPower = maxPower - 1;
    }
    this._propertiesEmitter.emit({
      ...this.properties,
      maxPower: maxPower,
      minPower: minPower
    });
  }

  get minPower() {
    return this.properties.minPower;
  }

  set minPower(minPower: number) {
    this._propertiesEmitter.emit({
      ...this.properties,
      minPower: minPower
    })
  }
}
