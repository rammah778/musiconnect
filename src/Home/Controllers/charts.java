package Home.Controllers;

import Home.App;
import Home.Helper;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class charts implements Initializable {
    @FXML
    Text displayName;
    @FXML
    Circle profilePicture;
    @FXML
    ListView<String> songsListView;
    @FXML
    ChoiceBox<String> navigator;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Helper.getUserData(displayName, profilePicture);
        Helper.populateUserNavigator(navigator,"Charts");
    }

    @FXML private  void navigate (){
        Helper.navigateUser(navigator);
    }

    @FXML
    private void logoutApp() throws IOException {
        App.navigateTo("login");
    }

    @FXML
    private void close() {
        App.close();
    }

    @FXML
    public void gotoWorldCharts() throws IOException {
        App.navigateTo("worldCharts");
    }

    @FXML
    public void gotoGenreCharts() throws IOException {
        App.navigateTo("genreCharts");
    }
}
