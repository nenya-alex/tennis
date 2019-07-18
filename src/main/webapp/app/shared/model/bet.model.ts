import { Moment } from 'moment';

export const enum MatchWinner {
  Q = 'Q'
}

export const enum BetStatus {
  A = 'A'
}

export interface IBet {
  id?: number;
  amount?: number;
  odds?: number;
  placedBetMatchWinner?: MatchWinner;
  status?: BetStatus;
  placedDate?: Moment;
  matchId?: number;
}

export class Bet implements IBet {
  constructor(
    public id?: number,
    public amount?: number,
    public odds?: number,
    public placedBetMatchWinner?: MatchWinner,
    public status?: BetStatus,
    public placedDate?: Moment,
    public matchId?: number
  ) {}
}
