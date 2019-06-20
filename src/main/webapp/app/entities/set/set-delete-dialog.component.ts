import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { Set } from './set.model';
import { SetPopupService } from './set-popup.service';
import { SetService } from './set.service';

@Component({
    selector: 'jhi-set-delete-dialog',
    templateUrl: './set-delete-dialog.component.html'
})
export class SetDeleteDialogComponent {

    set: Set;

    constructor(
        private setService: SetService,
        public activeModal: NgbActiveModal,
        private eventManager: JhiEventManager
    ) {
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: number) {
        this.setService.delete(id).subscribe((response) => {
            this.eventManager.broadcast({
                name: 'setListModification',
                content: 'Deleted an set'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-set-delete-popup',
    template: ''
})
export class SetDeletePopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private setPopupService: SetPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            this.setPopupService
                .open(SetDeleteDialogComponent as Component, params['id']);
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
