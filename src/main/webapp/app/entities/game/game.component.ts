import {Component, OnDestroy, OnInit} from "@angular/core";
import {HttpErrorResponse, HttpResponse} from "@angular/common/http";
import {Subscription} from "rxjs/Subscription";
import {JhiAlertService, JhiEventManager} from "ng-jhipster";

import {Game} from "./game.model";
import {GameService} from "./game.service";
import {Principal} from "../../shared";

@Component({
    selector: 'jhi-game',
    templateUrl: './game.component.html'
})
export class GameComponent implements OnInit, OnDestroy {
games: Game[];
    currentAccount: any;
    eventSubscriber: Subscription;

    constructor(
        private gameService: GameService,
        private jhiAlertService: JhiAlertService,
        private eventManager: JhiEventManager,
        private principal: Principal
    ) {
    }

    loadAll() {
        this.gameService.query().subscribe(
            (res: HttpResponse<Game[]>) => {
                this.games = res.body;
            },
            (res: HttpErrorResponse) => this.onError(res.message)
        );
    }
    ngOnInit() {
        this.loadAll();
        this.principal.identity().then((account) => {
            this.currentAccount = account;
        });
        this.registerChangeInGames();
    }

    ngOnDestroy() {
        this.eventManager.destroy(this.eventSubscriber);
    }

    trackId(index: number, item: Game) {
        return item.id;
    }
    registerChangeInGames() {
        this.eventSubscriber = this.eventManager.subscribe('gameListModification', (response) => this.loadAll());
    }

    private onError(error) {
        this.jhiAlertService.error(error.message, null, null);
    }
}
