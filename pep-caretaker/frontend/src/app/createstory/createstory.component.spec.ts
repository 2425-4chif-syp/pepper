import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreatestoryComponent } from './createstory.component';
import { testProviders } from '../../testing/test-providers';

describe('CreatestoryComponent', () => {
  let component: CreatestoryComponent;
  let fixture: ComponentFixture<CreatestoryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreatestoryComponent],
      providers: [...testProviders]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreatestoryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
