import {Injectable} from "@angular/core";
import {HttpClient, HttpResponse} from "@angular/common/http";
import {Observable} from "rxjs/Observable";
import {SERVER_API_URL} from "../../app.constants";

import {JhiDateUtils} from "ng-jhipster";

import {Match} from "./match.model";
import {createRequestOption} from "../../shared";

export type EntityResponseType = HttpResponse<Match>;

@Injectable()
export class MatchService {

    private resourceUrl =  SERVER_API_URL + 'api/matches';

    constructor(private http: HttpClient, private dateUtils: JhiDateUtils) { }

    create(match: Match): Observable<EntityResponseType> {
        const copy = this.convert(match);
        return this.http.post<Match>(this.resourceUrl, copy, { observe: 'response' })
            .map((res: EntityResponseType) => this.convertResponse(res));
    }

    update(match: Match): Observable<EntityResponseType> {
        const copy = this.convert(match);
        return this.http.put<Match>(this.resourceUrl, copy, { observe: 'response' })
            .map((res: EntityResponseType) => this.convertResponse(res));
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http.get<Match>(`${this.resourceUrl}/${id}`, { observe: 'response'})
            .map((res: EntityResponseType) => this.convertResponse(res));
    }

    query(req?: any): Observable<HttpResponse<Match[]>> {
        const options = createRequestOption(req);
        return this.http.get<Match[]>(this.resourceUrl, { params: options, observe: 'response' })
            .map((res: HttpResponse<Match[]>) => this.convertArrayResponse(res));
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response'});
    }

    private convertResponse(res: EntityResponseType): EntityResponseType {
        const body: Match = this.convertItemFromServer(res.body);
        return res.clone({body});
    }

    private convertArrayResponse(res: HttpResponse<Match[]>): HttpResponse<Match[]> {
        const jsonResponse: Match[] = res.body;
        const body: Match[] = [];
        for (let i = 0; i < jsonResponse.length; i++) {
            body.push(this.convertItemFromServer(jsonResponse[i]));
        }
        return res.clone({body});
    }

    /**
     * Convert a returned JSON object to Match.
     */
    private convertItemFromServer(match: Match): Match {
        const copy: Match = Object.assign({}, match);
        copy.startDate = this.dateUtils
            .convertDateTimeFromServer(match.startDate);
        copy.openDate = this.dateUtils
            .convertDateTimeFromServer(match.openDate);
        return copy;
    }

    /**
     * Convert a Match to a JSON which can be sent to the server.
     */
    private convert(match: Match): Match {
        const copy: Match = Object.assign({}, match);

        copy.startDate = this.dateUtils.toDate(match.startDate);

        copy.openDate = this.dateUtils.toDate(match.openDate);
        return copy;
    }
}
