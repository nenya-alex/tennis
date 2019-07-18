import { NgModule } from '@angular/core';

import { TennisSharedLibsModule, JhiAlertComponent, JhiAlertErrorComponent } from './';

@NgModule({
  imports: [TennisSharedLibsModule],
  declarations: [JhiAlertComponent, JhiAlertErrorComponent],
  exports: [TennisSharedLibsModule, JhiAlertComponent, JhiAlertErrorComponent]
})
export class TennisSharedCommonModule {}
