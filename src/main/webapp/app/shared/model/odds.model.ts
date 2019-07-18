import {Moment} from "moment";

export interface IOdds {
    id?: number;
    homeOdds?: number;
    awayOdds?: number;
    checkDate?: Moment;
    matchId?: number;
}

export class Odds implements IOdds {
    constructor(public id?: number, public homeOdds?: number, public awayOdds?: number, public checkDate?: Moment, public matchId?: number) {
    }
}
