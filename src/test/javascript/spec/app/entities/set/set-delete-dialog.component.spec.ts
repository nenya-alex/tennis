/* tslint:disable max-line-length */
import {async, ComponentFixture, fakeAsync, inject, TestBed, tick} from "@angular/core/testing";
import {NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {Observable} from "rxjs/Observable";
import {JhiEventManager} from "ng-jhipster";

import {TennisTestModule} from "../../../test.module";
import {SetDeleteDialogComponent} from "../../../../../../main/webapp/app/entities/set/set-delete-dialog.component";
import {SetService} from "../../../../../../main/webapp/app/entities/set/set.service";

describe('Component Tests', () => {

    describe('Set Management Delete Component', () => {
        let comp: SetDeleteDialogComponent;
        let fixture: ComponentFixture<SetDeleteDialogComponent>;
        let service: SetService;
        let mockEventManager: any;
        let mockActiveModal: any;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [TennisTestModule],
                declarations: [SetDeleteDialogComponent],
                providers: [
                    SetService
                ]
            })
            .overrideTemplate(SetDeleteDialogComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(SetDeleteDialogComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(SetService);
            mockEventManager = fixture.debugElement.injector.get(JhiEventManager);
            mockActiveModal = fixture.debugElement.injector.get(NgbActiveModal);
        });

        describe('confirmDelete', () => {
            it('Should call delete service on confirmDelete',
                inject([],
                    fakeAsync(() => {
                        // GIVEN
                        spyOn(service, 'delete').and.returnValue(Observable.of({}));

                        // WHEN
                        comp.confirmDelete(123);
                        tick();

                        // THEN
                        expect(service.delete).toHaveBeenCalledWith(123);
                        expect(mockActiveModal.dismissSpy).toHaveBeenCalled();
                        expect(mockEventManager.broadcastSpy).toHaveBeenCalled();
                    })
                )
            );
        });
    });

});
