import { TestBed } from '@angular/core/testing';

import { PreviewService } from './preview.service';
import { testProviders } from '../../testing/test-providers';

describe('PreviewService', () => {
  let service: PreviewService;

  beforeEach(() => {
    TestBed.configureTestingModule({ providers: [...testProviders] });
    service = TestBed.inject(PreviewService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
