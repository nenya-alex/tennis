/* tslint:disable max-line-length */
import {ComponentFixture, fakeAsync, inject, TestBed, tick} from "@angular/core/testing";
import {NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {Observable, of} from "rxjs";
import {JhiEventManager} from "ng-jhipster";

import {TennisTestModule} from "../../../test.module";
import {OddsDeleteDialogComponent} from "app/entities/odds/odds-delete-dialog.component";
import {OddsService} from "app/entities/odds/odds.service";

describe('Component Tests', () => {
    describe('Odds Management Delete Component', () => {
        let comp: OddsDeleteDialogComponent;
        let fixture: ComponentFixture<OddsDeleteDialogComponent>;
        let service: OddsService;
        let mockEventManager: any;
        let mockActiveModal: any;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [TennisTestModule],
                declarations: [OddsDeleteDialogComponent]
            })
                .overrideTemplate(OddsDeleteDialogComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(OddsDeleteDialogComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(OddsService);
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
