import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddResidentComponent } from './add-resident.component';
import { testProviders } from '../../testing/test-providers';

describe('AddResidentComponent', () => {
  let component: AddResidentComponent;
  let fixture: ComponentFixture<AddResidentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddResidentComponent],
      providers: [...testProviders]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AddResidentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
