import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { Location } from "@angular/common";

@Injectable({ providedIn: 'root' })
export class ApiPathInterceptor implements HttpInterceptor {
  constructor(private _location: Location) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    if (!req.url.startsWith("api/")) {
      return next.handle(req);
    }

    const url = this._location.prepareExternalUrl(`../${req.url}`);
    const language = Location.stripTrailingSlash(this._location.prepareExternalUrl('/')).split('/').pop();
    if (language) {
      return next.handle(req.clone({ url: url, setHeaders: { 'Accept-Language': language } }));
    }
    return next.handle(req.clone({ url: url }));
  }
}
