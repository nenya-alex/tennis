import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { TennisSharedModule } from 'app/shared';
import {
  BetComponent,
  BetDetailComponent,
  BetUpdateComponent,
  BetDeletePopupComponent,
  BetDeleteDialogComponent,
  betRoute,
  betPopupRoute
} from './';

const ENTITY_STATES = [...betRoute, ...betPopupRoute];

@NgModule({
  imports: [TennisSharedModule, RouterModule.forChild(ENTITY_STATES)],
  declarations: [BetComponent, BetDetailComponent, BetUpdateComponent, BetDeleteDialogComponent, BetDeletePopupComponent],
  entryComponents: [BetComponent, BetUpdateComponent, BetDeleteDialogComponent, BetDeletePopupComponent],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class TennisBetModule {}
