import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { Sett } from './sett.model';
import { SettPopupService } from './sett-popup.service';
import { SettService } from './sett.service';

@Component({
    selector: 'jhi-sett-delete-dialog',
    templateUrl: './sett-delete-dialog.component.html'
})
export class SettDeleteDialogComponent {

    sett: Sett;

    constructor(
        private settService: SettService,
        public activeModal: NgbActiveModal,
        private eventManager: JhiEventManager
    ) {
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: number) {
        this.settService.delete(id).subscribe((response) => {
            this.eventManager.broadcast({
                name: 'settListModification',
                content: 'Deleted an sett'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-sett-delete-popup',
    template: ''
})
export class SettDeletePopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private settPopupService: SettPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            this.settPopupService
                .open(SettDeleteDialogComponent as Component, params['id']);
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
