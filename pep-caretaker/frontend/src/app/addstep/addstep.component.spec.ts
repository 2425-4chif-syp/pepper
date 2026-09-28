import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddstepComponent } from './addstep.component';
import { testProviders } from '../../testing/test-providers';

describe('AddstepComponent', () => {
  let component: AddstepComponent;
  let fixture: ComponentFixture<AddstepComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddstepComponent],
      providers: [...testProviders]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AddstepComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
