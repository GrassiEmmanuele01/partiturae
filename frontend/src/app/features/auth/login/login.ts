import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { AuthService } from '../auth.service';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule],
  templateUrl: './login.html',
  styleUrl: './login.scss'
})
export class Login {
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  form = this.fb.nonNullable.group({
    email: ['', Validators.required],
    password: ['', Validators.required]
  });

  loading = signal(false);
  error = signal<string | null>(null);
  mostraPassword = signal(false);

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading.set(true);
    this.error.set(null);

    const { email, password } = this.form.getRawValue();

    this.auth.login(email.trim(), password).subscribe({
      next: () => this.router.navigateByUrl(this.destinazione()),
      error: (errore: HttpErrorResponse) => {
        this.loading.set(false);
        this.error.set(this.messaggio(errore));
        this.form.controls.password.reset('');
      }
    });
  }

  toggleMostraPassword(): void {
    this.mostraPassword.set(!this.mostraPassword());
  }

  // Dopo il login si torna alla pagina richiesta, ma solo se è un indirizzo interno dell'app.
  private destinazione(): string {
    const richiesta = this.route.snapshot.queryParamMap.get('redirect');
    return richiesta && richiesta.startsWith('/') && !richiesta.startsWith('//') ? richiesta : '/';
  }

  private messaggio(errore: HttpErrorResponse): string {
    if (errore.status === 0) {
      return 'Impossibile contattare il server. Controlla che il backend sia avviato.';
    }
    if (errore.status === 401 || errore.status === 429) {
      return errore.error?.message ?? 'Email o password non corrette.';
    }
    return 'Si è verificato un errore. Riprova tra poco.';
  }
}