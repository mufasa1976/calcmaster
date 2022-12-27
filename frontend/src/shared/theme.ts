export enum ThemeName {
  DEEPPURPLE_AMBER = "deeppurple-amber",
  INDIGO_PINK = "indigo-pink",
  PINK_BLUEGREY = "pink-bluegrey",
  PURPLE_GREEN = "purple-green"
}

export const DEFAULT_THEME = ThemeName.PURPLE_GREEN;

export interface Theme {
  backgroundColor: string;
  buttonColor: string;
  headingColor: string;
  label: string;
  value: ThemeName;
  logo: string;
}
