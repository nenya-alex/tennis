import {Component, OnDestroy, OnInit} from "@angular/core";
import {ActivatedRoute} from "@angular/router";
import {HttpResponse} from "@angular/common/http";
import {Subscription} from "rxjs/Subscription";
import {JhiEventManager} from "ng-jhipster";

import {Set} from "./set.model";
import {SetService} from "./set.service";

@Component({
    selector: 'jhi-set-detail',
    templateUrl: './set-detail.component.html'
})
export class SetDetailComponent implements OnInit, OnDestroy {

    set: Set;
    private subscription: Subscription;
    private eventSubscriber: Subscription;

    constructor(
        private eventManager: JhiEventManager,
        private setService: SetService,
        private route: ActivatedRoute
    ) {
    }

    ngOnInit() {
        this.subscription = this.route.params.subscribe((params) => {
            this.load(params['id']);
        });
        this.registerChangeInSets();
    }

    load(id) {
        this.setService.find(id)
            .subscribe((setResponse: HttpResponse<Set>) => {
                this.set = setResponse.body;
            });
    }
    previousState() {
        window.history.back();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
        this.eventManager.destroy(this.eventSubscriber);
    }

    registerChangeInSets() {
        this.eventSubscriber = this.eventManager.subscribe(
            'setListModification',
            (response) => this.load(this.set.id)
        );
    }
}
