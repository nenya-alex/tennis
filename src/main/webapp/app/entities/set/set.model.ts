import {BaseEntity} from "./../../shared";

export class Set implements BaseEntity {
    constructor(
        public id?: number,
        public homeScore?: number,
        public awayScore?: number,
        public probabilityHome?: number,
        public probabilityAway?: number,
        public games?: BaseEntity[],
        public matchId?: number,
    ) {
    }
}
