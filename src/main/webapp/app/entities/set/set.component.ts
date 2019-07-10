import {Component, OnDestroy, OnInit} from "@angular/core";
import {HttpErrorResponse, HttpResponse} from "@angular/common/http";
import {Subscription} from "rxjs/Subscription";
import {JhiAlertService, JhiEventManager} from "ng-jhipster";

import {Set} from "./set.model";
import {SetService} from "./set.service";
import {Principal} from "../../shared";

@Component({
    selector: 'jhi-set',
    templateUrl: './set.component.html'
})
export class SetComponent implements OnInit, OnDestroy {
sets: Set[];
    currentAccount: any;
    eventSubscriber: Subscription;

    constructor(
        private setService: SetService,
        private jhiAlertService: JhiAlertService,
        private eventManager: JhiEventManager,
        private principal: Principal
    ) {
    }

    loadAll() {
        this.setService.query().subscribe(
            (res: HttpResponse<Set[]>) => {
                this.sets = res.body;
            },
            (res: HttpErrorResponse) => this.onError(res.message)
        );
    }
    ngOnInit() {
        this.loadAll();
        this.principal.identity().then((account) => {
            this.currentAccount = account;
        });
        this.registerChangeInSets();
    }

    ngOnDestroy() {
        this.eventManager.destroy(this.eventSubscriber);
    }

    trackId(index: number, item: Set) {
        return item.id;
    }
    registerChangeInSets() {
        this.eventSubscriber = this.eventManager.subscribe('setListModification', (response) => this.loadAll());
    }

    private onError(error) {
        this.jhiAlertService.error(error.message, null, null);
    }
}
