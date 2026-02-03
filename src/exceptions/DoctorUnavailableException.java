package exceptions;

public class DoctorUnavailableException extends RuntimeException {
    public DoctorUnavailableException(String msg) {
        super(msg);
    }
}