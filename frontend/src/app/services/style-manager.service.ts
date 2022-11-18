import {Inject, Injectable} from '@angular/core';
import {DOCUMENT} from "@angular/common";

@Injectable({providedIn: 'root'})
export class StyleManagerService {

  constructor(@Inject(DOCUMENT) private readonly _document: Document) {
  }

  setStyle(key: string, href: string) {
    let themeLink = this._document.getElementById("client-theme") as HTMLLinkElement;
    if (themeLink) {
      themeLink.href = href;
    } else {
      const head = this._document.getElementsByTagName("head")[0];
      const style = this._document.createElement("link");
      style.id = "client-theme";
      style.rel = "stylesheet";
      style.href = href;
      head.appendChild(style)
    }
  }
}
