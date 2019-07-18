/* tslint:disable max-line-length */
import {ComponentFixture, TestBed} from "@angular/core/testing";
import {ActivatedRoute} from "@angular/router";
import {of} from "rxjs";

import {TennisTestModule} from "../../../test.module";
import {OddsDetailComponent} from "app/entities/odds/odds-detail.component";
import {Odds} from "app/shared/model/odds.model";

describe('Component Tests', () => {
    describe('Odds Management Detail Component', () => {
        let comp: OddsDetailComponent;
        let fixture: ComponentFixture<OddsDetailComponent>;
        const route = ({data: of({odds: new Odds(123)})} as any) as ActivatedRoute;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [TennisTestModule],
                declarations: [OddsDetailComponent],
                providers: [{provide: ActivatedRoute, useValue: route}]
            })
                .overrideTemplate(OddsDetailComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(OddsDetailComponent);
            comp = fixture.componentInstance;
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(comp.odds).toEqual(jasmine.objectContaining({id: 123}));
            });
        });
    });
});
