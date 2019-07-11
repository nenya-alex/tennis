import {Component, OnInit, OnDestroy} from '@angular/core';
import {HttpErrorResponse, HttpResponse} from '@angular/common/http';
import {Subscription} from 'rxjs';
import {filter, map} from 'rxjs/operators';
import {JhiEventManager, JhiAlertService} from 'ng-jhipster';

import {IOdds} from 'app/shared/model/odds.model';
import {AccountService} from 'app/core';
import {OddsService} from './odds.service';

@Component({
    selector: 'jhi-odds',
    templateUrl: './odds.component.html'
})
export class OddsComponent implements OnInit, OnDestroy {
    odds: IOdds[];
    currentAccount: any;
    eventSubscriber: Subscription;

    constructor(protected oddsService: OddsService,
                protected jhiAlertService: JhiAlertService,
                protected eventManager: JhiEventManager,
                protected accountService: AccountService) {
    }

    loadAll() {
        this.oddsService
            .query()
            .pipe(
                filter((res: HttpResponse<IOdds[]>) => res.ok),
                map((res: HttpResponse<IOdds[]>) => res.body)
            )
            .subscribe(
                (res: IOdds[]) => {
                    this.odds = res;
                },
                (res: HttpErrorResponse) => this.onError(res.message)
            );
    }

    ngOnInit() {
        this.loadAll();
        this.accountService.identity().then(account => {
            this.currentAccount = account;
        });
        this.registerChangeInOdds();
    }

    ngOnDestroy() {
        this.eventManager.destroy(this.eventSubscriber);
    }

    trackId(index: number, item: IOdds) {
        return item.id;
    }

    registerChangeInOdds() {
        this.eventSubscriber = this.eventManager.subscribe('oddsListModification', response => this.loadAll());
    }

    protected onError(errorMessage: string) {
        this.jhiAlertService.error(errorMessage, null, null);
    }
}
