import { Component, EventEmitter, Input, Output } from '@angular/core';
import { RoundingProperties } from "../../../../../shared/roundingProperties";

@Component({
  selector: 'app-rounding-options',
  templateUrl: './rounding-options.component.html',
  styleUrls: ['./rounding-options.component.scss']
})
export class RoundingOptionsComponent {
  @Input("properties") properties: RoundingProperties = {};
  @Output("propertiesChange") private _propertiesEmitter = new EventEmitter<RoundingProperties>();

  constructor() { }
}
