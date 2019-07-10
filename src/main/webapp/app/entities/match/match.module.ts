import {CUSTOM_ELEMENTS_SCHEMA, NgModule} from "@angular/core";
import {RouterModule} from "@angular/router";

import {TennisSharedModule} from "../../shared";
import {
    MatchComponent,
    MatchDeleteDialogComponent,
    MatchDeletePopupComponent,
    MatchDetailComponent,
    MatchDialogComponent,
    MatchPopupComponent,
    matchPopupRoute,
    MatchPopupService,
    matchRoute,
    MatchService
} from "./";

const ENTITY_STATES = [
    ...matchRoute,
    ...matchPopupRoute,
];

@NgModule({
    imports: [
        TennisSharedModule,
        RouterModule.forChild(ENTITY_STATES)
    ],
    declarations: [
        MatchComponent,
        MatchDetailComponent,
        MatchDialogComponent,
        MatchDeleteDialogComponent,
        MatchPopupComponent,
        MatchDeletePopupComponent,
    ],
    entryComponents: [
        MatchComponent,
        MatchDialogComponent,
        MatchPopupComponent,
        MatchDeleteDialogComponent,
        MatchDeletePopupComponent,
    ],
    providers: [
        MatchService,
        MatchPopupService,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class TennisMatchModule {}
