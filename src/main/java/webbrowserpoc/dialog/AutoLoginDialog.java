package webbrowserpoc.dialog;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

import java.util.Optional;

/**
 * Adapted from http://code.makery.ch/blog/javafx-dialogs-official/
 */
public class AutoLoginDialog {
    private final Dialog<Pair<String, String>> dialog;
    private final ButtonType loginButtonType;
    private TextField usernameField;
    private PasswordField passwordField;

    public AutoLoginDialog () {
        dialog = new Dialog<>();
        dialog.setTitle("Auto Login");
        dialog.setHeaderText("Please enter your common Username and Password");

        loginButtonType = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        GridPane grid = buildContentGrid();
        disableConfirmButtonUntilTextIsEntered();

        dialog.getDialogPane().setContent(grid);
        Platform.runLater(usernameField::requestFocus);

        setupResultConverter();
    }

    private GridPane buildContentGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        usernameField = new TextField();
        usernameField.setPromptText("Username");

        passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        grid.add(new Label("Username:"), 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(passwordField, 1, 1);

        return grid;
    }

    private void disableConfirmButtonUntilTextIsEntered() {
        Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
        loginButton.setDisable(true);

        usernameField.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty());
        });
    }

    private void setupResultConverter() {
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return new Pair<>(usernameField.getText(), passwordField.getText());
            }

            return null;
        });
    }

    public Optional<Pair<String, String>> showAndWait() {
        return dialog.showAndWait();
    }
}
