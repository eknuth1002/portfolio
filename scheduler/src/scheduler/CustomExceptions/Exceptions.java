/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.CustomExceptions;

import scheduler.Alerts;

/**
 *
 * @author Elliot Knuth
 */
public class Exceptions {
    
    //For exceptions dealing with outside 9a-5p hours and overlapping appointments
    public static class InvalidTimeException extends RuntimeException {
        private String exceptionMessage = "";
        private InvalidTimeException() {}
        
        public InvalidTimeException(String message) {
            exceptionMessage = message;
        }
        
        @Override
        public String getMessage() {
            return exceptionMessage;
        }

    }
    
    public static class InvalidField extends RuntimeException {
        public InvalidField() {
            
        }
    }
    
    public static class InvalidUsernameorPassword extends RuntimeException {

        private InvalidUsernameorPassword() {}
        
        public InvalidUsernameorPassword(String title, String message) {
                Alerts.errorAlert(title, message);
        }
    }
}

