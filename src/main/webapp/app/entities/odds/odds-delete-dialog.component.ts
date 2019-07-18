import {Component, OnDestroy, OnInit} from "@angular/core";
import {ActivatedRoute, Router} from "@angular/router";

import {NgbActiveModal, NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {JhiEventManager} from "ng-jhipster";

import {IOdds} from "app/shared/model/odds.model";
import {OddsService} from "./odds.service";

@Component({
    selector: 'jhi-odds-delete-dialog',
    templateUrl: './odds-delete-dialog.component.html'
})
export class OddsDeleteDialogComponent {
    odds: IOdds;

    constructor(protected oddsService: OddsService, public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: number) {
        this.oddsService.delete(id).subscribe(response => {
            this.eventManager.broadcast({
                name: 'oddsListModification',
                content: 'Deleted an odds'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-odds-delete-popup',
    template: ''
})
export class OddsDeletePopupComponent implements OnInit, OnDestroy {
    protected ngbModalRef: NgbModalRef;

    constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {
    }

    ngOnInit() {
        this.activatedRoute.data.subscribe(({odds}) => {
            setTimeout(() => {
                this.ngbModalRef = this.modalService.open(OddsDeleteDialogComponent as Component, {
                    size: 'lg',
                    backdrop: 'static'
                });
                this.ngbModalRef.componentInstance.odds = odds;
                this.ngbModalRef.result.then(
                    result => {
                        this.router.navigate(['/odds', {outlets: {popup: null}}]);
                        this.ngbModalRef = null;
                    },
                    reason => {
                        this.router.navigate(['/odds', {outlets: {popup: null}}]);
                        this.ngbModalRef = null;
                    }
                );
            }, 0);
        });
    }

    ngOnDestroy() {
        this.ngbModalRef = null;
    }
}
