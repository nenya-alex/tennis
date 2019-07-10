import {BaseEntity} from "./../../shared";

export class Game implements BaseEntity {
    constructor(
        public id?: number,
        public homeScore?: number,
        public awayScore?: number,
        public homeProbability?: number,
        public awayProbability?: number,
        public settId?: number,
    ) {
    }
}
