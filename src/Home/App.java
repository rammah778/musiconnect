package Home;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import tray.animations.AnimationType;
import tray.notification.NotificationType;
import tray.notification.TrayNotification;

import java.io.IOException;
import java.sql.Connection;

import static Home.Helper.setupDB;

public class App extends Application {
    double x, y;
    public static Connection connection;
    public static Scene scene;
    public static String userEmail = "";
    public static Integer userCountryId;
    public static Integer songID;

    public static Integer getSongID() {
        return songID;
    }

    public static void setSongID(Integer songID) {
        App.songID = songID;
    }

    public static Integer getUserCountryId() {
        return userCountryId;
    }

    public static void setUserCountryId(Integer userCountryId) {
        App.userCountryId = userCountryId;
    }

    public static String getUserEmail() {
        return userEmail;
    }

    public static void setUserEmail(String userEmail) {
        App.userEmail = userEmail;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        String url = "jdbc:postgresql://localhost:5432/musiconnect?user=postgres&password=postgres";
        connection = setupDB(url);
        scene = new Scene(loadFXML("login"));
        primaryStage.initStyle(StageStyle.UNDECORATED);
        scene.setOnMousePressed(mouseEvent -> {
            x = mouseEvent.getSceneX();
            y = mouseEvent.getSceneY();
        });
        scene.setOnMouseDragged(mouseEvent -> {
            primaryStage.setX(mouseEvent.getScreenX() - x);
            primaryStage.setY(mouseEvent.getScreenY() - y);
        });
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void navigateTo(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }


    public static void showError(String header, String message) {
        TrayNotification notification = new TrayNotification();
        notification.setTitle(header);
        notification.setMessage(message);
        notification.setNotificationType(NotificationType.ERROR);
        notification.setAnimationType(AnimationType.POPUP);
        notification.showAndDismiss(Duration.seconds(3));
    }

    public static void showSuccessMessage(String header, String message) {
        TrayNotification notification = new TrayNotification();
        notification.setTitle(header);
        notification.setMessage(message);
        notification.setNotificationType(NotificationType.SUCCESS);
        notification.setAnimationType(AnimationType.POPUP);
        notification.showAndDismiss(Duration.seconds(3));
    }

    public static void showInfoMessage(String header, String message) {
        TrayNotification notification = new TrayNotification();
        notification.setTitle(header);
        notification.setMessage(message);
        notification.setNotificationType(NotificationType.INFORMATION);
        notification.setAnimationType(AnimationType.POPUP);
        notification.showAndDismiss(Duration.seconds(3));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("FXMLS/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static FXMLLoader loaderFactory(String fxml) {
        return new FXMLLoader(App.class.getResource("FXMLS/" + fxml + ".fxml"));
    }

    public static void close(){
        Stage primary = (Stage) scene.getWindow();
        primary.close();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
