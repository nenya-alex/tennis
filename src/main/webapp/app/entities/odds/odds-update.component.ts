import {Component, OnInit} from "@angular/core";
import {HttpErrorResponse, HttpResponse} from "@angular/common/http";
import {FormBuilder, Validators} from "@angular/forms";
import {ActivatedRoute} from "@angular/router";
import {Observable} from "rxjs";
import {filter, map} from "rxjs/operators";
import * as moment from "moment";
import {DATE_TIME_FORMAT} from "app/shared/constants/input.constants";
import {JhiAlertService} from "ng-jhipster";
import {IOdds, Odds} from "app/shared/model/odds.model";
import {OddsService} from "./odds.service";
import {IMatch} from "app/shared/model/match.model";
import {MatchService} from "app/entities/match";

@Component({
    selector: 'jhi-odds-update',
    templateUrl: './odds-update.component.html'
})
export class OddsUpdateComponent implements OnInit {
    odds: IOdds;
    isSaving: boolean;

    matches: IMatch[];

    editForm = this.fb.group({
        id: [],
        homeOdds: [],
        awayOdds: [],
        checkDate: [],
        matchId: []
    });

    constructor(protected jhiAlertService: JhiAlertService,
                protected oddsService: OddsService,
                protected matchService: MatchService,
                protected activatedRoute: ActivatedRoute,
                private fb: FormBuilder) {
    }

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({odds}) => {
            this.updateForm(odds);
            this.odds = odds;
        });
        this.matchService
            .query()
            .pipe(
                filter((mayBeOk: HttpResponse<IMatch[]>) => mayBeOk.ok),
                map((response: HttpResponse<IMatch[]>) => response.body)
            )
            .subscribe((res: IMatch[]) => (this.matches = res), (res: HttpErrorResponse) => this.onError(res.message));
    }

    updateForm(odds: IOdds) {
        this.editForm.patchValue({
            id: odds.id,
            homeOdds: odds.homeOdds,
            awayOdds: odds.awayOdds,
            checkDate: odds.checkDate != null ? odds.checkDate.format(DATE_TIME_FORMAT) : null,
            matchId: odds.matchId
        });
    }

    previousState() {
        window.history.back();
    }

    save() {
        this.isSaving = true;
        const odds = this.createFromForm();
        if (odds.id !== undefined) {
            this.subscribeToSaveResponse(this.oddsService.update(odds));
        } else {
            this.subscribeToSaveResponse(this.oddsService.create(odds));
        }
    }

    private createFromForm(): IOdds {
        const entity = {
            ...new Odds(),
            id: this.editForm.get(['id']).value,
            homeOdds: this.editForm.get(['homeOdds']).value,
            awayOdds: this.editForm.get(['awayOdds']).value,
            checkDate: this.editForm.get(['checkDate']).value != null ? moment(this.editForm.get(['checkDate']).value, DATE_TIME_FORMAT) : undefined,
            matchId: this.editForm.get(['matchId']).value
        };
        return entity;
    }

    protected subscribeToSaveResponse(result: Observable<HttpResponse<IOdds>>) {
        result.subscribe((res: HttpResponse<IOdds>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
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
