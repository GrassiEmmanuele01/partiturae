package com.grassi.partiturae.profilo;

import com.grassi.partiturae.auth.Account;
import com.grassi.partiturae.auth.AccountRepository;
import com.grassi.partiturae.auth.Appartenenza;
import com.grassi.partiturae.auth.AppartenenzaRepository;
import com.grassi.partiturae.auth.UtenteCorrente;
import com.grassi.partiturae.musicista.Musicista;
import com.grassi.partiturae.musicista.MusicistaRepository;
import com.grassi.partiturae.socio.Socio;
import com.grassi.partiturae.socio.SocioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Collega la persona che ha fatto l'accesso ai suoi dati nella banda in cui sta lavorando:
 * il suo socio e il suo profilo musicale.
 *
 * Il socio si trova così: se l'appartenenza alla banda ha un socio collegato si usa quello,
 * altrimenti si cerca il socio della banda che ha la stessa email dell'account.
 * Il socio si cerca solo fra quelli della banda corrente, quindi un collegamento sbagliato
 * non può mai mostrare dati di un'altra banda.
 */
@Service
public class ProfiloUtenteService {

    private final AccountRepository accountRepository;
    private final AppartenenzaRepository appartenenzaRepository;
    private final SocioRepository socioRepository;
    private final MusicistaRepository musicistaRepository;

    public ProfiloUtenteService(AccountRepository accountRepository,
                                 AppartenenzaRepository appartenenzaRepository,
                                 SocioRepository socioRepository,
                                 MusicistaRepository musicistaRepository) {
        this.accountRepository = accountRepository;
        this.appartenenzaRepository = appartenenzaRepository;
        this.socioRepository = socioRepository;
        this.musicistaRepository = musicistaRepository;
    }

    @Transactional(readOnly = true)
    public Optional<Socio> socioCorrente() {
        Long accountId = UtenteCorrente.accountId().orElse(null);
        Long bandaId = UtenteCorrente.bandaId().orElse(null);

        if (accountId == null || bandaId == null) {
            return Optional.empty();
        }

        Optional<Socio> collegato = appartenenzaRepository.findByAccountIdAndBandaId(accountId, bandaId)
                .map(Appartenenza::getSocioId)
                .flatMap(socioRepository::findById);

        if (collegato.isPresent()) {
            return collegato;
        }

        return accountRepository.findById(accountId)
                .map(Account::getEmail)
                .flatMap(socioRepository::findByMailIgnoreCase);
    }

    @Transactional(readOnly = true)
    public Optional<Musicista> musicistaCorrente() {
        return socioCorrente().flatMap(socio -> musicistaRepository.findBySocioId(socio.getId()));
    }
}