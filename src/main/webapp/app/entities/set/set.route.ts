import { Routes } from '@angular/router';

import { UserRouteAccessService } from '../../shared';
import { SetComponent } from './set.component';
import { SetDetailComponent } from './set-detail.component';
import { SetPopupComponent } from './set-dialog.component';
import { SetDeletePopupComponent } from './set-delete-dialog.component';

export const setRoute: Routes = [
    {
        path: 'set',
        component: SetComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Sets'
        },
        canActivate: [UserRouteAccessService]
    }, {
        path: 'set/:id',
        component: SetDetailComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Sets'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const setPopupRoute: Routes = [
    {
        path: 'set-new',
        component: SetPopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Sets'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'set/:id/edit',
        component: SetPopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Sets'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'set/:id/delete',
        component: SetDeletePopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Sets'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
