/* tslint:disable max-line-length */
import {async, ComponentFixture, TestBed} from "@angular/core/testing";
import {HttpResponse} from "@angular/common/http";
import {Observable} from "rxjs/Observable";

import {TennisTestModule} from "../../../test.module";
import {SetDetailComponent} from "../../../../../../main/webapp/app/entities/set/set-detail.component";
import {SetService} from "../../../../../../main/webapp/app/entities/set/set.service";
import {Set} from "../../../../../../main/webapp/app/entities/set/set.model";

describe('Component Tests', () => {

    describe('Set Management Detail Component', () => {
        let comp: SetDetailComponent;
        let fixture: ComponentFixture<SetDetailComponent>;
        let service: SetService;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [TennisTestModule],
                declarations: [SetDetailComponent],
                providers: [
                    SetService
                ]
            })
            .overrideTemplate(SetDetailComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(SetDetailComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(SetService);
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN

                spyOn(service, 'find').and.returnValue(Observable.of(new HttpResponse({
                    body: new Set(123)
                })));

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(service.find).toHaveBeenCalledWith(123);
                expect(comp.set).toEqual(jasmine.objectContaining({id: 123}));
            });
        });
    });

});
