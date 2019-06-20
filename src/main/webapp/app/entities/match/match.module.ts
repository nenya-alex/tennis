import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { TennisSharedModule } from '../../shared';
import {
    MatchService,
    MatchPopupService,
    MatchComponent,
    MatchDetailComponent,
    MatchDialogComponent,
    MatchPopupComponent,
    MatchDeletePopupComponent,
    MatchDeleteDialogComponent,
    matchRoute,
    matchPopupRoute,
} from './';

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
