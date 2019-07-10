/* tslint:disable max-line-length */
import {async, ComponentFixture, TestBed} from "@angular/core/testing";
import {Observable} from "rxjs/Observable";
import {HttpHeaders, HttpResponse} from "@angular/common/http";

import {TennisTestModule} from "../../../test.module";
import {SettComponent} from "../../../../../../main/webapp/app/entities/sett/sett.component";
import {SettService} from "../../../../../../main/webapp/app/entities/sett/sett.service";
import {Sett} from "../../../../../../main/webapp/app/entities/sett/sett.model";

describe('Component Tests', () => {

    describe('Sett Management Component', () => {
        let comp: SettComponent;
        let fixture: ComponentFixture<SettComponent>;
        let service: SettService;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [TennisTestModule],
                declarations: [SettComponent],
                providers: [
                    SettService
                ]
            })
            .overrideTemplate(SettComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(SettComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(SettService);
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN
                const headers = new HttpHeaders().append('link', 'link;link');
                spyOn(service, 'query').and.returnValue(Observable.of(new HttpResponse({
                    body: [new Sett(123)],
                    headers
                })));

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(service.query).toHaveBeenCalled();
                expect(comp.setts[0]).toEqual(jasmine.objectContaining({id: 123}));
            });
        });
    });

});
