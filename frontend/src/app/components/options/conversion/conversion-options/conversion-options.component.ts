import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ConversionProperties, initialConversionProperties, Unit } from "../../../../../shared/conversion-properties";
import { MatSlideToggleChange } from "@angular/material/slide-toggle";

@Component({
  selector: 'app-conversion-options',
  templateUrl: './conversion-options.component.html',
  styleUrls: ['./conversion-options.component.scss']
})
export class ConversionOptionsComponent {
  @Input("properties") properties: ConversionProperties = { ...initialConversionProperties };
  @Output("propertiesChange") private _propertiesEmitter = new EventEmitter<ConversionProperties>();

  readonly unitEnum: typeof Unit = Unit;

  constructor() { }

  get unit() {
    return this.properties.unit;
  }

  set unit(unit: Unit) {
    this._propertiesEmitter.emit({
      ...this.properties,
      unit: unit,
      withKilometers: unit == Unit.METER ? this.properties.withKilometers : false
    })
  }

  get withKilometers() {
    return this.properties.withKilometers;
  }

  toggleWithKilometers(event: MatSlideToggleChange) {
    this._propertiesEmitter.emit({
      ...this.properties,
      withKilometers: this.properties.unit == Unit.METER ? event.checked : false
    })
  }
}
