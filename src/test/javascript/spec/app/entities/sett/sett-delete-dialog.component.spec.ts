/* tslint:disable max-line-length */
import {async, ComponentFixture, fakeAsync, inject, TestBed, tick} from "@angular/core/testing";
import {NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {Observable} from "rxjs/Observable";
import {JhiEventManager} from "ng-jhipster";

import {TennisTestModule} from "../../../test.module";
import {SettDeleteDialogComponent} from "../../../../../../main/webapp/app/entities/sett/sett-delete-dialog.component";
import {SettService} from "../../../../../../main/webapp/app/entities/sett/sett.service";

describe('Component Tests', () => {

    describe('Sett Management Delete Component', () => {
        let comp: SettDeleteDialogComponent;
        let fixture: ComponentFixture<SettDeleteDialogComponent>;
        let service: SettService;
        let mockEventManager: any;
        let mockActiveModal: any;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [TennisTestModule],
                declarations: [SettDeleteDialogComponent],
                providers: [
                    SettService
                ]
            })
            .overrideTemplate(SettDeleteDialogComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(SettDeleteDialogComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(SettService);
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
