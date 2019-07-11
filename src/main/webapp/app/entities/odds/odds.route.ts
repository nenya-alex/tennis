import {Injectable} from '@angular/core';
import {HttpResponse} from '@angular/common/http';
import {Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes} from '@angular/router';
import {UserRouteAccessService} from 'app/core';
import {Observable, of} from 'rxjs';
import {filter, map} from 'rxjs/operators';
import {Odds} from 'app/shared/model/odds.model';
import {OddsService} from './odds.service';
import {OddsComponent} from './odds.component';
import {OddsDetailComponent} from './odds-detail.component';
import {OddsUpdateComponent} from './odds-update.component';
import {OddsDeletePopupComponent} from './odds-delete-dialog.component';
import {IOdds} from 'app/shared/model/odds.model';

@Injectable({providedIn: 'root'})
export class OddsResolve implements Resolve<IOdds> {
    constructor(private service: OddsService) {
    }

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<IOdds> {
        const id = route.params['id'] ? route.params['id'] : null;
        if (id) {
            return this.service.find(id).pipe(
                filter((response: HttpResponse<Odds>) => response.ok),
                map((odds: HttpResponse<Odds>) => odds.body)
            );
        }
        return of(new Odds());
    }
}

export const oddsRoute: Routes = [
    {
        path: '',
        component: OddsComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Odds'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: ':id/view',
        component: OddsDetailComponent,
        resolve: {
            odds: OddsResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Odds'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'new',
        component: OddsUpdateComponent,
        resolve: {
            odds: OddsResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Odds'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: ':id/edit',
        component: OddsUpdateComponent,
        resolve: {
            odds: OddsResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Odds'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const oddsPopupRoute: Routes = [
    {
        path: ':id/delete',
        component: OddsDeletePopupComponent,
        resolve: {
            odds: OddsResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Odds'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
