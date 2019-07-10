import {CUSTOM_ELEMENTS_SCHEMA, NgModule} from "@angular/core";
import {RouterModule} from "@angular/router";

import {TennisSharedModule} from "../../shared";
import {
    SettComponent,
    SettDeleteDialogComponent,
    SettDeletePopupComponent,
    SettDetailComponent,
    SettDialogComponent,
    SettPopupComponent,
    settPopupRoute,
    SettPopupService,
    settRoute,
    SettService
} from "./";

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
