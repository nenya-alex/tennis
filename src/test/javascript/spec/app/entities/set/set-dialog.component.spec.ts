/* tslint:disable max-line-length */
import {async, ComponentFixture, fakeAsync, inject, TestBed, tick} from "@angular/core/testing";
import {HttpResponse} from "@angular/common/http";
import {NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {Observable} from "rxjs/Observable";
import {JhiEventManager} from "ng-jhipster";

import {TennisTestModule} from "../../../test.module";
import {SetDialogComponent} from "../../../../../../main/webapp/app/entities/set/set-dialog.component";
import {SetService} from "../../../../../../main/webapp/app/entities/set/set.service";
import {Set} from "../../../../../../main/webapp/app/entities/set/set.model";
import {MatchService} from "../../../../../../main/webapp/app/entities/match";

describe('Component Tests', () => {

    describe('Set Management Dialog Component', () => {
        let comp: SetDialogComponent;
        let fixture: ComponentFixture<SetDialogComponent>;
        let service: SetService;
        let mockEventManager: any;
        let mockActiveModal: any;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [TennisTestModule],
                declarations: [SetDialogComponent],
                providers: [
                    MatchService,
                    SetService
                ]
            })
            .overrideTemplate(SetDialogComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(SetDialogComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(SetService);
            mockEventManager = fixture.debugElement.injector.get(JhiEventManager);
            mockActiveModal = fixture.debugElement.injector.get(NgbActiveModal);
        });

        describe('save', () => {
            it('Should call update service on save for existing entity',
                inject([],
                    fakeAsync(() => {
                        // GIVEN
                        const entity = new Set(123);
                        spyOn(service, 'update').and.returnValue(Observable.of(new HttpResponse({body: entity})));
                        comp.set = entity;
                        // WHEN
                        comp.save();
                        tick(); // simulate async

                        // THEN
                        expect(service.update).toHaveBeenCalledWith(entity);
                        expect(comp.isSaving).toEqual(false);
                        expect(mockEventManager.broadcastSpy).toHaveBeenCalledWith({ name: 'setListModification', content: 'OK'});
                        expect(mockActiveModal.dismissSpy).toHaveBeenCalled();
                    })
                )
            );

            it('Should call create service on save for new entity',
                inject([],
                    fakeAsync(() => {
                        // GIVEN
                        const entity = new Set();
                        spyOn(service, 'create').and.returnValue(Observable.of(new HttpResponse({body: entity})));
                        comp.set = entity;
                        // WHEN
                        comp.save();
                        tick(); // simulate async

                        // THEN
                        expect(service.create).toHaveBeenCalledWith(entity);
                        expect(comp.isSaving).toEqual(false);
                        expect(mockEventManager.broadcastSpy).toHaveBeenCalledWith({ name: 'setListModification', content: 'OK'});
                        expect(mockActiveModal.dismissSpy).toHaveBeenCalled();
                    })
                )
            );
        });
    });

});
