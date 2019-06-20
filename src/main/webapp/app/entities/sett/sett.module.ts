import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { TennisSharedModule } from '../../shared';
import {
    SettService,
    SettPopupService,
    SettComponent,
    SettDetailComponent,
    SettDialogComponent,
    SettPopupComponent,
    SettDeletePopupComponent,
    SettDeleteDialogComponent,
    settRoute,
    settPopupRoute,
} from './';

const ENTITY_STATES = [
    ...settRoute,
    ...settPopupRoute,
];

@NgModule({
    imports: [
        TennisSharedModule,
        RouterModule.forChild(ENTITY_STATES)
    ],
    declarations: [
        SettComponent,
        SettDetailComponent,
        SettDialogComponent,
        SettDeleteDialogComponent,
        SettPopupComponent,
        SettDeletePopupComponent,
    ],
    entryComponents: [
        SettComponent,
        SettDialogComponent,
        SettPopupComponent,
        SettDeleteDialogComponent,
        SettDeletePopupComponent,
    ],
    providers: [
        SettService,
        SettPopupService,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class TennisSettModule {}
