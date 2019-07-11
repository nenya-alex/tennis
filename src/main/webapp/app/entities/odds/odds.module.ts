import {NgModule, CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';
import {RouterModule} from '@angular/router';

import {TennisSharedModule} from 'app/shared';
import {
    OddsComponent,
    OddsDetailComponent,
    OddsUpdateComponent,
    OddsDeletePopupComponent,
    OddsDeleteDialogComponent,
    oddsRoute,
    oddsPopupRoute
} from './';

const ENTITY_STATES = [...oddsRoute, ...oddsPopupRoute];

@NgModule({
    imports: [TennisSharedModule, RouterModule.forChild(ENTITY_STATES)],
    declarations: [OddsComponent, OddsDetailComponent, OddsUpdateComponent, OddsDeleteDialogComponent, OddsDeletePopupComponent],
    entryComponents: [OddsComponent, OddsUpdateComponent, OddsDeleteDialogComponent, OddsDeletePopupComponent],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class TennisOddsModule {
}
