import {Injectable} from "@angular/core";
import {HttpClient, HttpResponse} from "@angular/common/http";
import {Observable} from "rxjs/Observable";
import {SERVER_API_URL} from "../../app.constants";

import {Game} from "./game.model";
import {createRequestOption} from "../../shared";

export type EntityResponseType = HttpResponse<Game>;

@Injectable()
export class GameService {

    private resourceUrl =  SERVER_API_URL + 'api/games';

    constructor(private http: HttpClient) { }

    create(game: Game): Observable<EntityResponseType> {
        const copy = this.convert(game);
        return this.http.post<Game>(this.resourceUrl, copy, { observe: 'response' })
            .map((res: EntityResponseType) => this.convertResponse(res));
    }

    update(game: Game): Observable<EntityResponseType> {
        const copy = this.convert(game);
        return this.http.put<Game>(this.resourceUrl, copy, { observe: 'response' })
            .map((res: EntityResponseType) => this.convertResponse(res));
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http.get<Game>(`${this.resourceUrl}/${id}`, { observe: 'response'})
            .map((res: EntityResponseType) => this.convertResponse(res));
    }

    query(req?: any): Observable<HttpResponse<Game[]>> {
        const options = createRequestOption(req);
        return this.http.get<Game[]>(this.resourceUrl, { params: options, observe: 'response' })
            .map((res: HttpResponse<Game[]>) => this.convertArrayResponse(res));
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response'});
    }

    private convertResponse(res: EntityResponseType): EntityResponseType {
        const body: Game = this.convertItemFromServer(res.body);
        return res.clone({body});
    }

    private convertArrayResponse(res: HttpResponse<Game[]>): HttpResponse<Game[]> {
        const jsonResponse: Game[] = res.body;
        const body: Game[] = [];
        for (let i = 0; i < jsonResponse.length; i++) {
            body.push(this.convertItemFromServer(jsonResponse[i]));
        }
        return res.clone({body});
    }

    /**
     * Convert a returned JSON object to Game.
     */
    private convertItemFromServer(game: Game): Game {
        const copy: Game = Object.assign({}, game);
        return copy;
    }

    /**
     * Convert a Game to a JSON which can be sent to the server.
     */
    private convert(game: Game): Game {
        const copy: Game = Object.assign({}, game);
        return copy;
    }
}
