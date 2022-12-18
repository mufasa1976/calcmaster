import { Injectable, OnDestroy } from '@angular/core';
import { HttpClient } from "@angular/common/http";
import { Observable, Subscription } from "rxjs";
import { DEFAULT_THEME, Theme, ThemeName } from "../../shared/theme";
import { StyleManagerService } from "./style-manager.service";
import { Location } from "@angular/common";

@Injectable({ providedIn: 'root' })
export class ThemeService implements OnDestroy {
  private readonly themeSubscription: Subscription;

  private _logos: { [name: string]: string } = {};
  private _currentTheme: ThemeName = DEFAULT_THEME;

  constructor(private _location: Location,
              private _httpClient: HttpClient,
              private _styleManager: StyleManagerService) {
    this.themeSubscription = this._httpClient.get<Theme[]>(_location.prepareExternalUrl('assets/themes.json')).subscribe(themes => {
      themes.forEach(theme => this._logos = {
        ...this._logos,
        [theme.value]: theme.logo
      });
    });
  }

  ngOnDestroy(): void {
    this.themeSubscription.unsubscribe();
  }

  getThemes(): Observable<Theme[]> {
    return this._httpClient.get<Theme[]>(this._location.prepareExternalUrl('assets/themes.json'));
  }

  setTheme(themeName: ThemeName) {
    this._styleManager.setStyle("theme", this._location.prepareExternalUrl(`${themeName}.css`));
    this._currentTheme = themeName;
  }

  getCurrentLogo(): string {
    return this._location.prepareExternalUrl(this._logos[this._currentTheme] || "assets/logo_dark.png");
  }
}
