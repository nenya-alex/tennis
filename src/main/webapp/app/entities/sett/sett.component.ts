import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Subscription } from 'rxjs/Subscription';
import { JhiEventManager, JhiAlertService } from 'ng-jhipster';

import { Sett } from './sett.model';
import { SettService } from './sett.service';
import { Principal } from '../../shared';

@Component({
    selector: 'jhi-sett',
    templateUrl: './sett.component.html'
})
export class SettComponent implements OnInit, OnDestroy {
setts: Sett[];
    currentAccount: any;
    eventSubscriber: Subscription;

    constructor(
        private settService: SettService,
        private jhiAlertService: JhiAlertService,
        private eventManager: JhiEventManager,
        private principal: Principal
    ) {
    }

    loadAll() {
        this.settService.query().subscribe(
            (res: HttpResponse<Sett[]>) => {
                this.setts = res.body;
            },
            (res: HttpErrorResponse) => this.onError(res.message)
        );
    }
    ngOnInit() {
        this.loadAll();
        this.principal.identity().then((account) => {
            this.currentAccount = account;
        });
        this.registerChangeInSetts();
    }

    ngOnDestroy() {
        this.eventManager.destroy(this.eventSubscriber);
    }

    trackId(index: number, item: Sett) {
        return item.id;
    }
    registerChangeInSetts() {
        this.eventSubscriber = this.eventManager.subscribe('settListModification', (response) => this.loadAll());
    }

    private onError(error) {
        this.jhiAlertService.error(error.message, null, null);
    }
}
