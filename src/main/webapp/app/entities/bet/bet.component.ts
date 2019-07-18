import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { JhiEventManager, JhiAlertService } from 'ng-jhipster';

import { IBet } from 'app/shared/model/bet.model';
import { AccountService } from 'app/core';
import { BetService } from './bet.service';

@Component({
  selector: 'jhi-bet',
  templateUrl: './bet.component.html'
})
export class BetComponent implements OnInit, OnDestroy {
  bets: IBet[];
  currentAccount: any;
  eventSubscriber: Subscription;

  constructor(
    protected betService: BetService,
    protected jhiAlertService: JhiAlertService,
    protected eventManager: JhiEventManager,
    protected accountService: AccountService
  ) {}

  loadAll() {
    this.betService
      .query()
      .pipe(
        filter((res: HttpResponse<IBet[]>) => res.ok),
        map((res: HttpResponse<IBet[]>) => res.body)
      )
      .subscribe(
        (res: IBet[]) => {
          this.bets = res;
        },
        (res: HttpErrorResponse) => this.onError(res.message)
      );
  }

  ngOnInit() {
    this.loadAll();
    this.accountService.identity().then(account => {
      this.currentAccount = account;
    });
    this.registerChangeInBets();
  }

  ngOnDestroy() {
    this.eventManager.destroy(this.eventSubscriber);
  }

  trackId(index: number, item: IBet) {
    return item.id;
  }

  registerChangeInBets() {
    this.eventSubscriber = this.eventManager.subscribe('betListModification', response => this.loadAll());
  }

  protected onError(errorMessage: string) {
    this.jhiAlertService.error(errorMessage, null, null);
  }
}
