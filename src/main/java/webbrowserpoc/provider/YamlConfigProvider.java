package webbrowserpoc.provider;

import java.io.InputStream;
import java.io.InputStreamReader;

import com.esotericsoftware.yamlbeans.YamlReader;
import webbrowserpoc.interfaces.ConfigProvider;
import webbrowserpoc.model.WebBrowserConfig;
import webbrowserpoc.exception.WebBrowserConfigLoadingException;


public class YamlConfigProvider implements ConfigProvider {
    @Override
    public WebBrowserConfig loadConfig() throws WebBrowserConfigLoadingException {
        try {
            InputStream configFileStream = getClass().getClassLoader().getResourceAsStream("config.yaml");
            InputStreamReader inputStreamReader = new InputStreamReader(configFileStream);

            YamlReader yamlReader = new YamlReader(inputStreamReader);
            WebBrowserConfig config = yamlReader.read(WebBrowserConfig.class);

            return config;
        } catch (Exception ex) {
            throw new WebBrowserConfigLoadingException("Error while loading config from YAML file.", ex);
        }
    }
}
