package webbrowserpoc.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextInputDialog;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Pair;
import webbrowserpoc.dialog.AutoLoginDialog;
import webbrowserpoc.interfaces.ConfigProvider;
import webbrowserpoc.interfaces.WebEngineAutoLoginService;
import webbrowserpoc.model.WebBrowserConfig;
import webbrowserpoc.exception.WebBrowserConfigLoadingException;
import webbrowserpoc.provider.AutoLoginCredentialsProvider;

import javax.inject.Inject;
import java.util.Optional;

public class WebBrowsersController {
    @FXML private WebView mainWebView;
    @FXML private WebView leftMiniWebView;
    @FXML private WebView rightMiniWebView;

    @Inject private ConfigProvider configProvider;
    @Inject private AutoLoginCredentialsProvider autoLoginCredentialsProvider;
    @Inject private WebEngineAutoLoginService autoLoginService;

    @FXML
    protected void initialize() {
        WebBrowserConfig webBrowserConfig;

        try {
            webBrowserConfig = configProvider.loadConfig();
        } catch (WebBrowserConfigLoadingException e) {
            e.printStackTrace();
            webBrowserConfig = new WebBrowserConfig();
        }

        if (webBrowserConfig.enableAutoLogin){
            getCredentialsAndConfigureAutoLogin();
        }

        loadWebView(mainWebView, webBrowserConfig.mainWebViewUrl);
        loadWebView(leftMiniWebView, webBrowserConfig.leftMiniWebViewUrl);
        loadWebView(rightMiniWebView, webBrowserConfig.rightMiniWebViewUrl);
    }

    private void loadWebView(WebView webViewToLoad, String url) {
        WebEngine engine = webViewToLoad.getEngine();
        engine.load(url);
    }

    private void getCredentialsAndConfigureAutoLogin() {

        AutoLoginDialog autoLoginDialog = new AutoLoginDialog();

        Optional<Pair<String, String>> nullableUsernameAndPassword = autoLoginDialog.showAndWait();

        if (!nullableUsernameAndPassword.isPresent()) {
            return;
        }

        Pair<String, String> usernameAndPassword = nullableUsernameAndPassword.get();

        autoLoginCredentialsProvider.setUserName(usernameAndPassword.getKey());
        autoLoginCredentialsProvider.setPassword(usernameAndPassword.getValue());

        configureAutoLogin(mainWebView);
        configureAutoLogin(leftMiniWebView);
        configureAutoLogin(rightMiniWebView);
    }

    private Optional<String> showInputDialog(String title, String prompt) {
        TextInputDialog dialog = new TextInputDialog();

        dialog.setTitle(title);
        dialog.setContentText(prompt);

        return dialog.showAndWait();
    }

    private void configureAutoLogin(WebView webViewToConfigure) {
        WebEngine engine = webViewToConfigure.getEngine();
        autoLoginService.configureAutoLogin(engine);
    }
}
