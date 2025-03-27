package catering.businesslogic;

public class UseCaseLogicException extends Exception {
  public UseCaseLogicException(String message) {
    super(message);
  }

  public UseCaseLogicException(String message, Throwable cause) {
    super(message, cause);
  }

  public UseCaseLogicException() {
    super();
  }
}
