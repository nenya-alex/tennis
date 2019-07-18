import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { TennisSharedLibsModule, TennisSharedCommonModule, JhiLoginModalComponent, HasAnyAuthorityDirective } from './';

@NgModule({
  imports: [TennisSharedLibsModule, TennisSharedCommonModule],
  declarations: [JhiLoginModalComponent, HasAnyAuthorityDirective],
  entryComponents: [JhiLoginModalComponent],
  exports: [TennisSharedCommonModule, JhiLoginModalComponent, HasAnyAuthorityDirective],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class TennisSharedModule {
  static forRoot() {
    return {
      ngModule: TennisSharedModule
    };
  }
}
