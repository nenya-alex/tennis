import { Routes } from '@angular/router';

import { UserRouteAccessService } from '../../shared';
import { SettComponent } from './sett.component';
import { SettDetailComponent } from './sett-detail.component';
import { SettPopupComponent } from './sett-dialog.component';
import { SettDeletePopupComponent } from './sett-delete-dialog.component';

export const settRoute: Routes = [
    {
        path: 'sett',
        component: SettComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Setts'
        },
        canActivate: [UserRouteAccessService]
    }, {
        path: 'sett/:id',
        component: SettDetailComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Setts'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const settPopupRoute: Routes = [
    {
        path: 'sett-new',
        component: SettPopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Setts'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'sett/:id/edit',
        component: SettPopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Setts'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'sett/:id/delete',
        component: SettDeletePopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Setts'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
