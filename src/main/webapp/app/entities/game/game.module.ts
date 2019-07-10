import {CUSTOM_ELEMENTS_SCHEMA, NgModule} from "@angular/core";
import {RouterModule} from "@angular/router";

import {TennisSharedModule} from "../../shared";
import {
    GameComponent,
    GameDeleteDialogComponent,
    GameDeletePopupComponent,
    GameDetailComponent,
    GameDialogComponent,
    GamePopupComponent,
    gamePopupRoute,
    GamePopupService,
    gameRoute,
    GameService
} from "./";

const ENTITY_STATES = [
    ...gameRoute,
    ...gamePopupRoute,
];

@NgModule({
    imports: [
        TennisSharedModule,
        RouterModule.forChild(ENTITY_STATES)
    ],
    declarations: [
        GameComponent,
        GameDetailComponent,
        GameDialogComponent,
        GameDeleteDialogComponent,
        GamePopupComponent,
        GameDeletePopupComponent,
    ],
    entryComponents: [
        GameComponent,
        GameDialogComponent,
        GamePopupComponent,
        GameDeleteDialogComponent,
        GameDeletePopupComponent,
    ],
    providers: [
        GameService,
        GamePopupService,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class TennisGameModule {}
