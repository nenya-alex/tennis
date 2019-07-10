import {Injectable} from "@angular/core";
import {HttpClient, HttpResponse} from "@angular/common/http";
import {Observable} from "rxjs/Observable";
import {SERVER_API_URL} from "../../app.constants";

import {Set} from "./set.model";
import {createRequestOption} from "../../shared";

export type EntityResponseType = HttpResponse<Set>;

@Injectable()
export class SetService {

    private resourceUrl =  SERVER_API_URL + 'api/sets';

    constructor(private http: HttpClient) { }

    create(set: Set): Observable<EntityResponseType> {
        const copy = this.convert(set);
        return this.http.post<Set>(this.resourceUrl, copy, { observe: 'response' })
            .map((res: EntityResponseType) => this.convertResponse(res));
    }

    update(set: Set): Observable<EntityResponseType> {
        const copy = this.convert(set);
        return this.http.put<Set>(this.resourceUrl, copy, { observe: 'response' })
            .map((res: EntityResponseType) => this.convertResponse(res));
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http.get<Set>(`${this.resourceUrl}/${id}`, { observe: 'response'})
            .map((res: EntityResponseType) => this.convertResponse(res));
    }

    query(req?: any): Observable<HttpResponse<Set[]>> {
        const options = createRequestOption(req);
        return this.http.get<Set[]>(this.resourceUrl, { params: options, observe: 'response' })
            .map((res: HttpResponse<Set[]>) => this.convertArrayResponse(res));
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response'});
    }

    private convertResponse(res: EntityResponseType): EntityResponseType {
        const body: Set = this.convertItemFromServer(res.body);
        return res.clone({body});
    }

    private convertArrayResponse(res: HttpResponse<Set[]>): HttpResponse<Set[]> {
        const jsonResponse: Set[] = res.body;
        const body: Set[] = [];
        for (let i = 0; i < jsonResponse.length; i++) {
            body.push(this.convertItemFromServer(jsonResponse[i]));
        }
        return res.clone({body});
    }

    /**
     * Convert a returned JSON object to Set.
     */
    private convertItemFromServer(set: Set): Set {
        const copy: Set = Object.assign({}, set);
        return copy;
    }

    /**
     * Convert a Set to a JSON which can be sent to the server.
     */
    private convert(set: Set): Set {
        const copy: Set = Object.assign({}, set);
        return copy;
    }
}
