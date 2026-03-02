import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RegisterComponent } from './register.component';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';

describe('RegisterComponent', () => {
    let component: RegisterComponent;
    let fixture: ComponentFixture<RegisterComponent>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [
                ReactiveFormsModule,
                HttpClientTestingModule,
                RouterTestingModule,
                RegisterComponent
            ]
        }).compileComponents();
    });

    beforeEach(() => {
        fixture = TestBed.createComponent(RegisterComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create the register component', () => {
        expect(component).toBeTruthy();
    });

    it('should initialize the registration form', () => {
        expect(component.registerForm).toBeDefined();
        expect(component.registerForm.get('email')).toBeDefined();
        expect(component.registerForm.get('password')).toBeDefined();
    });

    it('should require valid email', () => {
        const emailControl = component.registerForm.get('email');
        emailControl?.setValue('invalid-email');
        expect(emailControl?.valid).toBeFalse();

        emailControl?.setValue('valid@example.com');
        expect(emailControl?.valid).toBeTrue();
    });
});
