import {Injectable} from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs';
import * as moment from 'moment';
import {DATE_FORMAT} from 'app/shared/constants/input.constants';
import {map} from 'rxjs/operators';

import {SERVER_API_URL} from 'app/app.constants';
import {createRequestOption} from 'app/shared';
import {IOdds} from 'app/shared/model/odds.model';

type EntityResponseType = HttpResponse<IOdds>;
type EntityArrayResponseType = HttpResponse<IOdds[]>;

@Injectable({providedIn: 'root'})
export class OddsService {
    public resourceUrl = SERVER_API_URL + 'api/odds';

    constructor(protected http: HttpClient) {
    }

    create(odds: IOdds): Observable<EntityResponseType> {
        const copy = this.convertDateFromClient(odds);
        return this.http
            .post<IOdds>(this.resourceUrl, copy, {observe: 'response'})
            .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
    }

    update(odds: IOdds): Observable<EntityResponseType> {
        const copy = this.convertDateFromClient(odds);
        return this.http
            .put<IOdds>(this.resourceUrl, copy, {observe: 'response'})
            .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http
            .get<IOdds>(`${this.resourceUrl}/${id}`, {observe: 'response'})
            .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
    }

    query(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http
            .get<IOdds[]>(this.resourceUrl, {params: options, observe: 'response'})
            .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, {observe: 'response'});
    }

    protected convertDateFromClient(odds: IOdds): IOdds {
        const copy: IOdds = Object.assign({}, odds, {
            checkDate: odds.checkDate != null && odds.checkDate.isValid() ? odds.checkDate.toJSON() : null
        });
        return copy;
    }

    protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
        if (res.body) {
            res.body.checkDate = res.body.checkDate != null ? moment(res.body.checkDate) : null;
        }
        return res;
    }

    protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
        if (res.body) {
            res.body.forEach((odds: IOdds) => {
                odds.checkDate = odds.checkDate != null ? moment(odds.checkDate) : null;
            });
        }
        return res;
    }
}
