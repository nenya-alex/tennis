import {CUSTOM_ELEMENTS_SCHEMA, NgModule} from "@angular/core";
import {RouterModule} from "@angular/router";

import {TennisSharedModule} from "../../shared";
import {
    SetComponent,
    SetDeleteDialogComponent,
    SetDeletePopupComponent,
    SetDetailComponent,
    SetDialogComponent,
    SetPopupComponent,
    setPopupRoute,
    SetPopupService,
    setRoute,
    SetService
} from "./";

const ENTITY_STATES = [
    ...setRoute,
    ...setPopupRoute,
];

@NgModule({
    imports: [
        TennisSharedModule,
        RouterModule.forChild(ENTITY_STATES)
    ],
    declarations: [
        SetComponent,
        SetDetailComponent,
        SetDialogComponent,
        SetDeleteDialogComponent,
        SetPopupComponent,
        SetDeletePopupComponent,
    ],
    entryComponents: [
        SetComponent,
        SetDialogComponent,
        SetPopupComponent,
        SetDeleteDialogComponent,
        SetDeletePopupComponent,
    ],
    providers: [
        SetService,
        SetPopupService,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class TennisSetModule {}
