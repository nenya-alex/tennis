/* tslint:disable max-line-length */
import {getTestBed, TestBed} from "@angular/core/testing";
import {HttpClientTestingModule, HttpTestingController} from "@angular/common/http/testing";
import {JhiDateUtils} from "ng-jhipster";

import {SettService} from "../../../../../../main/webapp/app/entities/sett/sett.service";
import {SERVER_API_URL} from "../../../../../../main/webapp/app/app.constants";

describe('Service Tests', () => {

    describe('Sett Service', () => {
        let injector: TestBed;
        let service: SettService;
        let httpMock: HttpTestingController;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [
                    HttpClientTestingModule
                ],
                providers: [
                    JhiDateUtils,
                    SettService
                ]
            });
            injector = getTestBed();
            service = injector.get(SettService);
            httpMock = injector.get(HttpTestingController);
        });

        describe('Service methods', () => {
            it('should call correct URL', () => {
                service.find(123).subscribe(() => {});

                const req  = httpMock.expectOne({ method: 'GET' });

                const resourceUrl = SERVER_API_URL + 'api/setts';
                expect(req.request.url).toEqual(resourceUrl + '/' + 123);
            });
            it('should return Sett', () => {

                service.find(123).subscribe((received) => {
                    expect(received.body.id).toEqual(123);
                });

                const req = httpMock.expectOne({ method: 'GET' });
                req.flush({id: 123});
            });

            it('should propagate not found response', () => {

                service.find(123).subscribe(null, (_error: any) => {
                    expect(_error.status).toEqual(404);
                });

                const req  = httpMock.expectOne({ method: 'GET' });
                req.flush('Invalid request parameters', {
                    status: 404, statusText: 'Bad Request'
                });

            });
        });

        afterEach(() => {
            httpMock.verify();
        });

    });

});
