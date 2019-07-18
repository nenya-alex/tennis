/* tslint:disable max-line-length */
import {ComponentFixture, fakeAsync, TestBed, tick} from "@angular/core/testing";
import {HttpResponse} from "@angular/common/http";
import {FormBuilder} from "@angular/forms";
import {Observable, of} from "rxjs";

import {TennisTestModule} from "../../../test.module";
import {BetUpdateComponent} from "app/entities/bet/bet-update.component";
import {BetService} from "app/entities/bet/bet.service";
import {Bet} from "app/shared/model/bet.model";

describe('Component Tests', () => {
  describe('Bet Management Update Component', () => {
    let comp: BetUpdateComponent;
    let fixture: ComponentFixture<BetUpdateComponent>;
    let service: BetService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [TennisTestModule],
        declarations: [BetUpdateComponent],
        providers: [FormBuilder]
      })
        .overrideTemplate(BetUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(BetUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(BetService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new Bet(123);
        spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
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
        const entity = new Bet();
        spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
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
