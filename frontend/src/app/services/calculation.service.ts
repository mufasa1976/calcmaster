import { Injectable } from "@angular/core";
import { HttpClient, HttpErrorResponse, HttpResponse } from "@angular/common/http";
import { CalculationProperties } from "../../shared/calculationProperties";
import { catchError, Observable, throwError } from "rxjs";
import { ToastrService } from "ngx-toastr";

@Injectable({ providedIn: "root" })
export class CalculationService {

  constructor(private _httpClient: HttpClient, private _toastr: ToastrService) {
  }

  generate(calculationProperties: CalculationProperties, fileEnding: string = "pdf"): Observable<HttpResponse<Blob>> {
    return this._httpClient.post(`api/v1/calculation.${fileEnding}`, calculationProperties, {
      observe: "response",
      responseType: "blob",
      headers: {
        "Accept": "application/octet-stream"
      }
    }).pipe(
      catchError((error: HttpErrorResponse) => {
        console.error(error);
        this._toastr.error(`Fehler beim Generieren. HTTP ${error.status}: ${error.statusText}`)
        return throwError(() => error);
      })
    );
  }
}
