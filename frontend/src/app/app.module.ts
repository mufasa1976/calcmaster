import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {AppComponent} from './components/app.component';
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {HttpClientModule} from "@angular/common/http";
import {MatToolbarModule} from "@angular/material/toolbar";
import {MatIconModule} from "@angular/material/icon";
import {MatMenuModule} from "@angular/material/menu";
import {ThemeSwitcherComponent} from "./components/theme-switcher/theme-switcher.component";
import {FaIconLibrary, FontAwesomeModule} from "@fortawesome/angular-fontawesome";
import {registerLocaleData} from "@angular/common";
import localeDe from '@angular/common/locales/de';
import localeDeAt from '@angular/common/locales/de-AT';
import {fas} from "@fortawesome/free-solid-svg-icons";
import {MatButtonModule} from "@angular/material/button";
import {MatButtonToggleModule} from "@angular/material/button-toggle";
import {AdditionOptionsComponent} from './components/options/addition/addition-options.component';
import {SubtractionOptionsComponent} from './components/options/subtraction/subtraction-options.component';
import {MultiplicationOptionsComponent} from './components/options/multiplication/multiplication-options.component';
import {DivisionOptionsComponent} from './components/options/division/division-options.component';
import {MatInputModule} from "@angular/material/input";
import {MatSelectModule} from "@angular/material/select";
import {MatSlideToggleModule} from "@angular/material/slide-toggle";
import {MatChipsModule} from "@angular/material/chips";
import {MatTooltipModule} from "@angular/material/tooltip";
import {FormsModule} from "@angular/forms";
import {ToastrModule} from "ngx-toastr";

@NgModule({
  declarations: [
    AppComponent,
    ThemeSwitcherComponent,
    AdditionOptionsComponent,
    SubtractionOptionsComponent,
    MultiplicationOptionsComponent,
    DivisionOptionsComponent,
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    FormsModule,
    HttpClientModule,
    FontAwesomeModule,
    ToastrModule.forRoot(),
    MatToolbarModule,
    MatIconModule,
    MatMenuModule,
    MatButtonModule,
    MatButtonToggleModule,
    MatInputModule,
    MatSelectModule,
    MatSlideToggleModule,
    MatChipsModule,
    MatTooltipModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule {
  constructor(private _faIconLibrary: FaIconLibrary) {
    registerLocaleData(localeDe, 'de');
    registerLocaleData(localeDeAt, 'de-AT');
    _faIconLibrary.addIconPacks(fas);
  }
}
