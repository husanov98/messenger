package uz.mh.messenger.exceptions;

public class SchedulerNotStartException extends RuntimeException{
    private String message;
    public SchedulerNotStartException(String message){
        this.message = message;
    }
}
