package webbrowserpoc;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import webbrowserpoc.interfaces.ConfigProvider;
import webbrowserpoc.interfaces.WebEngineAutoLoginService;
import webbrowserpoc.provider.AutoLoginCredentialsProvider;
import webbrowserpoc.provider.YamlConfigProvider;
import webbrowserpoc.service.WebEngineAutoLoginServiceImpl;

public class WebBrowserPocModule extends AbstractModule {
    @Override protected void configure() {
        // services
        bind(WebEngineAutoLoginService.class).to(WebEngineAutoLoginServiceImpl.class).in(Singleton.class);

        // providers
        bind(ConfigProvider.class).to(YamlConfigProvider.class).in(Singleton.class);
        bind(AutoLoginCredentialsProvider.class).in(Singleton.class);
    }
}
