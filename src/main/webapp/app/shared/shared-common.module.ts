import {LOCALE_ID, NgModule} from "@angular/core";
import {Title} from "@angular/platform-browser";
import {registerLocaleData} from "@angular/common";
import locale from "@angular/common/locales/en";

import {JhiAlertComponent, JhiAlertErrorComponent, TennisSharedLibsModule} from "./";

@NgModule({
    imports: [
        TennisSharedLibsModule
    ],
    declarations: [
        JhiAlertComponent,
        JhiAlertErrorComponent
    ],
    providers: [
        Title,
        {
            provide: LOCALE_ID,
            useValue: 'en'
        },
    ],
    exports: [
        TennisSharedLibsModule,
        JhiAlertComponent,
        JhiAlertErrorComponent
    ]
})
export class TennisSharedCommonModule {
    constructor() {
        registerLocaleData(locale);
    }
}
