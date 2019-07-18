import {CUSTOM_ELEMENTS_SCHEMA, NgModule} from "@angular/core";
import {RouterModule} from "@angular/router";

import {TennisSharedModule} from "app/shared";
import {
    BetComponent,
    BetDeleteDialogComponent,
    BetDeletePopupComponent,
    BetDetailComponent,
    betPopupRoute,
    betRoute,
    BetUpdateComponent
} from "./";

const ENTITY_STATES = [...betRoute, ...betPopupRoute];

@NgModule({
  imports: [TennisSharedModule, RouterModule.forChild(ENTITY_STATES)],
  declarations: [BetComponent, BetDetailComponent, BetUpdateComponent, BetDeleteDialogComponent, BetDeletePopupComponent],
  entryComponents: [BetComponent, BetUpdateComponent, BetDeleteDialogComponent, BetDeletePopupComponent],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class TennisBetModule {}
