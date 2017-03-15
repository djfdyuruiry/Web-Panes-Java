package webbrowserpoc.interfaces;

import webbrowserpoc.model.WebBrowserConfig;
import webbrowserpoc.exception.WebBrowserConfigLoadingException;

public interface ConfigProvider {
    WebBrowserConfig loadConfig() throws WebBrowserConfigLoadingException;
}
