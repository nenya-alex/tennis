import {BaseEntity} from "./../../shared";

export class Match implements BaseEntity {
    constructor(
        public id?: number,
        public identifier?: string,
        public homeName?: string,
        public awayName?: string,
        public homeScore?: number,
        public awayScore?: number,
        public startDate?: any,
        public name?: string,
        public openDate?: any,
        public setts?: BaseEntity[],
    ) {
    }
}
