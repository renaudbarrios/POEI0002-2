import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { UserFormLoginComponent } from './user-form-login.component';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { LoaderComponent } from '../../../shared/loader/loader.component';
import { MatProgressSpinnerModule } from '@angular/material';

describe('LoginComponent', () => {
  let component: UserFormLoginComponent;
  let fixture: ComponentFixture<UserFormLoginComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        UserFormLoginComponent,
        LoaderComponent
      ],
      imports: [
        RouterTestingModule,
        HttpClientTestingModule,
        ReactiveFormsModule,
        FormsModule,
        MatProgressSpinnerModule
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UserFormLoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
