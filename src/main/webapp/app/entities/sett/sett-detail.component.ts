import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs/Subscription';
import { JhiEventManager } from 'ng-jhipster';

import { Sett } from './sett.model';
import { SettService } from './sett.service';

@Component({
    selector: 'jhi-sett-detail',
    templateUrl: './sett-detail.component.html'
})
export class SettDetailComponent implements OnInit, OnDestroy {

    sett: Sett;
    private subscription: Subscription;
    private eventSubscriber: Subscription;

    constructor(
        private eventManager: JhiEventManager,
        private settService: SettService,
        private route: ActivatedRoute
    ) {
    }

    ngOnInit() {
        this.subscription = this.route.params.subscribe((params) => {
            this.load(params['id']);
        });
        this.registerChangeInSetts();
    }

    load(id) {
        this.settService.find(id)
            .subscribe((settResponse: HttpResponse<Sett>) => {
                this.sett = settResponse.body;
            });
    }
    previousState() {
        window.history.back();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
        this.eventManager.destroy(this.eventSubscriber);
    }

    registerChangeInSetts() {
        this.eventSubscriber = this.eventManager.subscribe(
            'settListModification',
            (response) => this.load(this.sett.id)
        );
    }
}
