import {Component, EventEmitter, Input, Output} from '@angular/core';
import {DivisionProperties} from "../../../../shared/divisionProperties";
import * as _ from "lodash";
import {MatChipInputEvent} from "@angular/material/chips";
import {COMMA, ENTER} from "@angular/cdk/keycodes";

@Component({
  selector: 'app-division-options',
  templateUrl: './division-options.component.html',
  styleUrls: ['./division-options.component.scss']
})
export class DivisionOptionsComponent {
  @Input("properties") properties: DivisionProperties = {
    maxDividend: 100,
    maxRemainder: 0,
    exclusions: [],
    fixedDivisors: []
  }
  @Output("propertiesChange") private _propertiesEmitter = new EventEmitter<DivisionProperties>();

  addOnBlur = true;
  readonly separatorKeysCodes = [ENTER, COMMA] as const;

  constructor() {}

  get maxDividend() {
    return this.properties.maxDividend;
  }

  set maxDividend(maxDividend: number) {
    const fixedDivisor = maxDividend / 10;
    let fixedDivisors = this.properties.fixedDivisors;
    let maxRemainder = this.properties.maxRemainder;
    if (maxDividend < 100) {
      fixedDivisors = [fixedDivisor];
      maxRemainder = 0;
    } else if (this.properties.maxDividend < 100) {
      fixedDivisors = [];
    }
    this._propertiesEmitter.emit({
      ...this.properties,
      maxDividend: maxDividend,
      maxRemainder: maxRemainder,
      exclusions: _.remove(this.properties.exclusions, value => value !== fixedDivisor),
      fixedDivisors: fixedDivisors
    })
  }

  get maxRemainder() {
    return this.properties.maxRemainder;
  }

  set maxRemainder(maxRemainder: number) {
    this._propertiesEmitter.emit({
      ...this.properties,
      maxRemainder: maxRemainder
    })
  }

  get exclusions() {
    return this.properties.exclusions;
  }

  removeExclusion(exclusion: number) {
    this._propertiesEmitter.emit({
      ...this.properties,
      exclusions: _.remove(this.properties.exclusions, value => value !== exclusion)
    });
  }

  addExclusion(event: MatChipInputEvent) {
    const value = (event.value || '').trim();

    if (value) {
      this._propertiesEmitter.emit({
        ...this.properties,
        exclusions: _.concat(this.properties.exclusions, parseInt(value)),
        fixedDivisors: _.remove(this.properties.fixedDivisors, v => v !== parseInt(value))
      });
    }

    event.chipInput!.clear();
  }

  get fixedDivisors() {
    return this.properties.fixedDivisors;
  }

  removeFixedDivisor(fixedDivisor: number) {
    this._propertiesEmitter.emit({
      ...this.properties,
      fixedDivisors: _.remove(this.properties.fixedDivisors, value => value !== fixedDivisor)
    })
  }

  addFixedDivisor(event: MatChipInputEvent) {
    const value = (event.value || '').trim();

    if (value) {
      this._propertiesEmitter.emit({
        ...this.properties,
        exclusions: _.remove(this.properties.exclusions, v => v !== parseInt(value)),
        fixedDivisors: _.concat(this.properties.fixedDivisors, parseInt(value))
      })
    }

    event.chipInput!.clear();
  }
}
