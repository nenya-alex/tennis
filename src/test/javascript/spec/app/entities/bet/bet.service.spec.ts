/* tslint:disable max-line-length */
import {getTestBed, TestBed} from "@angular/core/testing";
import {HttpClientTestingModule, HttpTestingController} from "@angular/common/http/testing";
import {HttpClient, HttpResponse} from "@angular/common/http";
import {of} from "rxjs";
import {map, take} from "rxjs/operators";
import * as moment from "moment";
import {DATE_TIME_FORMAT} from "app/shared/constants/input.constants";
import {BetService} from "app/entities/bet/bet.service";
import {Bet, BetStatus, IBet, MatchWinner} from "app/shared/model/bet.model";

describe('Service Tests', () => {
  describe('Bet Service', () => {
    let injector: TestBed;
    let service: BetService;
    let httpMock: HttpTestingController;
    let elemDefault: IBet;
    let expectedResult;
    let currentDate: moment.Moment;
    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule]
      });
      expectedResult = {};
      injector = getTestBed();
      service = injector.get(BetService);
      httpMock = injector.get(HttpTestingController);
      currentDate = moment();

      elemDefault = new Bet(0, 0, 0, MatchWinner.Q, BetStatus.A, currentDate);
    });

    describe('Service methods', () => {
      it('should find an element', async () => {
        const returnedFromService = Object.assign(
          {
            placedDate: currentDate.format(DATE_TIME_FORMAT)
          },
          elemDefault
        );
        service
          .find(123)
          .pipe(take(1))
          .subscribe(resp => (expectedResult = resp));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject({ body: elemDefault });
      });

      it('should create a Bet', async () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
            placedDate: currentDate.format(DATE_TIME_FORMAT)
          },
          elemDefault
        );
        const expected = Object.assign(
          {
            placedDate: currentDate
          },
          returnedFromService
        );
        service
          .create(new Bet(null))
          .pipe(take(1))
          .subscribe(resp => (expectedResult = resp));
        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject({ body: expected });
      });

      it('should update a Bet', async () => {
        const returnedFromService = Object.assign(
          {
            amount: 1,
            odds: 1,
            placedBetMatchWinner: 'BBBBBB',
            status: 'BBBBBB',
            placedDate: currentDate.format(DATE_TIME_FORMAT)
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            placedDate: currentDate
          },
          returnedFromService
        );
        service
          .update(expected)
          .pipe(take(1))
          .subscribe(resp => (expectedResult = resp));
        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject({ body: expected });
      });

      it('should return a list of Bet', async () => {
        const returnedFromService = Object.assign(
          {
            amount: 1,
            odds: 1,
            placedBetMatchWinner: 'BBBBBB',
            status: 'BBBBBB',
            placedDate: currentDate.format(DATE_TIME_FORMAT)
          },
          elemDefault
        );
        const expected = Object.assign(
          {
            placedDate: currentDate
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
        const req = httpMock.expectOne({ method: 'GET' });
        req.flush([returnedFromService]);
        httpMock.verify();
        expect(expectedResult).toContainEqual(expected);
      });

      it('should delete a Bet', async () => {
        const rxPromise = service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
