/* tslint:disable max-line-length */
import {async, ComponentFixture, TestBed} from "@angular/core/testing";
import {Observable} from "rxjs/Observable";
import {HttpHeaders, HttpResponse} from "@angular/common/http";

import {TennisTestModule} from "../../../test.module";
import {SetComponent} from "../../../../../../main/webapp/app/entities/set/set.component";
import {SetService} from "../../../../../../main/webapp/app/entities/set/set.service";
import {Set} from "../../../../../../main/webapp/app/entities/set/set.model";

describe('Component Tests', () => {

    describe('Set Management Component', () => {
        let comp: SetComponent;
        let fixture: ComponentFixture<SetComponent>;
        let service: SetService;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [TennisTestModule],
                declarations: [SetComponent],
                providers: [
                    SetService
                ]
            })
            .overrideTemplate(SetComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(SetComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(SetService);
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN
                const headers = new HttpHeaders().append('link', 'link;link');
                spyOn(service, 'query').and.returnValue(Observable.of(new HttpResponse({
                    body: [new Set(123)],
                    headers
                })));

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(service.query).toHaveBeenCalled();
                expect(comp.sets[0]).toEqual(jasmine.objectContaining({id: 123}));
            });
        });
    });

});
