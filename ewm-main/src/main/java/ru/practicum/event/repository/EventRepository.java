package ru.practicum.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.enam.EventState;
import ru.practicum.event.model.Event;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;


public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByInitiator_Id(Long userId, Pageable pageable);

    Optional<Event> findByIdAndInitiatorId(long eventId, long userId);

    @Query(" select e FROM Event e where " +
            "(e.eventDate between :rangeStart and :rangeEnd) and " +
            "((:initiators is null) or e.initiator.id in :initiators) and " +
            "((:categories is null) or e.category.id in :categories) and " +
            "((:states is null) or e.state in :states) " +
            "ORDER BY e.eventDate DESC ")
    List<Event> findByParameters(List<Long> initiators,
                                 List<EventState> states,
                                 List<Long> categories,
                                 LocalDateTime rangeStart,
                                 LocalDateTime rangeEnd,
                                 Pageable pageable);

    @Query(" select e FROM Event e WHERE " +
            "(e.state = 'PUBLISHED') and " +
            "(e.eventDate between :rangeStart and :rangeEnd) and " +
            "(:text is null) or ((lower(e.annotation) like %:text% or lower(e.description) like %:text%)) and " +
            "((:categories is null) or e.category.id in :categories) and " +
            "((:paid is null) or e.paid = :paid) and " +
            "((:onlyAvailable is null) or e.participantLimit > e.participants.size) " +
            "ORDER BY e.views ")
    List<Event> findByParametersForPublicSortViews(String text,
                                                   List<Long> categories,
                                                   Boolean paid,
                                                   LocalDateTime rangeStart,
                                                   LocalDateTime rangeEnd,
                                                   Boolean onlyAvailable,
                                                   Pageable pageable);

    @Query(" select e FROM Event e WHERE " +
            "(e.state = 'PUBLISHED') and " +
            "(e.eventDate between :rangeStart and :rangeEnd) and " +
            "(:text is null) or ((lower(e.annotation) like %:text% or lower(e.description) like %:text%)) and " +
            "((:categories is null) or e.category.id in :categories) and " +
            "((:paid is null) or e.paid = :paid) and " +
            "((:onlyAvailable is null) or e.participantLimit > e.participants.size) " +
            "ORDER BY e.eventDate ")
    List<Event> findByParametersForPublicSortEventDate(String text,
                                                       List<Long> categories,
                                                       Boolean paid,
                                                       LocalDateTime rangeStart,
                                                       LocalDateTime rangeEnd,
                                                       Boolean onlyAvailable,
                                                       Pageable pageable);

    Set<Event> getByIdIn(Collection<Long> ids);

    Optional<Event> findAllByCategoryId(long categoryId);
}
