/* tslint:disable max-line-length */
import {async, ComponentFixture, TestBed} from "@angular/core/testing";
import {HttpResponse} from "@angular/common/http";
import {Observable} from "rxjs/Observable";

import {TennisTestModule} from "../../../test.module";
import {SettDetailComponent} from "../../../../../../main/webapp/app/entities/sett/sett-detail.component";
import {SettService} from "../../../../../../main/webapp/app/entities/sett/sett.service";
import {Sett} from "../../../../../../main/webapp/app/entities/sett/sett.model";

describe('Component Tests', () => {

    describe('Sett Management Detail Component', () => {
        let comp: SettDetailComponent;
        let fixture: ComponentFixture<SettDetailComponent>;
        let service: SettService;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [TennisTestModule],
                declarations: [SettDetailComponent],
                providers: [
                    SettService
                ]
            })
            .overrideTemplate(SettDetailComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(SettDetailComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(SettService);
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN

                spyOn(service, 'find').and.returnValue(Observable.of(new HttpResponse({
                    body: new Sett(123)
                })));

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(service.find).toHaveBeenCalledWith(123);
                expect(comp.sett).toEqual(jasmine.objectContaining({id: 123}));
            });
        });
    });

});
