import { TestBed } from '@angular/core/testing';

import { AuthRequestService } from './auth-request.service';

describe('AuthRequestService', () => {
  let service: AuthRequestService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AuthRequestService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
