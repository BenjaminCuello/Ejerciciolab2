package service;

import dao.UserRepository;
import ui.LoginController; //violacion de capas no permitidas

public class UserService {
    private final UserRepository repository = new UserRepository();

    public void registerUser(String name) {
        repository.save(name);
    }

    public void getUserInfo(String name) {
        System.out.println("User info for: " + name);
    }
}