import {Component, OnInit} from "@angular/core";
import {HttpErrorResponse, HttpResponse} from "@angular/common/http";
import {FormBuilder, Validators} from "@angular/forms";
import {ActivatedRoute} from "@angular/router";
import {Observable} from "rxjs";
import {filter, map} from "rxjs/operators";
import * as moment from "moment";
import {DATE_TIME_FORMAT} from "app/shared/constants/input.constants";
import {JhiAlertService} from "ng-jhipster";
import {Bet, IBet} from "app/shared/model/bet.model";
import {BetService} from "./bet.service";
import {IMatch} from "app/shared/model/match.model";
import {MatchService} from "app/entities/match";

@Component({
  selector: 'jhi-bet-update',
  templateUrl: './bet-update.component.html'
})
export class BetUpdateComponent implements OnInit {
  bet: IBet;
  isSaving: boolean;

  matches: IMatch[];

  editForm = this.fb.group({
    id: [],
    amount: [],
    odds: [],
    placedBetMatchWinner: [],
    status: [],
    placedDate: [],
    matchId: []
  });

  constructor(
    protected jhiAlertService: JhiAlertService,
    protected betService: BetService,
    protected matchService: MatchService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit() {
    this.isSaving = false;
    this.activatedRoute.data.subscribe(({ bet }) => {
      this.updateForm(bet);
      this.bet = bet;
    });
    this.matchService
      .query({ filter: 'bet-is-null' })
      .pipe(
        filter((mayBeOk: HttpResponse<IMatch[]>) => mayBeOk.ok),
        map((response: HttpResponse<IMatch[]>) => response.body)
      )
      .subscribe(
        (res: IMatch[]) => {
          if (!this.bet.matchId) {
            this.matches = res;
          } else {
            this.matchService
              .find(this.bet.matchId)
              .pipe(
                filter((subResMayBeOk: HttpResponse<IMatch>) => subResMayBeOk.ok),
                map((subResponse: HttpResponse<IMatch>) => subResponse.body)
              )
              .subscribe(
                (subRes: IMatch) => (this.matches = [subRes].concat(res)),
                (subRes: HttpErrorResponse) => this.onError(subRes.message)
              );
          }
        },
        (res: HttpErrorResponse) => this.onError(res.message)
      );
  }

  updateForm(bet: IBet) {
    this.editForm.patchValue({
      id: bet.id,
      amount: bet.amount,
      odds: bet.odds,
      placedBetMatchWinner: bet.placedBetMatchWinner,
      status: bet.status,
      placedDate: bet.placedDate != null ? bet.placedDate.format(DATE_TIME_FORMAT) : null,
      matchId: bet.matchId
    });
  }

  previousState() {
    window.history.back();
  }

  save() {
    this.isSaving = true;
    const bet = this.createFromForm();
    if (bet.id !== undefined) {
      this.subscribeToSaveResponse(this.betService.update(bet));
    } else {
      this.subscribeToSaveResponse(this.betService.create(bet));
    }
  }

  private createFromForm(): IBet {
    const entity = {
      ...new Bet(),
      id: this.editForm.get(['id']).value,
      amount: this.editForm.get(['amount']).value,
      odds: this.editForm.get(['odds']).value,
      placedBetMatchWinner: this.editForm.get(['placedBetMatchWinner']).value,
      status: this.editForm.get(['status']).value,
      placedDate:
        this.editForm.get(['placedDate']).value != null ? moment(this.editForm.get(['placedDate']).value, DATE_TIME_FORMAT) : undefined,
      matchId: this.editForm.get(['matchId']).value
    };
    return entity;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IBet>>) {
    result.subscribe((res: HttpResponse<IBet>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
  }

  protected onSaveSuccess() {
    this.isSaving = false;
    this.previousState();
  }

  protected onSaveError() {
    this.isSaving = false;
  }
  protected onError(errorMessage: string) {
    this.jhiAlertService.error(errorMessage, null, null);
  }

  trackMatchById(index: number, item: IMatch) {
    return item.id;
  }
}
