/* tslint:disable max-line-length */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Observable, of } from 'rxjs';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { TennisTestModule } from '../../../test.module';
import { BetComponent } from 'app/entities/bet/bet.component';
import { BetService } from 'app/entities/bet/bet.service';
import { Bet } from 'app/shared/model/bet.model';

describe('Component Tests', () => {
  describe('Bet Management Component', () => {
    let comp: BetComponent;
    let fixture: ComponentFixture<BetComponent>;
    let service: BetService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [TennisTestModule],
        declarations: [BetComponent],
        providers: []
      })
        .overrideTemplate(BetComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(BetComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(BetService);
    });

    it('Should call load all on init', () => {
      // GIVEN
      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'query').and.returnValue(
        of(
          new HttpResponse({
            body: [new Bet(123)],
            headers
          })
        )
      );

      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.bets[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
