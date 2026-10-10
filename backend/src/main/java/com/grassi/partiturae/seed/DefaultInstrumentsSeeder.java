package com.grassi.partiturae.seed;

import com.grassi.partiturae.banda.Banda;
import com.grassi.partiturae.banda.BandaContext;
import com.grassi.partiturae.banda.BandaRepository;
import com.grassi.partiturae.famiglia.Famiglia;
import com.grassi.partiturae.famiglia.FamigliaRepository;
import com.grassi.partiturae.strumento.Strumento;
import com.grassi.partiturae.strumento.StrumentoRepository;
import com.grassi.partiturae.strumentofiglio.StrumentoFiglio;
import com.grassi.partiturae.strumentofiglio.StrumentoFiglioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

/**
 * Ogni banda ha il suo catalogo di strumenti. All'avvio, per ogni banda con il catalogo vuoto,
 * inserisce gli strumenti di {@link InstrumentCatalog}. Se la banda ha già almeno una famiglia
 * non tocca nulla, così le modifiche fatte dagli utenti non vengono mai sovrascritte.
 */
@Component
@Order(2)
public class DefaultInstrumentsSeeder implements CommandLineRunner {

    private final BandaRepository bandaRepository;
    private final FamigliaRepository famigliaRepository;
    private final StrumentoRepository strumentoRepository;
    private final StrumentoFiglioRepository strumentoFiglioRepository;
    private final PlatformTransactionManager transactionManager;

    public DefaultInstrumentsSeeder(BandaRepository bandaRepository,
                                     FamigliaRepository famigliaRepository,
                                     StrumentoRepository strumentoRepository,
                                     StrumentoFiglioRepository strumentoFiglioRepository,
                                     PlatformTransactionManager transactionManager) {
        this.bandaRepository = bandaRepository;
        this.famigliaRepository = famigliaRepository;
        this.strumentoRepository = strumentoRepository;
        this.strumentoFiglioRepository = strumentoFiglioRepository;
        this.transactionManager = transactionManager;
    }

    @Override
    public void run(String... args) {
        for (Banda banda : bandaRepository.findAll()) {
            inizializzaCatalogo(banda.getId());
        }
    }

    /** Inserisce gli strumenti di partenza nella banda indicata, se il suo catalogo è vuoto. */
    public void inizializzaCatalogo(Long bandaId) {
        // La banda si decide quando si apre la sessione di database: per questo la transazione
        // parte dentro il contesto della banda, non fuori.
        BandaContext.esegui(bandaId, () ->
                new TransactionTemplate(transactionManager).executeWithoutResult(stato -> inserisciSeVuoto()));
    }

    private void inserisciSeVuoto() {
        if (famigliaRepository.count() > 0) {
            return;
        }

        for (InstrumentCatalog.FamigliaDef famigliaDef : InstrumentCatalog.defaults()) {
            Famiglia famiglia = famigliaRepository.save(
                    Famiglia.builder().nome(famigliaDef.nome()).build());

            for (InstrumentCatalog.StrumentoDef strumentoDef : famigliaDef.strumenti()) {
                Strumento strumento = strumentoRepository.save(
                        Strumento.builder().nome(strumentoDef.nome()).famiglia(famiglia).build());

                List<StrumentoFiglio> voci = strumentoDef.voci().stream()
                        .map(nome -> StrumentoFiglio.builder().nome(nome).strumento(strumento).build())
                        .toList();

                strumentoFiglioRepository.saveAll(voci);
            }
        }
    }
}
