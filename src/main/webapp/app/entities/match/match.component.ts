import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Subscription } from 'rxjs/Subscription';
import { JhiEventManager, JhiAlertService } from 'ng-jhipster';

import { Match } from './match.model';
import { MatchService } from './match.service';
import { Principal } from '../../shared';

@Component({
    selector: 'jhi-match',
    templateUrl: './match.component.html'
})
export class MatchComponent implements OnInit, OnDestroy {
matches: Match[];
    currentAccount: any;
    eventSubscriber: Subscription;

    constructor(
        private matchService: MatchService,
        private jhiAlertService: JhiAlertService,
        private eventManager: JhiEventManager,
        private principal: Principal
    ) {
    }

    loadAll() {
        this.matchService.query().subscribe(
            (res: HttpResponse<Match[]>) => {
                this.matches = res.body;
            },
            (res: HttpErrorResponse) => this.onError(res.message)
        );
    }
    ngOnInit() {
        this.loadAll();
        this.principal.identity().then((account) => {
            this.currentAccount = account;
        });
        this.registerChangeInMatches();
    }

    ngOnDestroy() {
        this.eventManager.destroy(this.eventSubscriber);
    }

    trackId(index: number, item: Match) {
        return item.id;
    }
    registerChangeInMatches() {
        this.eventSubscriber = this.eventManager.subscribe('matchListModification', (response) => this.loadAll());
    }

    private onError(error) {
        this.jhiAlertService.error(error.message, null, null);
    }
}
