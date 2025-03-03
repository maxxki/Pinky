import { TestBed } from '@angular/core/testing';

import { LoginResponseService } from './login-response.service';

describe('LoginResponseService', () => {
  let service: LoginResponseService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(LoginResponseService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
