import "./vendor.ts";

import {Injector, NgModule} from "@angular/core";
import {BrowserModule} from "@angular/platform-browser";
import {HTTP_INTERCEPTORS} from "@angular/common/http";
import {LocalStorageService, Ng2Webstorage, SessionStorageService} from "ngx-webstorage";
import {JhiEventManager} from "ng-jhipster";

import {AuthInterceptor} from "./blocks/interceptor/auth.interceptor";
import {AuthExpiredInterceptor} from "./blocks/interceptor/auth-expired.interceptor";
import {ErrorHandlerInterceptor} from "./blocks/interceptor/errorhandler.interceptor";
import {NotificationInterceptor} from "./blocks/interceptor/notification.interceptor";
import {TennisSharedModule, UserRouteAccessService} from "./shared";
import {TennisAppRoutingModule} from "./app-routing.module";
import {TennisHomeModule} from "./home/home.module";
import {TennisAdminModule} from "./admin/admin.module";
import {TennisAccountModule} from "./account/account.module";
import {TennisEntityModule} from "./entities/entity.module";
import {PaginationConfig} from "./blocks/config/uib-pagination.config";
// jhipster-needle-angular-add-module-import JHipster will add new module here
import {
    ErrorComponent,
    FooterComponent,
    JhiMainComponent,
    NavbarComponent,
    PageRibbonComponent,
    ProfileService
} from "./layouts";

@NgModule({
    imports: [
        BrowserModule,
        TennisAppRoutingModule,
        Ng2Webstorage.forRoot({ prefix: 'jhi', separator: '-'}),
        TennisSharedModule,
        TennisHomeModule,
        TennisAdminModule,
        TennisAccountModule,
        TennisEntityModule,
        // jhipster-needle-angular-add-module JHipster will add new module here
    ],
    declarations: [
        JhiMainComponent,
        NavbarComponent,
        ErrorComponent,
        PageRibbonComponent,
        FooterComponent
    ],
    providers: [
        ProfileService,
        PaginationConfig,
        UserRouteAccessService,
        {
            provide: HTTP_INTERCEPTORS,
            useClass: AuthInterceptor,
            multi: true,
            deps: [
                LocalStorageService,
                SessionStorageService
            ]
        },
        {
            provide: HTTP_INTERCEPTORS,
            useClass: AuthExpiredInterceptor,
            multi: true,
            deps: [
                Injector
            ]
        },
        {
            provide: HTTP_INTERCEPTORS,
            useClass: ErrorHandlerInterceptor,
            multi: true,
            deps: [
                JhiEventManager
            ]
        },
        {
            provide: HTTP_INTERCEPTORS,
            useClass: NotificationInterceptor,
            multi: true,
            deps: [
                Injector
            ]
        }
    ],
    bootstrap: [ JhiMainComponent ]
})
export class TennisAppModule {}
