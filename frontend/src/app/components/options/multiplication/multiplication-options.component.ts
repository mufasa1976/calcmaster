import { Component, EventEmitter, Input, Output } from '@angular/core';
import { initialMultiplicationProperties, MultiplicationProperties, MultiplicationType } from "../../../../shared/multiplication-properties";
import { COMMA, ENTER } from "@angular/cdk/keycodes";
import * as _ from "lodash";
import { MatChipInputEvent } from "@angular/material/chips";
import { UNLIMITED_TRANSACTIONS } from "../../../../shared/constants";
import { MatCheckboxChange } from "@angular/material/checkbox";

@Component({
  selector: 'app-multiplication-options',
  templateUrl: './multiplication-options.component.html',
  styleUrls: ['./multiplication-options.component.scss']
})
export class MultiplicationOptionsComponent {
  @Input("properties") properties: MultiplicationProperties = { ...initialMultiplicationProperties };
  @Output("propertiesChange") private _propertiesEmitter = new EventEmitter<MultiplicationProperties>();

  readonly ONE = 1 << 0;
  readonly TEN = 1 << 1;
  readonly HUNDRED = 1 << 2;
  readonly THOUSAND = 1 << 3;
  readonly TENTHOUSAND = 1 << 4;

  readonly typeEnum: typeof MultiplicationType = MultiplicationType;

  addOnBlur = true;
  readonly separatorKeysCodes = [ENTER, COMMA] as const;

  constructor() {
  }

  get type() {
    return this.properties.type;
  }

  set type(type: MultiplicationType) {
    this._propertiesEmitter.emit({
      ...this.properties,
      type: type
    })
  }

  get maxProduct() {
    return this.properties.maxProduct;
  }

  set maxProduct(maxProduct: number) {
    const fixedMultiplicand = maxProduct / 10;
    let fixedMultiplicands = this.properties.fixedMultiplicands;
    let transgression = this.properties.transgression;
    if (maxProduct < 100) {
      fixedMultiplicands = [fixedMultiplicand];
    } else if (this.properties.maxProduct < 100) {
      fixedMultiplicands = [];
    }
    if (maxProduct < 20 && transgression > UNLIMITED_TRANSACTIONS) {
      transgression = UNLIMITED_TRANSACTIONS;
    } else if (maxProduct <= 100 && transgression >= this.ONE) {
      transgression &= this.ONE;
    } else if (maxProduct <= 1000 && transgression >= this.TEN) {
      transgression &= this.ONE | this.TEN;
    } else if (maxProduct <= 10000 && transgression > this.HUNDRED) {
      transgression &= this.ONE | this.TEN | this.HUNDRED;
    } else if (maxProduct <= 100000 && transgression > this.THOUSAND) {
      transgression &= this.ONE | this.TEN | this.HUNDRED | this.THOUSAND;
    }
    this._propertiesEmitter.emit({
      ...this.properties,
      maxProduct: maxProduct,
      exclusions: _.remove(this.properties.exclusions, value => value !== fixedMultiplicand),
      fixedMultiplicands: fixedMultiplicands,
      transgression: transgression
    });
  }

  get exclusions() {
    return this.properties.exclusions;
  }

  removeExclusion(exclusion: number) {
    this._propertiesEmitter.emit({
      ...this.properties,
      exclusions: _.remove(this.properties.exclusions, value => value !== exclusion)
    })
  }

  addExclusion(event: MatChipInputEvent) {
    const value = (event.value || '').trim();

    if (value) {
      this._propertiesEmitter.emit({
        ...this.properties,
        exclusions: _.concat(this.properties.exclusions, parseInt(value)),
        fixedMultiplicands: _.remove(this.properties.fixedMultiplicands, v => v !== parseInt(value))
      })
    }

    event.chipInput!.clear();
  }

  get fixedMultiplicands() {
    return this.properties.fixedMultiplicands;
  }

  removeFixedMultiplicand(fixedMultiplicand: number) {
    this._propertiesEmitter.emit({
      ...this.properties,
      fixedMultiplicands: _.remove(this.properties.fixedMultiplicands, value => value !== fixedMultiplicand)
    })
  }

  addFixedMultiplicand(event: MatChipInputEvent) {
    const value = (event.value || '').trim();

    if (value) {
      this._propertiesEmitter.emit({
        ...this.properties,
        exclusions: _.remove(this.properties.exclusions, v => v !== parseInt(value)),
        fixedMultiplicands: _.concat(this.properties.fixedMultiplicands, parseInt(value))
      })
    }

    event.chipInput!.clear();
  }

  get transgression() {
    return this.properties.transgression;
  }

  changeUnlimitedTransgression(event: MatCheckboxChange) {
    const maxProduct = this.properties.maxProduct;
    let transgression = this.ONE;
    if (maxProduct > 100) {
      transgression |= this.TEN;
    }
    if (maxProduct > 1000) {
      transgression |= this.HUNDRED;
    }
    if (maxProduct > 10000) {
      transgression |= this.THOUSAND;
    }
    if (maxProduct > 100000) {
      transgression |= this.TENTHOUSAND;
    }
    this._propertiesEmitter.emit({
      ...this.properties,
      transgression: event.checked ? transgression : -1,
    })
  }

  isTransgression(digit: number): boolean {
    return (this.properties.transgression & digit) === digit;
  }

  setTransgression(digit: number) {
    let fixedMultiplicands = this.properties.fixedMultiplicands;
    let exclusions = this.properties.exclusions;
    let transgression = this.properties.transgression ^ digit;
    this._propertiesEmitter.emit({
      ...this.properties,
      transgression: transgression,
      fixedMultiplicands: transgression > 0 ? [] : fixedMultiplicands,
      exclusions: transgression > 0 ? [] : exclusions
    })
  }
}
