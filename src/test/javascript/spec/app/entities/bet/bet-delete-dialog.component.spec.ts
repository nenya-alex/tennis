/* tslint:disable max-line-length */
import {ComponentFixture, fakeAsync, inject, TestBed, tick} from "@angular/core/testing";
import {NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {Observable, of} from "rxjs";
import {JhiEventManager} from "ng-jhipster";

import {TennisTestModule} from "../../../test.module";
import {BetDeleteDialogComponent} from "app/entities/bet/bet-delete-dialog.component";
import {BetService} from "app/entities/bet/bet.service";

describe('Component Tests', () => {
  describe('Bet Management Delete Component', () => {
    let comp: BetDeleteDialogComponent;
    let fixture: ComponentFixture<BetDeleteDialogComponent>;
    let service: BetService;
    let mockEventManager: any;
    let mockActiveModal: any;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [TennisTestModule],
        declarations: [BetDeleteDialogComponent]
      })
        .overrideTemplate(BetDeleteDialogComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(BetDeleteDialogComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(BetService);
      mockEventManager = fixture.debugElement.injector.get(JhiEventManager);
      mockActiveModal = fixture.debugElement.injector.get(NgbActiveModal);
    });

    describe('confirmDelete', () => {
      it('Should call delete service on confirmDelete', inject(
        [],
        fakeAsync(() => {
          // GIVEN
          spyOn(service, 'delete').and.returnValue(of({}));

          // WHEN
          comp.confirmDelete(123);
          tick();

          // THEN
          expect(service.delete).toHaveBeenCalledWith(123);
          expect(mockActiveModal.dismissSpy).toHaveBeenCalled();
          expect(mockEventManager.broadcastSpy).toHaveBeenCalled();
        })
      ));
    });
  });
});
