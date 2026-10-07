package com.grassi.partiturae.services;

import com.grassi.partiturae.dto.EventoRequest;
import com.grassi.partiturae.dto.EventoResponse;
import com.grassi.partiturae.exceptions.ResourceNotFoundException;
import com.grassi.partiturae.model.Evento;
import com.grassi.partiturae.repositories.EventoRepository;
import com.grassi.partiturae.repositories.PresenzaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EventoService {

    private final EventoRepository eventoRepository;
    private final PresenzaRepository presenzaRepository;

    public EventoService(EventoRepository eventoRepository, PresenzaRepository presenzaRepository) {
        this.eventoRepository = eventoRepository;
        this.presenzaRepository = presenzaRepository;
    }

    @Transactional(readOnly = true)
    public List<EventoResponse> getAll() {
        return eventoRepository.findAllByOrderByDataDesc().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public EventoResponse getById(Long id) {
        return toResponse(findEntityById(id));
    }

    @Transactional
    public EventoResponse create(EventoRequest request) {
        Evento evento = Evento.builder()
                .titolo(request.getTitolo())
                .tipo(request.getTipo())
                .data(request.getData())
                .ora(request.getOra())
                .luogo(request.getLuogo())
                .note(request.getNote())
                .build();

        return toResponse(eventoRepository.save(evento));
    }

    @Transactional
    public EventoResponse update(Long id, EventoRequest request) {
        Evento evento = findEntityById(id);

        evento.setTitolo(request.getTitolo());
        evento.setTipo(request.getTipo());
        evento.setData(request.getData());
        evento.setOra(request.getOra());
        evento.setLuogo(request.getLuogo());
        evento.setNote(request.getNote());

        return toResponse(eventoRepository.save(evento));
    }

    @Transactional
    public void delete(Long id) {
        Evento evento = findEntityById(id);
        eventoRepository.delete(evento);
    }

    Evento findEntityById(Long id) {
        return eventoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento non trovato con id: " + id));
    }

    private EventoResponse toResponse(Evento evento) {
        return EventoResponse.builder()
                .id(evento.getId())
                .titolo(evento.getTitolo())
                .tipo(evento.getTipo())
                .data(evento.getData())
                .ora(evento.getOra())
                .luogo(evento.getLuogo())
                .note(evento.getNote())
                .numeroPresenti(presenzaRepository.countByEventoIdAndPresenteTrue(evento.getId()))
                .build();
    }
}