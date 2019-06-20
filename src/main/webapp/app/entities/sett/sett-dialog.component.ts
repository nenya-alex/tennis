import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';

import { Observable } from 'rxjs/Observable';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager, JhiAlertService } from 'ng-jhipster';

import { Sett } from './sett.model';
import { SettPopupService } from './sett-popup.service';
import { SettService } from './sett.service';
import { Match, MatchService } from '../match';

@Component({
    selector: 'jhi-sett-dialog',
    templateUrl: './sett-dialog.component.html'
})
export class SettDialogComponent implements OnInit {

    sett: Sett;
    isSaving: boolean;

    matches: Match[];

    constructor(
        public activeModal: NgbActiveModal,
        private jhiAlertService: JhiAlertService,
        private settService: SettService,
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
        if (this.sett.id !== undefined) {
            this.subscribeToSaveResponse(
                this.settService.update(this.sett));
        } else {
            this.subscribeToSaveResponse(
                this.settService.create(this.sett));
        }
    }

    private subscribeToSaveResponse(result: Observable<HttpResponse<Sett>>) {
        result.subscribe((res: HttpResponse<Sett>) =>
            this.onSaveSuccess(res.body), (res: HttpErrorResponse) => this.onSaveError());
    }

    private onSaveSuccess(result: Sett) {
        this.eventManager.broadcast({ name: 'settListModification', content: 'OK'});
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
    selector: 'jhi-sett-popup',
    template: ''
})
export class SettPopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private settPopupService: SettPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            if ( params['id'] ) {
                this.settPopupService
                    .open(SettDialogComponent as Component, params['id']);
            } else {
                this.settPopupService
                    .open(SettDialogComponent as Component);
            }
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
