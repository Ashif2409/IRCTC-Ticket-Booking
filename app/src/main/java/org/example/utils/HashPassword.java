package org.example.utils;

import org.mindrot.jbcrypt.BCrypt;

public class HashPassword {
    public static String createPassword(String password){
        return BCrypt.hashpw(password,BCrypt.gensalt(12));
    }
    public static Boolean checkPassword(String plainPassword,String hashedPassword){
        return BCrypt.checkpw(plainPassword,hashedPassword);
    }
}
