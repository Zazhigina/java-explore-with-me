package ru.practicum.event.mapper;

import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.model.Category;
import ru.practicum.enam.EventState;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.Location;
import ru.practicum.event.model.update.UpdateEvent;
import ru.practicum.user.dto.UserShortDto;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.*;

public class EventMapper {

    public static EventShortDto toEventShortDto(Event event, CategoryDto categoryDto, UserShortDto initiator) {
        EventShortDto eventShortDto = EventShortDto.builder()
                .id(event.getId())
                .category(categoryDto)
                .annotation(event.getAnnotation())
                .confirmedRequests(0)
                .eventDate(event.getEventDate())
                .initiator(initiator)
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
        return eventShortDto;
    }

    public static EventFullDto toEventFullDto(Event event,
                                              CategoryDto categoryDto,
                                              UserShortDto initiator,
                                              Location locationDto) {
        return EventFullDto.builder()
                .id(event.getId())
                .category(categoryDto)
                .annotation(event.getAnnotation())
                .confirmedRequests(Optional.ofNullable(event.getParticipants()).orElse(Set.of()).size())
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .initiator(initiator)
                .location(locationDto)
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
    }

    public static Event toEvent(NewEventDto newEventDto,
                                Category category,
                                Location location,
                                User initiator) {
        return Event.builder()
                .category(category)
                .annotation(newEventDto.getAnnotation())
                .createdOn(LocalDateTime.now())
                .description(newEventDto.getDescription())
                .eventDate(newEventDto.getEventDate())
                .initiator(initiator)
                .location(location)
                .paid(newEventDto.getPaid())
                .participantLimit(newEventDto.getParticipantLimit())
                .requestModeration(newEventDto.getRequestModeration())
                .state(EventState.PENDING)
                .title(newEventDto.getTitle())
                .views(0)
                .participants(Set.of())
                .build();
    }


    public static Event updateEvent(Event donor, Event recipient) {
        if (recipient.getAnnotation() != null) donor.setAnnotation(recipient.getAnnotation());
        if (recipient.getCategory() != null) donor.setCategory(recipient.getCategory());
        if (recipient.getDescription() != null) donor.setDescription(recipient.getDescription());
        if (recipient.getEventDate() != null) donor.setEventDate(recipient.getEventDate());
        if (recipient.getLocation() != null) donor.setLocation(recipient.getLocation());
        if (recipient.getPaid() != null) donor.setPaid(recipient.getPaid());
        if (recipient.getParticipantLimit() != null) donor.setParticipantLimit(recipient.getParticipantLimit());
        if (recipient.getRequestModeration() != null) donor.setRequestModeration(recipient.getRequestModeration());
        if (recipient.getState() != null) donor.setState(recipient.getState());
        if (recipient.getTitle() != null) donor.setTitle(recipient.getTitle());
        if (recipient.getPublishedOn() != null) donor.setPublishedOn(recipient.getPublishedOn());
        if (recipient.getParticipants() != null) donor.setParticipants(recipient.getParticipants());
        return donor;
    }

    private static Event toEvent(UpdateEvent updateUserDto) {
        Event event = new Event();
        if (updateUserDto.getAnnotation() != null) event.setAnnotation(updateUserDto.getAnnotation());
        if (updateUserDto.getCategory() != null) event.setCategory(
                Category.builder().id(updateUserDto.getCategory()).build());
        if (updateUserDto.getDescription() != null) event.setDescription(
                updateUserDto.getDescription());
        if (updateUserDto.getEventDate() != null) event.setEventDate(updateUserDto.getEventDate());
        if (updateUserDto.getLocation() != null) event.setLocation(updateUserDto.getLocation());
        if (updateUserDto.getPaid() != null) event.setPaid(
                updateUserDto.getPaid());
        if (updateUserDto.getParticipantLimit() != null) event.setParticipantLimit(
                updateUserDto.getParticipantLimit());
        if (updateUserDto.getRequestModeration() != null) event.setRequestModeration(
                updateUserDto.getRequestModeration());
        if (updateUserDto.getTitle() != null) event.setTitle(
                updateUserDto.getTitle());
        return event;
    }

    private static EventState findState(String str) {
        if (str == null) return null;
        if (str.equals("CANCEL_REVIEW")) return EventState.CANCELED;
        if (str.equals("PUBLISH_EVENT")) return EventState.PUBLISHED;
        if (str.equals("REJECT_EVENT")) return EventState.CANCELED;
        if (str.equals("SEND_TO_REVIEW")) return EventState.PENDING;
        return null;
    }

    public static Event updateEventAdmin(UpdateEvent adminRequest) {
        Event event = toEvent(adminRequest);
        event.setState(findState(String.valueOf(adminRequest.getStateAction())));
        event.setPublishedOn(LocalDateTime.now());
        return event;
    }

    public static Event updateEventUser(UpdateEvent userRequest) {
        Event event = toEvent(userRequest);
        event.setState(findState(String.valueOf(userRequest.getStateAction())));
        return event;
    }
}
