import {Component, OnDestroy, OnInit} from "@angular/core";
import {ActivatedRoute} from "@angular/router";
import {HttpResponse} from "@angular/common/http";
import {Subscription} from "rxjs/Subscription";
import {JhiEventManager} from "ng-jhipster";

import {Game} from "./game.model";
import {GameService} from "./game.service";

@Component({
    selector: 'jhi-game-detail',
    templateUrl: './game-detail.component.html'
})
export class GameDetailComponent implements OnInit, OnDestroy {

    game: Game;
    private subscription: Subscription;
    private eventSubscriber: Subscription;

    constructor(
        private eventManager: JhiEventManager,
        private gameService: GameService,
        private route: ActivatedRoute
    ) {
    }

    ngOnInit() {
        this.subscription = this.route.params.subscribe((params) => {
            this.load(params['id']);
        });
        this.registerChangeInGames();
    }

    load(id) {
        this.gameService.find(id)
            .subscribe((gameResponse: HttpResponse<Game>) => {
                this.game = gameResponse.body;
            });
    }
    previousState() {
        window.history.back();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
        this.eventManager.destroy(this.eventSubscriber);
    }

    registerChangeInGames() {
        this.eventSubscriber = this.eventManager.subscribe(
            'gameListModification',
            (response) => this.load(this.game.id)
        );
    }
}
