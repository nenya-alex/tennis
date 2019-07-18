import {CUSTOM_ELEMENTS_SCHEMA, NgModule} from "@angular/core";
import {RouterModule} from "@angular/router";

import {TennisSharedModule} from "app/shared";
import {
    OddsComponent,
    OddsDeleteDialogComponent,
    OddsDeletePopupComponent,
    OddsDetailComponent,
    oddsPopupRoute,
    oddsRoute,
    OddsUpdateComponent
} from "./";

const ENTITY_STATES = [...oddsRoute, ...oddsPopupRoute];

@NgModule({
    imports: [TennisSharedModule, RouterModule.forChild(ENTITY_STATES)],
    declarations: [OddsComponent, OddsDetailComponent, OddsUpdateComponent, OddsDeleteDialogComponent, OddsDeletePopupComponent],
    entryComponents: [OddsComponent, OddsUpdateComponent, OddsDeleteDialogComponent, OddsDeletePopupComponent],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class TennisOddsModule {
}
