import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { environment } from '../environments/environment';
import { FileService } from './shared/file.service';
import { App } from './app';

describe('App', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [App],
      providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting()]
    }).compileComponents();
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(App);
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('non mostra il menu a chi non ha fatto il login', async () => {
    const fixture = TestBed.createComponent(App);
    await fixture.whenStable();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('.sidebar')).toBeNull();
  });

  it('i link verso l\'API vengono scaricati con il token invece di aprirsi da soli', () => {
    const scarica = vi.spyOn(TestBed.inject(FileService), 'scarica').mockImplementation(() => undefined);
    TestBed.createComponent(App);

    const link = document.createElement('a');
    link.href = `${environment.apiUrl}/parti/5/pdf`;
    document.body.appendChild(link);
    const evento = new MouseEvent('click', { bubbles: true, cancelable: true });
    link.dispatchEvent(evento);
    link.remove();

    expect(evento.defaultPrevented).toBe(true);
    expect(scarica).toHaveBeenCalledWith(`${environment.apiUrl}/parti/5/pdf`);
  });

  it('i link normali dell\'app non vengono toccati', () => {
    const scarica = vi.spyOn(TestBed.inject(FileService), 'scarica').mockImplementation(() => undefined);
    TestBed.createComponent(App);

    const link = document.createElement('a');
    link.href = 'http://localhost/partiture';
    link.addEventListener('click', (e) => e.preventDefault());
    document.body.appendChild(link);
    link.click();
    link.remove();

    expect(scarica).not.toHaveBeenCalled();
  });
});
