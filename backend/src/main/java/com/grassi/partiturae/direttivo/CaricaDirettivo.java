package com.grassi.partiturae.direttivo;

import java.util.EnumSet;
import java.util.Set;

public enum CaricaDirettivo {
    PRESIDENTE,
    VICEPRESIDENTE,
    SEGRETARIO,
    TESORIERE,
    CONSIGLIERE,
    REVISORE_DEI_CONTI,
    MAESTRO_CONCERTATORE,
    ALTRO;

    private static final Set<CaricaDirettivo> UNICHE = EnumSet.of(
            PRESIDENTE,
            VICEPRESIDENTE,
            SEGRETARIO,
            TESORIERE,
            MAESTRO_CONCERTATORE
    );

    public boolean isUnica() {
        return UNICHE.contains(this);
    }
}