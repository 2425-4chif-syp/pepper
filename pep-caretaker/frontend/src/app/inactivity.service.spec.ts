import { TestBed } from '@angular/core/testing';

import { InactivityService } from './inactivity.service';
import { testProviders } from '../testing/test-providers';

describe('InactivityService', () => {
  let service: InactivityService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(InactivityService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
