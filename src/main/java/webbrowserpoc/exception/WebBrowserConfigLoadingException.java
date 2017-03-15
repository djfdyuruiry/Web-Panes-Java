package webbrowserpoc.exception;

public class WebBrowserConfigLoadingException extends Exception{
    public WebBrowserConfigLoadingException (String error, Exception cause) {
        super(error, cause);
    }
}
