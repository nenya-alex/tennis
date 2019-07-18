import {Component, OnInit} from "@angular/core";
import {ActivatedRoute} from "@angular/router";

import {IOdds} from "app/shared/model/odds.model";

@Component({
    selector: 'jhi-odds-detail',
    templateUrl: './odds-detail.component.html'
})
export class OddsDetailComponent implements OnInit {
    odds: IOdds;

    constructor(protected activatedRoute: ActivatedRoute) {
    }

    ngOnInit() {
        this.activatedRoute.data.subscribe(({odds}) => {
            this.odds = odds;
        });
    }

    previousState() {
        window.history.back();
    }
}
