package webbrowserpoc.service;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.web.WebEngine;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.html.HTMLFormElement;
import org.w3c.dom.html.HTMLInputElement;
import webbrowserpoc.interfaces.WebEngineAutoLoginService;
import webbrowserpoc.model.HtmlLoginForm;
import webbrowserpoc.exception.LoginFormDiscoveryException;
import webbrowserpoc.provider.AutoLoginCredentialsProvider;

import javax.inject.Inject;
import java.util.Dictionary;
import java.util.Hashtable;

public class WebEngineAutoLoginServiceImpl implements WebEngineAutoLoginService {
    private final Dictionary<WebEngine, ChangeListener<Document>> autoLoginListeners;
    private final Dictionary<WebEngine, EventListener> dynamicAutoLoginListeners;

    @Inject
    private AutoLoginCredentialsProvider autoLoginCredentialsProvider;

    public WebEngineAutoLoginServiceImpl() {
        autoLoginListeners = new Hashtable<>();
        dynamicAutoLoginListeners = new Hashtable<>();
    }

    public void configureAutoLogin(WebEngine engine) {
        ReadOnlyObjectProperty<Document> documentProperty = engine.documentProperty();
        ChangeListener<Document> autoLoginListener = (ov, oldDoc, doc) -> autoLoginHandler(engine);

        autoLoginListeners.put(engine, autoLoginListener);

        documentProperty.addListener(autoLoginListener);
    }

    private void autoLoginHandler(WebEngine webEngine) {
        Document document = webEngine.getDocument();

        if (document == null) {
            return;
        }

        NodeList forms = document.getElementsByTagName("form");

        if (forms.getLength() < 1) {
            return;
        }

        try {
            HtmlLoginForm form = discoverLoginForm(forms);

            form.usernameInput.setValue(autoLoginCredentialsProvider.getUserName());
            form.passwordInput.setValue(autoLoginCredentialsProvider.getPassword());

            form.formElement.submit();
        } catch (LoginFormDiscoveryException ex) {
            waitForLoginFormToBeDynamicallyInserted(webEngine);
            return;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        removeEventListeners(webEngine);
    }

    private HtmlLoginForm discoverLoginForm(NodeList forms) throws LoginFormDiscoveryException {
        for (int formIdx = 0; formIdx < forms.getLength(); formIdx++) {
            HTMLFormElement form = (HTMLFormElement) forms.item(formIdx);
            NodeList inputs = form.getElementsByTagName("input");
            HtmlLoginForm loginForm = new HtmlLoginForm();

            loginForm.formElement = form;

            for (int inputIdx = 0; inputIdx < inputs.getLength(); inputIdx++) {
                HTMLInputElement input = (HTMLInputElement) inputs.item(inputIdx);
                String inputName = input.getName();

                if (inputName == null) {
                    continue;
                }

                switch (inputName) {
                    case "login":
                    case "username":
                        loginForm.usernameInput = input;
                        break;
                    case "password":
                        loginForm.passwordInput = input;
                        break;
                }
            }

            if (loginForm.usernameInput != null && loginForm.passwordInput != null) {
                return loginForm;
            }
        }

        throw new LoginFormDiscoveryException();
    }

    private void waitForLoginFormToBeDynamicallyInserted(WebEngine webEngine) {
        Document document = webEngine.getDocument();
        EventListener listener = ev -> autoLoginHandler(webEngine);

        dynamicAutoLoginListeners.put(webEngine, listener);

        ((EventTarget) document).addEventListener("DOMNodeInserted", listener, false);
    }


    private void removeEventListeners(WebEngine webEngine) {
        ReadOnlyObjectProperty<Document> engineDocumentProperty = webEngine.documentProperty();
        Document document = webEngine.getDocument();

        // static login listeners
        ChangeListener<Document> autoLoginListener = autoLoginListeners.get(webEngine);

        if (autoLoginListener != null) {
            engineDocumentProperty.removeListener(autoLoginListener);
            autoLoginListeners.remove(webEngine);
        }

        // dynamic login listeners
        EventListener dynamicAutoLoginListener = dynamicAutoLoginListeners.get(webEngine);

        if (dynamicAutoLoginListener != null){
            ((EventTarget) document).removeEventListener("DOMNodeInserted",dynamicAutoLoginListener, false);
        }
    }
}
