package ru.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.enam.RequestStatus;
import ru.practicum.request.dto.RequestCountDto;
import ru.practicum.request.model.Request;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findAllByRequester_Id(Long userId);

    List<Request> getAllByIdIn(List<Long> requestIds);

    @Query("select new ru.practicum.request.dto.RequestCountDto(r.event.id, count(r.id)) " +
            "from Request as r " +
            "where r.event.id = ?1 " +
            "AND r.status = ?2 " +
            "group by r.event.id " +
            "order by count(r.id) desc")
    RequestCountDto findRequestCountDtoByEventIdAndStatus(Long eventId, RequestStatus status);

    @Query("select new ru.practicum.request.dto.RequestCountDto(r.event.id, count(r.id)) " +
            "from Request as r " +
            "where r.event.id IN ?1 " +
            "AND r.status = ?2 " +
            "group by r.event.id " +
            "order by count(r.id) desc")
    List<RequestCountDto> findRequestCountDtoListByEventId(List<Long> eventIdList, RequestStatus status);

    List<Request> findByEventInitiatorIdAndEventId(Long userId, Long eventId);

    Request findOneByEventIdAndRequesterId(Long eventId, Long userId);
}

