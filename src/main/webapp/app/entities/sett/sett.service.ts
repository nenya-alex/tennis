import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { SERVER_API_URL } from '../../app.constants';

import { Sett } from './sett.model';
import { createRequestOption } from '../../shared';

export type EntityResponseType = HttpResponse<Sett>;

@Injectable()
export class SettService {

    private resourceUrl =  SERVER_API_URL + 'api/setts';

    constructor(private http: HttpClient) { }

    create(sett: Sett): Observable<EntityResponseType> {
        const copy = this.convert(sett);
        return this.http.post<Sett>(this.resourceUrl, copy, { observe: 'response' })
            .map((res: EntityResponseType) => this.convertResponse(res));
    }

    update(sett: Sett): Observable<EntityResponseType> {
        const copy = this.convert(sett);
        return this.http.put<Sett>(this.resourceUrl, copy, { observe: 'response' })
            .map((res: EntityResponseType) => this.convertResponse(res));
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http.get<Sett>(`${this.resourceUrl}/${id}`, { observe: 'response'})
            .map((res: EntityResponseType) => this.convertResponse(res));
    }

    query(req?: any): Observable<HttpResponse<Sett[]>> {
        const options = createRequestOption(req);
        return this.http.get<Sett[]>(this.resourceUrl, { params: options, observe: 'response' })
            .map((res: HttpResponse<Sett[]>) => this.convertArrayResponse(res));
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response'});
    }

    private convertResponse(res: EntityResponseType): EntityResponseType {
        const body: Sett = this.convertItemFromServer(res.body);
        return res.clone({body});
    }

    private convertArrayResponse(res: HttpResponse<Sett[]>): HttpResponse<Sett[]> {
        const jsonResponse: Sett[] = res.body;
        const body: Sett[] = [];
        for (let i = 0; i < jsonResponse.length; i++) {
            body.push(this.convertItemFromServer(jsonResponse[i]));
        }
        return res.clone({body});
    }

    /**
     * Convert a returned JSON object to Sett.
     */
    private convertItemFromServer(sett: Sett): Sett {
        const copy: Sett = Object.assign({}, sett);
        return copy;
    }

    /**
     * Convert a Sett to a JSON which can be sent to the server.
     */
    private convert(sett: Sett): Sett {
        const copy: Sett = Object.assign({}, sett);
        return copy;
    }
}
