package org.example.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.entities.User;
import org.example.utils.HashPassword;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserService {
    private  User user;
    private  List<User> userList;
    public  final String USER_DB_PATH="../localDb/user.json";
    private final ObjectMapper mapper=new ObjectMapper();

    public UserService(User user1) throws IOException{
        user=user1;
        loadAllUsers();
    }

    public UserService() throws IOException{
        loadAllUsers();
    }

    private  void loadAllUsers() throws IOException {
        File users=new File(USER_DB_PATH);
        if(!users.exists() || users.length()==0){
            this.userList=new ArrayList<>();
            return;
        }
        this.userList = mapper.readValue(users, new TypeReference<List<User>>() {});

    }

    public Boolean login(String name,String password){
        Optional<User> userExist=userList.stream().filter(
                u->u.getName().equals(name) &&
                        HashPassword.checkPassword(password,u.getHashedPassword())).findFirst();
        if(userExist.isPresent()){
            this.user=userExist.get();
            return true;
        }
        return false;
    }

    public Boolean signUp(User newUser) throws IOException{
        Optional<User> isUserExist=userList.stream().findFirst().filter(u->u.getName().equals(newUser.getName()));
        if(isUserExist.isPresent()){
            System.out.printf("User with this username %s already exists%n", newUser.getName());
            return false;
        }
        userList.add(newUser);
        saveAllUsers();
        return true;
    }

    private void saveAllUsers() throws IOException{
        File users=new File(USER_DB_PATH);
        if(users.getParentFile()!=null){
            users.getParentFile().mkdirs();
        }
        mapper.writerWithDefaultPrettyPrinter()
                .writeValue(users,userList);
    }
}
