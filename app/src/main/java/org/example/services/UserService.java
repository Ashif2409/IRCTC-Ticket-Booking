package org.example.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.entities.Ticket;
import org.example.entities.Train;
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
    public  final String USER_DB_PATH="D:/CSE/webDevelopment/IRCTC-Ticket-Booking/app/src/main/java/org/example/localDb/user.json";
    private final ObjectMapper mapper=new ObjectMapper();
    private final TrainService trainService;

    public UserService(User user1) throws IOException{
        user=user1;
        this.trainService=new TrainService();
        loadAllUsers();
    }

    public UserService() throws IOException{
        this.trainService=new TrainService();
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

    public List<Ticket> fetchBooking(){
        return user.getTicketBooked();
    }

    public Boolean cancelBooking(String ticketId) {
        try {
            // Remove ticket from the current user
            boolean removed = user.getTicketBooked()
                    .removeIf(ticket -> ticket.getTicketId().equals(ticketId));

            if (removed) {
                userList = userList.stream()
                        .map(u -> u.getUserId().equals(user.getUserId()) ? user : u)
                        .toList();

                saveAllUsers();
                return true;
            }
            return false;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }

    public List<Train> getTrains(String src,String desc){
        return trainService.searchTrain(src,desc);
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
