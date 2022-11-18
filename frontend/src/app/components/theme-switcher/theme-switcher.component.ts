import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Theme, ThemeName } from "../../../shared/theme";
import { ThemeService } from "../../services/theme.service";

@Component({
  selector: 'app-theme-switcher',
  templateUrl: './theme-switcher.component.html',
  styleUrls: ['./theme-switcher.component.scss']
})
export class ThemeSwitcherComponent {
  @Input() themes: Array<Theme> | null | undefined;
  @Output() themeChange: EventEmitter<ThemeName> = new EventEmitter<ThemeName>();

  constructor(private readonly _themeService: ThemeService) { }

  changeTheme(themeName: ThemeName) {
    this.themeChange.emit(themeName);
  }
}
