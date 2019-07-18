/* tslint:disable max-line-length */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { TennisTestModule } from '../../../test.module';
import { BetDetailComponent } from 'app/entities/bet/bet-detail.component';
import { Bet } from 'app/shared/model/bet.model';

describe('Component Tests', () => {
  describe('Bet Management Detail Component', () => {
    let comp: BetDetailComponent;
    let fixture: ComponentFixture<BetDetailComponent>;
    const route = ({ data: of({ bet: new Bet(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [TennisTestModule],
        declarations: [BetDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }]
      })
        .overrideTemplate(BetDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(BetDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should call load all on init', () => {
        // GIVEN

        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.bet).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
