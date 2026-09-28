import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditstoryComponent } from './editstory.component';
import { testProviders } from '../../testing/test-providers';

describe('EditstoryComponent', () => {
  let component: EditstoryComponent;
  let fixture: ComponentFixture<EditstoryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditstoryComponent],
      providers: [...testProviders]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EditstoryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
