package org.example.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.entities.Ticket;
import org.example.entities.Train;
import org.example.entities.User;
import org.example.utils.HashPassword;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class UserService {
    private  User user=null;
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
        if(this.user==null){
            System.out.println("You need to login first");
            return new ArrayList<>();
        }
        return user.getTicketBooked();
    }

    public Boolean cancelBooking(String ticketId) {
        try {
            if(this.user==null){
                System.out.println("You need to login first");
                return false;
            }
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

    public Boolean Logout(){
        try{
            if(this.user==null){
                System.out.println("You need to login first");
                return false;
            }
            this.user=null;
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public Boolean changePassword(String oldPassword,String newPassword){
        try{
            if(this.user==null){
                System.out.println("You need to login first");
                return false;
            }
            Boolean isPasswordCorrect=HashPassword.checkPassword(oldPassword,user.getHashedPassword());
            if(isPasswordCorrect){
                user.setPassword(newPassword);
                user.setHashedPassword(HashPassword.createPassword(newPassword));
                userList = userList.stream()
                        .map(u -> u.getUserId().equals(user.getUserId()) ? user : u)
                        .toList();
                saveAllUsers();
                return true;
            }else {
                System.out.println("Incorrect Password");
                return false;
            }
        }catch (Exception e){
            return false;
        }
    }

    public Boolean bookTicket(String dateOfTravel, String src, String dest, String trainId, String seatNum) {
        if (this.user == null) {
            System.out.println("You need to login first");
            return false;
        }

        Train train = trainService.searchTrainById(trainId);
        if (train == null) {
            System.out.println("Train not found!");
            return false;
        }

        List<List<Integer>> seats = train.getSeats();

        // Parse seat (assume "rowNumber + Letter", e.g. "3B")
        String rowPart = seatNum.replaceAll("\\D", "");
        int row = Integer.parseInt(rowPart);
        char colChar = seatNum.charAt(seatNum.length() - 1);
        int col = colChar - 'A';

        // Validate bounds
        if (row < 0 || row >= seats.size() || col < 0 || col >= seats.get(row).size()) {
            System.out.println("Invalid seat number!");
            return false;
        }

        // Check if already booked
        if (seats.get(row).get(col) == 1) {
            System.out.println("Seat already booked!");
            return false;
        }

        // Book the seat
        seats.get(row).set(col, 1);
        train.setSeats(seats);

        // Create ticket
        Ticket ticket = new Ticket(
                UUID.randomUUID().toString(),
                user.getUserId(),
                src,
                dest,
                dateOfTravel,
                train,
                seatNum
        );

        // Save train and update user bookings
        try {
            trainService.saveTrain(train);
            user.getTicketBooked().add(ticket);
            return true;
        } catch (IOException e) {
            return false;
        }
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
