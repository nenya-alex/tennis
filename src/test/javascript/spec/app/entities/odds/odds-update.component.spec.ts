/* tslint:disable max-line-length */
import {ComponentFixture, fakeAsync, TestBed, tick} from "@angular/core/testing";
import {HttpResponse} from "@angular/common/http";
import {FormBuilder} from "@angular/forms";
import {Observable, of} from "rxjs";

import {TennisTestModule} from "../../../test.module";
import {OddsUpdateComponent} from "app/entities/odds/odds-update.component";
import {OddsService} from "app/entities/odds/odds.service";
import {Odds} from "app/shared/model/odds.model";

describe('Component Tests', () => {
    describe('Odds Management Update Component', () => {
        let comp: OddsUpdateComponent;
        let fixture: ComponentFixture<OddsUpdateComponent>;
        let service: OddsService;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [TennisTestModule],
                declarations: [OddsUpdateComponent],
                providers: [FormBuilder]
            })
                .overrideTemplate(OddsUpdateComponent, '')
                .compileComponents();

            fixture = TestBed.createComponent(OddsUpdateComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(OddsService);
        });

        describe('save', () => {
            it('Should call update service on save for existing entity', fakeAsync(() => {
                // GIVEN
                const entity = new Odds(123);
                spyOn(service, 'update').and.returnValue(of(new HttpResponse({body: entity})));
                comp.updateForm(entity);
                // WHEN
                comp.save();
                tick(); // simulate async

                // THEN
                expect(service.update).toHaveBeenCalledWith(entity);
                expect(comp.isSaving).toEqual(false);
            }));

            it('Should call create service on save for new entity', fakeAsync(() => {
                // GIVEN
                const entity = new Odds();
                spyOn(service, 'create').and.returnValue(of(new HttpResponse({body: entity})));
                comp.updateForm(entity);
                // WHEN
                comp.save();
                tick(); // simulate async

                // THEN
                expect(service.create).toHaveBeenCalledWith(entity);
                expect(comp.isSaving).toEqual(false);
            }));
        });
    });
});
