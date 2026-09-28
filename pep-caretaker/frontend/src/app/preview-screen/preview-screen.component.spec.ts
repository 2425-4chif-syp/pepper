import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PreviewScreenComponent } from './preview-screen.component';
import { testProviders } from '../../testing/test-providers';

describe('PreviewScreenComponent', () => {
  let component: PreviewScreenComponent;
  let fixture: ComponentFixture<PreviewScreenComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PreviewScreenComponent],
      providers: [...testProviders]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PreviewScreenComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
