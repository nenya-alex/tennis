/* tslint:disable max-line-length */
import {getTestBed, TestBed} from "@angular/core/testing";
import {HttpClientTestingModule, HttpTestingController} from "@angular/common/http/testing";
import {HttpClient, HttpResponse} from "@angular/common/http";
import {of} from "rxjs";
import {map, take} from "rxjs/operators";
import * as moment from "moment";
import {DATE_TIME_FORMAT} from "app/shared/constants/input.constants";
import {OddsService} from "app/entities/odds/odds.service";
import {IOdds, Odds} from "app/shared/model/odds.model";

describe('Service Tests', () => {
    describe('Odds Service', () => {
        let injector: TestBed;
        let service: OddsService;
        let httpMock: HttpTestingController;
        let elemDefault: IOdds;
        let expectedResult;
        let currentDate: moment.Moment;
        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [HttpClientTestingModule]
            });
            expectedResult = {};
            injector = getTestBed();
            service = injector.get(OddsService);
            httpMock = injector.get(HttpTestingController);
            currentDate = moment();

            elemDefault = new Odds(0, 0, 0, currentDate);
        });

        describe('Service methods', () => {
            it('should find an element', async () => {
                const returnedFromService = Object.assign(
                    {
                        checkDate: currentDate.format(DATE_TIME_FORMAT)
                    },
                    elemDefault
                );
                service
                    .find(123)
                    .pipe(take(1))
                    .subscribe(resp => (expectedResult = resp));

                const req = httpMock.expectOne({method: 'GET'});
                req.flush(returnedFromService);
                expect(expectedResult).toMatchObject({body: elemDefault});
            });

            it('should create a Odds', async () => {
                const returnedFromService = Object.assign(
                    {
                        id: 0,
                        checkDate: currentDate.format(DATE_TIME_FORMAT)
                    },
                    elemDefault
                );
                const expected = Object.assign(
                    {
                        checkDate: currentDate
                    },
                    returnedFromService
                );
                service
                    .create(new Odds(null))
                    .pipe(take(1))
                    .subscribe(resp => (expectedResult = resp));
                const req = httpMock.expectOne({method: 'POST'});
                req.flush(returnedFromService);
                expect(expectedResult).toMatchObject({body: expected});
            });

            it('should update a Odds', async () => {
                const returnedFromService = Object.assign(
                    {
                        homeOdds: 1,
                        awayOdds: 1,
                        checkDate: currentDate.format(DATE_TIME_FORMAT)
                    },
                    elemDefault
                );

                const expected = Object.assign(
                    {
                        checkDate: currentDate
                    },
                    returnedFromService
                );
                service
                    .update(expected)
                    .pipe(take(1))
                    .subscribe(resp => (expectedResult = resp));
                const req = httpMock.expectOne({method: 'PUT'});
                req.flush(returnedFromService);
                expect(expectedResult).toMatchObject({body: expected});
            });

            it('should return a list of Odds', async () => {
                const returnedFromService = Object.assign(
                    {
                        homeOdds: 1,
                        awayOdds: 1,
                        checkDate: currentDate.format(DATE_TIME_FORMAT)
                    },
                    elemDefault
                );
                const expected = Object.assign(
                    {
                        checkDate: currentDate
                    },
                    returnedFromService
                );
                service
                    .query(expected)
                    .pipe(
                        take(1),
                        map(resp => resp.body)
                    )
                    .subscribe(body => (expectedResult = body));
                const req = httpMock.expectOne({method: 'GET'});
                req.flush([returnedFromService]);
                httpMock.verify();
                expect(expectedResult).toContainEqual(expected);
            });

            it('should delete a Odds', async () => {
                const rxPromise = service.delete(123).subscribe(resp => (expectedResult = resp.ok));

                const req = httpMock.expectOne({method: 'DELETE'});
                req.flush({status: 200});
                expect(expectedResult);
            });
        });

        afterEach(() => {
            httpMock.verify();
        });
    });
});
