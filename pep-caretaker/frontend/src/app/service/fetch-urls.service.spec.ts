import { TestBed } from '@angular/core/testing';

import { FetchUrlsService } from './fetch-urls.service';
import { testProviders } from '../../testing/test-providers';

describe('FetchUrlsService', () => {
  let service: FetchUrlsService;

  beforeEach(() => {
    TestBed.configureTestingModule({ providers: [...testProviders] });
    service = TestBed.inject(FetchUrlsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
