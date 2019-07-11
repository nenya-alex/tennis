/* tslint:disable max-line-length */
import {ComponentFixture, TestBed} from '@angular/core/testing';
import {Observable, of} from 'rxjs';
import {HttpHeaders, HttpResponse} from '@angular/common/http';

import {TennisTestModule} from '../../../test.module';
import {OddsComponent} from 'app/entities/odds/odds.component';
import {OddsService} from 'app/entities/odds/odds.service';
import {Odds} from 'app/shared/model/odds.model';

describe('Component Tests', () => {
    describe('Odds Management Component', () => {
        let comp: OddsComponent;
        let fixture: ComponentFixture<OddsComponent>;
        let service: OddsService;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [TennisTestModule],
                declarations: [OddsComponent],
                providers: []
            })
                .overrideTemplate(OddsComponent, '')
                .compileComponents();

            fixture = TestBed.createComponent(OddsComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(OddsService);
        });

        it('Should call load all on init', () => {
            // GIVEN
            const headers = new HttpHeaders().append('link', 'link;link');
            spyOn(service, 'query').and.returnValue(
                of(
                    new HttpResponse({
                        body: [new Odds(123)],
                        headers
                    })
                )
            );

            // WHEN
            comp.ngOnInit();

            // THEN
            expect(service.query).toHaveBeenCalled();
            expect(comp.odds[0]).toEqual(jasmine.objectContaining({id: 123}));
        });
    });
});
