import { TestBed } from '@angular/core/testing';

import { ImageServiceService } from './image-service.service';
import { testProviders } from '../../testing/test-providers';

describe('ImageServiceService', () => {
  let service: ImageServiceService;

  beforeEach(() => {
    TestBed.configureTestingModule({ providers: [...testProviders] });
    service = TestBed.inject(ImageServiceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
