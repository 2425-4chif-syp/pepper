import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ResidentsComponent } from './residents.component';
import { testProviders } from '../../testing/test-providers';

describe('ResidentsComponent', () => {
  let component: ResidentsComponent;
  let fixture: ComponentFixture<ResidentsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ResidentsComponent],
      providers: [...testProviders]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ResidentsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
