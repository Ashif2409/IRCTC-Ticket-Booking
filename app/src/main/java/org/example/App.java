package org.example;

import org.example.entities.Ticket;
import org.example.entities.Train;
import org.example.entities.User;
import org.example.services.TrainService;
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
        TrainService trainService;

        try {
            userService = new UserService();
            trainService = new TrainService();
        } catch (Exception e) {
            System.out.println("Something went wrong while loading services: " + e.getMessage());
            return;
        }

        while (option != 10) {
            System.out.println("\nChoose one option:");
            System.out.println("1. Signup");
            System.out.println("2. Login");
            System.out.println("3. View Bookings");
            System.out.println("4. Cancel Booking");
            System.out.println("5. Search Train");
            System.out.println("6. Change Password");
            System.out.println("7. Logout");
            System.out.println("8. Find Train by Id");
            System.out.println("9. Book Ticket");
            System.out.println("10. Exit");

            option = scanner.nextInt();
            scanner.nextLine(); // consume leftover newline

            switch (option) {
                case 1:
                    System.out.println("Enter your name:");
                    String name = scanner.nextLine();

                    System.out.println("Enter your password:");
                    String password = scanner.nextLine();

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
                    System.out.println("Your bookings:");
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
                    System.out.println("Source:");
                    String src = scanner.nextLine();
                    System.out.println("Destination:");
                    String dest = scanner.nextLine();
                    List<Train> trainList = userService.getTrains(src, dest);
                    for (Train train : trainList) {
                        List<List<Integer>> seats = train.getSeats();
                        int remainingSeats = 0;
                        for (List<Integer> seat : seats) {
                            for (Integer s : seat) {
                                if (s == 0) {
                                    remainingSeats++;
                                }
                            }
                        }
                        System.out.printf("Train with trainId %s has %d remaining seats%n",
                                train.getTrainId(), remainingSeats);
                    }
                    break;

                case 6:
                    System.out.println("Enter your old password:");
                    String oldPassword = scanner.nextLine();
                    System.out.println("Enter your new password:");
                    String newPassword = scanner.nextLine();
                    if (userService.changePassword(oldPassword, newPassword)) {
                        System.out.println("Password changed successfully.");
                    } else {
                        System.out.println("Error while changing password.");
                    }
                    break;

                case 7:
                    if (userService.Logout()) {
                        System.out.println("You have successfully logged out.");
                    } else {
                        System.out.println("Error while logging out.");
                    }
                    break;

                case 8:
                    System.out.println("Enter the train ID:");
                    String trainId = scanner.nextLine();
                    Train train = trainService.searchTrainById(trainId);
                    if (train == null) {
                        System.out.println("No train found with that ID.");
                        break;
                    }
                    List<List<Integer>> seats = train.getSeats();
                    int remainingSeats = 0;
                    for (List<Integer> seat : seats) {
                        for (Integer s : seat) {
                            if (s == 0) {
                                remainingSeats++;
                            }
                        }
                    }
                    System.out.printf("Train number %s has %d remaining seats%n",
                            train.getTrainNo(), remainingSeats);
                    break;

                case 9:
                    System.out.println("Enter the date of travel:");
                    String date = scanner.nextLine();
                    System.out.println("Enter the source:");
                    String source = scanner.nextLine();
                    System.out.println("Enter the destination:");
                    String destination = scanner.nextLine();
                    System.out.println("Enter trainId:");
                    String trainIdForBooking = scanner.nextLine();
                    System.out.println("Enter seat number (e.g., 3B):");
                    String seatNum = scanner.nextLine();

                    Boolean trainBook = userService.bookTicket(date, source, destination, trainIdForBooking, seatNum);
                    if (trainBook) {
                        System.out.println("You successfully booked the ticket.");
                    } else {
                        System.out.println("Error while booking ticket.");
                    }
                    break;

                case 10:
                    System.out.println("Exiting application. Goodbye!");
                    break;

                default:
                    System.out.println("Invalid option, please try again.");
            }
        }
    }
}
