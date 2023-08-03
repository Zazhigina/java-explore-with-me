package ru.practicum.user.server;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.user.mapper.UserMapper.toUser;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {
    private final UserRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAll(List<Long> ids, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return UserMapper.toUserResponseDtoCollection(getUsers(ids, pageable));
    }

    @Override
    @Transactional
    public UserDto create(NewUserRequest dto) {
        User user = toUser(dto);
        return UserMapper.toUserDto(repository.save(user));
    }

    @Override
    @Transactional
    public void delete(Long userId) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User with id=%d was not found", userId),
                        User.class,
                        LocalDateTime.now()));
        repository.deleteById(userId);
    }

    void getUser(Long id) {
        repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("User with id=%d was not found", id),
                        User.class,
                        LocalDateTime.now())
                );
    }

    private List<User> getUsers(List<Long> ids, Pageable pageable) {
        if (ids != null) {
            return repository.findAllByIdIn(ids, pageable);
        } else {
            return repository.findAll(pageable).toList();
        }
    }

}
