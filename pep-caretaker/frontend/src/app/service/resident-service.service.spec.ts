import { TestBed } from '@angular/core/testing';

import { ResidentServiceService } from './resident-service.service';
import { testProviders } from '../../testing/test-providers';

describe('ResidentServiceService', () => {
  let service: ResidentServiceService;

  beforeEach(() => {
    TestBed.configureTestingModule({ providers: [...testProviders] });
    service = TestBed.inject(ResidentServiceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
