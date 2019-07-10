import {CUSTOM_ELEMENTS_SCHEMA, NgModule} from "@angular/core";
import {RouterModule} from "@angular/router";

import {TennisSharedModule} from "../shared";

import {HOME_ROUTE, HomeComponent} from "./";

@NgModule({
    imports: [
        TennisSharedModule,
        RouterModule.forChild([ HOME_ROUTE ])
    ],
    declarations: [
        HomeComponent,
    ],
    entryComponents: [
    ],
    providers: [
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class TennisHomeModule {}
