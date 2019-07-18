import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IBet } from 'app/shared/model/bet.model';
import { BetService } from './bet.service';

@Component({
  selector: 'jhi-bet-delete-dialog',
  templateUrl: './bet-delete-dialog.component.html'
})
export class BetDeleteDialogComponent {
  bet: IBet;

  constructor(protected betService: BetService, public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {}

  clear() {
    this.activeModal.dismiss('cancel');
  }

  confirmDelete(id: number) {
    this.betService.delete(id).subscribe(response => {
      this.eventManager.broadcast({
        name: 'betListModification',
        content: 'Deleted an bet'
      });
      this.activeModal.dismiss(true);
    });
  }
}

@Component({
  selector: 'jhi-bet-delete-popup',
  template: ''
})
export class BetDeletePopupComponent implements OnInit, OnDestroy {
  protected ngbModalRef: NgbModalRef;

  constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ bet }) => {
      setTimeout(() => {
        this.ngbModalRef = this.modalService.open(BetDeleteDialogComponent as Component, { size: 'lg', backdrop: 'static' });
        this.ngbModalRef.componentInstance.bet = bet;
        this.ngbModalRef.result.then(
          result => {
            this.router.navigate(['/bet', { outlets: { popup: null } }]);
            this.ngbModalRef = null;
          },
          reason => {
            this.router.navigate(['/bet', { outlets: { popup: null } }]);
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
