import {Component, OnDestroy, OnInit} from "@angular/core";
import {ActivatedRoute} from "@angular/router";
import {HttpErrorResponse, HttpResponse} from "@angular/common/http";

import {Observable} from "rxjs/Observable";
import {NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {JhiEventManager} from "ng-jhipster";

import {Match} from "./match.model";
import {MatchPopupService} from "./match-popup.service";
import {MatchService} from "./match.service";

@Component({
    selector: 'jhi-match-dialog',
    templateUrl: './match-dialog.component.html'
})
export class MatchDialogComponent implements OnInit {

    match: Match;
    isSaving: boolean;

    constructor(
        public activeModal: NgbActiveModal,
        private matchService: MatchService,
        private eventManager: JhiEventManager
    ) {
    }

    ngOnInit() {
        this.isSaving = false;
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    save() {
        this.isSaving = true;
        if (this.match.id !== undefined) {
            this.subscribeToSaveResponse(
                this.matchService.update(this.match));
        } else {
            this.subscribeToSaveResponse(
                this.matchService.create(this.match));
        }
    }

    private subscribeToSaveResponse(result: Observable<HttpResponse<Match>>) {
        result.subscribe((res: HttpResponse<Match>) =>
            this.onSaveSuccess(res.body), (res: HttpErrorResponse) => this.onSaveError());
    }

    private onSaveSuccess(result: Match) {
        this.eventManager.broadcast({ name: 'matchListModification', content: 'OK'});
        this.isSaving = false;
        this.activeModal.dismiss(result);
    }

    private onSaveError() {
        this.isSaving = false;
    }
}

@Component({
    selector: 'jhi-match-popup',
    template: ''
})
export class MatchPopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private matchPopupService: MatchPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            if ( params['id'] ) {
                this.matchPopupService
                    .open(MatchDialogComponent as Component, params['id']);
            } else {
                this.matchPopupService
                    .open(MatchDialogComponent as Component);
            }
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
