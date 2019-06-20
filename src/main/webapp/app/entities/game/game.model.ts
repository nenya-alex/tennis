import { BaseEntity } from './../../shared';

export class Game implements BaseEntity {
    constructor(
        public id?: number,
        public probabilityHome?: number,
        public probabilityAway?: number,
        public settId?: number,
    ) {
    }
}
