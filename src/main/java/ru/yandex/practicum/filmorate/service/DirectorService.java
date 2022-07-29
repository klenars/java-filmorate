package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectorService {

    private final DirectorStorage directorStorage;

    public Director add(Director director){
        validation(director);
        directorStorage.add(director);
        log.info("Added director: {}, id: {}", director.getName(), director.getId());
        return getById(director.getId());
    }

    public Director update(Director director){
        isExist(director.getId());
        validation(director);
        directorStorage.update(director);
        log.info("Updated director id: {}", director.getId());
        return getById(director.getId());
    }

    public Director getById(int id) {
        isExist(id);
        return directorStorage.get(id);
    }

    public List<Director> getAll() {
        return directorStorage.getAll();
    }

    public void deleteDirectorById(int directorId) {
        isExist(directorId);
        directorStorage.deleteDirectorById(directorId);
    }

    private void isExist(int id) {
        if (!directorStorage.isExistById(id)) {
            throw new ResourceNotFoundException(String.format("Director with id: %d doesn't exist!", id));
        }
    }

    private void validation(Director director) {
        String errorMessage = null;

        if (director.getName().isBlank()) {
            errorMessage = "Name is empty!";
        }

        if (errorMessage != null) {
            throw new ValidationException(errorMessage);
        }
    }
}