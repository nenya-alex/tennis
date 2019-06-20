/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, async } from '@angular/core/testing';
import { Observable } from 'rxjs/Observable';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { TennisTestModule } from '../../../test.module';
import { GameComponent } from '../../../../../../main/webapp/app/entities/game/game.component';
import { GameService } from '../../../../../../main/webapp/app/entities/game/game.service';
import { Game } from '../../../../../../main/webapp/app/entities/game/game.model';

describe('Component Tests', () => {

    describe('Game Management Component', () => {
        let comp: GameComponent;
        let fixture: ComponentFixture<GameComponent>;
        let service: GameService;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [TennisTestModule],
                declarations: [GameComponent],
                providers: [
                    GameService
                ]
            })
            .overrideTemplate(GameComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(GameComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(GameService);
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN
                const headers = new HttpHeaders().append('link', 'link;link');
                spyOn(service, 'query').and.returnValue(Observable.of(new HttpResponse({
                    body: [new Game(123)],
                    headers
                })));

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(service.query).toHaveBeenCalled();
                expect(comp.games[0]).toEqual(jasmine.objectContaining({id: 123}));
            });
        });
    });

});
