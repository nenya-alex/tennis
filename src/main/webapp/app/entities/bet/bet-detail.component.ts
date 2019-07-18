import {Component, OnInit} from "@angular/core";
import {ActivatedRoute} from "@angular/router";

import {IBet} from "app/shared/model/bet.model";

@Component({
  selector: 'jhi-bet-detail',
  templateUrl: './bet-detail.component.html'
})
export class BetDetailComponent implements OnInit {
  bet: IBet;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ bet }) => {
      this.bet = bet;
    });
  }

  previousState() {
    window.history.back();
  }
}
