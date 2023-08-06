package ru.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.request.model.Request;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findAllByRequester_Id(Long userId);

    List<Request> findByEventInitiatorIdAndEventId(Long userId, Long eventId);

    Request findOneByEventIdAndRequesterId(Long eventId, Long userId);
}

