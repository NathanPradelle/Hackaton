import { Component } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Component({
    selector: 'app-login',
    imports: [CommonModule, ReactiveFormsModule],
    templateUrl: './login.html',
    styleUrl: './login.css',
})
export class Login {
    isLoginMode = true;
    loginForm!: FormGroup;
    registerForm!: FormGroup;
    submitted = false;
    loading = false;
    errorMessage: string | null = null;

    constructor(
        private fb: FormBuilder,
        private authService: AuthService,
        private router: Router
    ) {
        this.initializeLoginForm();
        this.initializeRegisterForm();
    }

    initializeLoginForm() {
        this.loginForm = this.fb.group({
            identifier: ['', [Validators.required, Validators.minLength(3)]],
            password: ['', [Validators.required, Validators.minLength(6)]]
        });
    }

    initializeRegisterForm() {
        this.registerForm = this.fb.group({
            name: ['', [Validators.required, Validators.minLength(2)]],
            siret: ['', [Validators.required, Validators.pattern(/^\d{14}$/)]],
            city: ['', [Validators.required, Validators.minLength(2)]],
            identifier: ['', [Validators.required, Validators.minLength(3)]],
            password: ['', [Validators.required, Validators.minLength(6)]],
            confirmPassword: ['', [Validators.required]]
        });
    }

    isControlInvalid(form: FormGroup, controlName: string): boolean {
        const control = form.get(controlName);
        return !!control && control.invalid && (control.touched || this.submitted);
    }

    toggleMode() {
        this.isLoginMode = !this.isLoginMode;
        this.submitted = false;
        this.errorMessage = null;
    }

    onLoginSubmit() {
        this.submitted = true;
        this.errorMessage = null;

        if (this.loginForm.valid) {
            this.loading = true;
            this.authService.login(this.loginForm.value).subscribe({
                next: (response) => {
                    console.log('Connexion réussie:', response);
                    this.loading = false;
                    this.router.navigate(['']); //TODO redirection vers la page d'accueil ou dashboard après connexion
                },
                error: (err) => {
                    this.loading = false;
                    this.errorMessage = err.error?.message || 'Erreur de connexion. Veuillez vérifier vos identifiants.';
                    console.error('Erreur de connexion:', err);
                }
            });
        }
    }

    onRegisterSubmit() {
        this.submitted = true;
        this.errorMessage = null;

        if (this.registerForm.valid) {
            if (this.registerForm.value.password !== this.registerForm.value.confirmPassword) {
                this.errorMessage = 'Les mots de passe ne correspondent pas';
                return;
            }

            this.loading = true;
            const { confirmPassword, ...registerData } = this.registerForm.value;

            this.authService.register(registerData).subscribe({
                next: (response) => {
                    console.log('Inscription réussie:', response);
                    this.loading = false;
                    this.router.navigate(['']); //TODO redirection vers la page d'accueil ou dashboard après inscription
                },
                error: (err) => {
                    this.loading = false;
                    this.errorMessage = err.error?.message || 'Erreur lors de l\'inscription. Veuillez réessayer.';
                    console.error('Erreur d\'inscription:', err);
                }
            });
        }
    }
}
