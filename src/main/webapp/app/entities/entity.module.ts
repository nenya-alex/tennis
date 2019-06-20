import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

import { TennisGameModule } from './game/game.module';
import { TennisSetModule } from './set/set.module';
import { TennisMatchModule } from './match/match.module';
import { TennisSettModule } from './sett/sett.module';
/* jhipster-needle-add-entity-module-import - JHipster will add entity modules imports here */

@NgModule({
    imports: [
        TennisGameModule,
        TennisSetModule,
        TennisMatchModule,
        TennisSettModule,
        /* jhipster-needle-add-entity-module - JHipster will add entity modules here */
    ],
    declarations: [],
    entryComponents: [],
    providers: [],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class TennisEntityModule {}
