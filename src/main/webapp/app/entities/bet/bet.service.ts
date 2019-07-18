import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_FORMAT } from 'app/shared/constants/input.constants';
import { map } from 'rxjs/operators';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { IBet } from 'app/shared/model/bet.model';

type EntityResponseType = HttpResponse<IBet>;
type EntityArrayResponseType = HttpResponse<IBet[]>;

@Injectable({ providedIn: 'root' })
export class BetService {
  public resourceUrl = SERVER_API_URL + 'api/bets';

  constructor(protected http: HttpClient) {}

  create(bet: IBet): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(bet);
    return this.http
      .post<IBet>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(bet: IBet): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(bet);
    return this.http
      .put<IBet>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IBet>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IBet[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<any>> {
    return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  protected convertDateFromClient(bet: IBet): IBet {
    const copy: IBet = Object.assign({}, bet, {
      placedDate: bet.placedDate != null && bet.placedDate.isValid() ? bet.placedDate.toJSON() : null
    });
    return copy;
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.placedDate = res.body.placedDate != null ? moment(res.body.placedDate) : null;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((bet: IBet) => {
        bet.placedDate = bet.placedDate != null ? moment(bet.placedDate) : null;
      });
    }
    return res;
  }
}
