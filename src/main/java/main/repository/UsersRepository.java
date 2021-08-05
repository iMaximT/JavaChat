package main.repository;

import main.model.Users;
import org.springframework.data.repository.CrudRepository;

public interface UsersRepository extends CrudRepository<Users, Integer> {
    Users getBySessionId(String sessionId);
}
