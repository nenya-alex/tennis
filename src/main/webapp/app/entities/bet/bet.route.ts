import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { Bet } from 'app/shared/model/bet.model';
import { BetService } from './bet.service';
import { BetComponent } from './bet.component';
import { BetDetailComponent } from './bet-detail.component';
import { BetUpdateComponent } from './bet-update.component';
import { BetDeletePopupComponent } from './bet-delete-dialog.component';
import { IBet } from 'app/shared/model/bet.model';

@Injectable({ providedIn: 'root' })
export class BetResolve implements Resolve<IBet> {
  constructor(private service: BetService) {}

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<IBet> {
    const id = route.params['id'] ? route.params['id'] : null;
    if (id) {
      return this.service.find(id).pipe(
        filter((response: HttpResponse<Bet>) => response.ok),
        map((bet: HttpResponse<Bet>) => bet.body)
      );
    }
    return of(new Bet());
  }
}

export const betRoute: Routes = [
  {
    path: '',
    component: BetComponent,
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'Bets'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: BetDetailComponent,
    resolve: {
      bet: BetResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'Bets'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: BetUpdateComponent,
    resolve: {
      bet: BetResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'Bets'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: BetUpdateComponent,
    resolve: {
      bet: BetResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'Bets'
    },
    canActivate: [UserRouteAccessService]
  }
];

export const betPopupRoute: Routes = [
  {
    path: ':id/delete',
    component: BetDeletePopupComponent,
    resolve: {
      bet: BetResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'Bets'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  }
];
