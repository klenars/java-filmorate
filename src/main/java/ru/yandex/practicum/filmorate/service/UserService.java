package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    //private static int idForUsers = 1;
    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User add(User user) {
        validation(user);
//            int id = idForUsers++;
//            user.setId(id);
        userStorage.add(user);
        log.info("Added user name: {}, id: {}", user.getName(), user.getId());

        //TODO: Сделать возврат юзера с присвоенным id из БД
        return user;
    }

    public User getById(int id) {
        isExists(id);
        return userStorage.get(id);
    }

    public User update(User user) {
        isExists(user.getId());
        validation(user);
        userStorage.add(user);
        log.info("Updated user id: {}", user.getId());
        return user;
    }

    public List<User> getAll() {
        return new ArrayList<>(userStorage.getAll());
    }

    public void addFriend(int id, int friendId) {
        isExists(id);
        isExists(friendId);

//        TODO: Изменить логику добавления в друзья
//        userStorage.get(id).getFriendsIDs().add(friendId);
//        userStorage.get(friendId).getFriendsIDs().add(id);
    }

    public void deleteFriend(int id, int friendId) {
        isExists(id);
        isExists(friendId);

        userStorage.get(id).getFriendsIDs().remove(friendId);
        userStorage.get(friendId).getFriendsIDs().remove(id);
    }

    public List<User> getAllFriends(int id) {
        isExists(id);

        //TODO: Проверить логику после изменения френдов на мапу
        return userStorage.get(id).getFriendsIDs().keySet().stream()
                .map(userStorage::get)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(int id, int otherId) {

        //TODO: Проверить логику после изменения френдов на мапу
        return userStorage.get(id).getFriendsIDs().keySet().stream()
                .filter(friendId -> userStorage.get(otherId).getFriendsIDs().containsKey(friendId))
                .map(userStorage::get)
                .collect(Collectors.toList());
    }

    public void isExists(int id) {
        if (!userStorage.isExists(id)) {
            log.warn(String.format("User with id: %d doesn't exist!", id));
            throw new ResourceNotFoundException(String.format("User with id: %d doesn't exist!", id));
        }
    }

    private boolean validation(User user) {
        if (user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }

        String errorMessage = null;

        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            errorMessage = "E-mail is wrong!";
        } else if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            errorMessage = "Login is wrong!";
        } else if (user.getBirthday().isAfter(LocalDate.now())) {
            errorMessage = "Birthday can't be in the future!";
        }

        if (errorMessage != null) {
            log.warn(errorMessage);
            throw new ValidationException(errorMessage);
        } else {
            return true;
        }
    }


}
