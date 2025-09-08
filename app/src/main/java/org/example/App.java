package org.example;

import org.example.entities.Ticket;
import org.example.entities.Train;
import org.example.entities.User;
import org.example.services.UserService;
import org.example.utils.HashPassword;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class App {
    public static void main(String[] args) {
        System.out.println("IRCTC Application is running");
        Scanner scanner = new Scanner(System.in);
        int option = 0;
        UserService userService;

        try {
            userService = new UserService();
        } catch (Exception e) {
            System.out.println("Something went wrong while loading users: " + e.getMessage());
            return;
        }

        while (option != 7) {
            System.out.println("Choose one option");
            System.out.println("1. Signup");
            System.out.println("2. Login");
            System.out.println("3. View Bookings");
            System.out.println("4. Cancel Booking");
            System.out.println("5. Search Train");
            System.out.println("7. Exit");

            option = scanner.nextInt();
            scanner.nextLine(); // consume leftover newline

            switch (option) {
                case 1:
                    System.out.println("Enter your name:");
                    String name = scanner.nextLine(); // can include spaces

                    System.out.println("Enter your password:");
                    String password = scanner.nextLine(); // full password

                    User newUser = new User(
                            name,
                            password,
                            HashPassword.createPassword(password),
                            new ArrayList<>(),
                            UUID.randomUUID().toString()
                    );

                    try {
                        if (userService.signUp(newUser)) {
                            System.out.println("Successfully signed up!");
                        } else {
                            System.out.println("Couldn't sign up (username may already exist).");
                        }
                    } catch (Exception e) {
                        System.out.println("Error while adding user: " + e.getMessage());
                    }
                    break;

                case 2:
                    System.out.println("Enter your name:");
                    String loginName = scanner.nextLine();

                    System.out.println("Enter your password:");
                    String loginPassword = scanner.nextLine();

                    boolean success = userService.login(loginName, loginPassword);
                    if (success) {
                        System.out.println("Login successful! Welcome " + loginName);
                    } else {
                        System.out.println("Invalid username or password!");
                    }
                    break;

                case 3:
                    System.out.println("Your booking");
                    System.out.println("-------------------------------------");
                    List<Ticket> bookings = userService.fetchBooking();

                    for (Ticket t : bookings) {
                        System.out.printf("%s | %s -> %s | Ticket ID: %s%n",
                                t.getDateOfTravel(),
                                t.getSource(),
                                t.getDestination(),
                                t.getTicketId());
                    }
                    break;

                case 4:
                    System.out.println("Enter ticket Id: ");
                    String ticketId = scanner.nextLine();
                    if (userService.cancelBooking(ticketId)) {
                        System.out.println("Your booking was cancelled successfully.");
                    } else {
                        System.out.println("Error occurred while cancelling booking.");
                    }
                    break;
                case 5:
                    System.out.println("Source");
                    String src=scanner.nextLine();
                    System.out.println("Destination");
                    String dest=scanner.nextLine();
                    List<Train>trainList=userService.getTrains(src,dest);
                    System.out.println("Debuggg");
                    for(Train train:trainList){
                        System.out.printf("train with trainId %s",train.getTrainId());
                    }
                    break;

                case 7:
                    System.out.println("Exiting application. Goodbye!");
                    break;

                default:
                    System.out.println("Invalid option, please try again.");
            }
        }
    }
}
