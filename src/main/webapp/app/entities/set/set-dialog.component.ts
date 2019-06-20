import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';

import { Observable } from 'rxjs/Observable';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager, JhiAlertService } from 'ng-jhipster';

import { Set } from './set.model';
import { SetPopupService } from './set-popup.service';
import { SetService } from './set.service';
import { Match, MatchService } from '../match';

@Component({
    selector: 'jhi-set-dialog',
    templateUrl: './set-dialog.component.html'
})
export class SetDialogComponent implements OnInit {

    set: Set;
    isSaving: boolean;

    matches: Match[];

    constructor(
        public activeModal: NgbActiveModal,
        private jhiAlertService: JhiAlertService,
        private setService: SetService,
        private matchService: MatchService,
        private eventManager: JhiEventManager
    ) {
    }

    ngOnInit() {
        this.isSaving = false;
        this.matchService.query()
            .subscribe((res: HttpResponse<Match[]>) => { this.matches = res.body; }, (res: HttpErrorResponse) => this.onError(res.message));
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    save() {
        this.isSaving = true;
        if (this.set.id !== undefined) {
            this.subscribeToSaveResponse(
                this.setService.update(this.set));
        } else {
            this.subscribeToSaveResponse(
                this.setService.create(this.set));
        }
    }

    private subscribeToSaveResponse(result: Observable<HttpResponse<Set>>) {
        result.subscribe((res: HttpResponse<Set>) =>
            this.onSaveSuccess(res.body), (res: HttpErrorResponse) => this.onSaveError());
    }

    private onSaveSuccess(result: Set) {
        this.eventManager.broadcast({ name: 'setListModification', content: 'OK'});
        this.isSaving = false;
        this.activeModal.dismiss(result);
    }

    private onSaveError() {
        this.isSaving = false;
    }

    private onError(error: any) {
        this.jhiAlertService.error(error.message, null, null);
    }

    trackMatchById(index: number, item: Match) {
        return item.id;
    }
}

@Component({
    selector: 'jhi-set-popup',
    template: ''
})
export class SetPopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private setPopupService: SetPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            if ( params['id'] ) {
                this.setPopupService
                    .open(SetDialogComponent as Component, params['id']);
            } else {
                this.setPopupService
                    .open(SetDialogComponent as Component);
            }
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
