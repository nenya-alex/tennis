import {CUSTOM_ELEMENTS_SCHEMA, NgModule} from "@angular/core";
import {DatePipe} from "@angular/common";

import {
    AccountService,
    AuthServerProvider,
    CSRFService,
    HasAnyAuthorityDirective,
    JhiLoginModalComponent,
    LoginModalService,
    LoginService,
    Principal,
    StateStorageService,
    TennisSharedCommonModule,
    TennisSharedLibsModule,
    UserService
} from "./";

@NgModule({
    imports: [
        TennisSharedLibsModule,
        TennisSharedCommonModule
    ],
    declarations: [
        JhiLoginModalComponent,
        HasAnyAuthorityDirective
    ],
    providers: [
        LoginService,
        LoginModalService,
        AccountService,
        StateStorageService,
        Principal,
        CSRFService,
        AuthServerProvider,
        UserService,
        DatePipe
    ],
    entryComponents: [JhiLoginModalComponent],
    exports: [
        TennisSharedCommonModule,
        JhiLoginModalComponent,
        HasAnyAuthorityDirective,
        DatePipe
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]

})
export class TennisSharedModule {}
