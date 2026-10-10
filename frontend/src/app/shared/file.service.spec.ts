import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { FileService } from './file.service';
import { NotificheService } from './notifiche.service';

describe('FileService', () => {
  let servizio: FileService;
  let backend: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting()] });
    servizio = TestBed.inject(FileService);
    backend = TestBed.inject(HttpTestingController);
    globalThis.URL.createObjectURL = vi.fn(() => 'blob:prova');
    globalThis.URL.revokeObjectURL = vi.fn();
  });

  afterEach(() => backend.verify());

  it('salva il file con il nome indicato dal server', () => {
    let nomeSalvato = '';
    vi.spyOn(HTMLAnchorElement.prototype, 'click').mockImplementation(function (this: HTMLAnchorElement) {
      nomeSalvato = this.download;
    });

    servizio.scarica('http://api/parti/5/pdf');
    backend
      .expectOne('http://api/parti/5/pdf')
      .flush(new Blob(['%PDF']), { headers: { 'Content-Disposition': 'inline; filename="Ottavino1_InnoDiMameli.pdf"' } });

    expect(nomeSalvato).toBe('Ottavino1_InnoDiMameli.pdf');
  });

  it('se il download non riesce mostra un avviso', () => {
    servizio.scarica('http://api/parti/5/pdf');
    backend.expectOne('http://api/parti/5/pdf').flush(new Blob(['errore']), { status: 404, statusText: 'Not Found' });

    expect(TestBed.inject(NotificheService).notifiche()[0].testo).toContain('Impossibile scaricare');
  });
});
