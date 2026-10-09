package com.grassi.partiturae.seed;

import com.grassi.partiturae.famiglia.Famiglia;
import com.grassi.partiturae.famiglia.FamigliaRepository;
import com.grassi.partiturae.strumento.Strumento;
import com.grassi.partiturae.strumento.StrumentoRepository;
import com.grassi.partiturae.strumentofiglio.StrumentoFiglio;
import com.grassi.partiturae.strumentofiglio.StrumentoFiglioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Al primo avvio (catalogo strumenti vuoto) inserisce gli strumenti di {@link InstrumentCatalog}.
 * Se nel database c'è già almeno una famiglia non tocca nulla, così le modifiche fatte
 * dall'utente non vengono mai sovrascritte.
 */
@Component
public class DefaultInstrumentsSeeder implements CommandLineRunner {

    private final FamigliaRepository famigliaRepository;
    private final StrumentoRepository strumentoRepository;
    private final StrumentoFiglioRepository strumentoFiglioRepository;

    public DefaultInstrumentsSeeder(FamigliaRepository famigliaRepository,
                                     StrumentoRepository strumentoRepository,
                                     StrumentoFiglioRepository strumentoFiglioRepository) {
        this.famigliaRepository = famigliaRepository;
        this.strumentoRepository = strumentoRepository;
        this.strumentoFiglioRepository = strumentoFiglioRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
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