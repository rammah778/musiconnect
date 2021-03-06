package Home.Controllers;

import Home.App;
import Home.Helper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import static Home.Helper.execute;
import static Home.Helper.executeQuery;

public class selectedUser implements Initializable {
    @FXML
    Text displayName;
    @FXML
    Circle profilePicture;
    @FXML
    Text userName;
    @FXML
    Circle userPicture;
    @FXML
    ListView<String> popListView;
    @FXML
    ListView<String> jazzListView;
    @FXML
    ListView<String> classicListView;
    @FXML
    ChoiceBox<String> navigator;
    @FXML
    TextField selectedUser;
    int user_id;
    ObservableList<String> songs = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Helper.getUserData(displayName, profilePicture);
        Helper.populateUserNavigator(navigator, "userAlbums");

    }


    @FXML
    private void onPopRowClickAction() throws IOException, SQLException {
        String song = popListView.getSelectionModel().getSelectedItem();
        if (song != null) {
            App.setSongID(getSongID(song));
            App.navigateTo("selectedSong");
        }
    }

    @FXML
    private void onJazzRowClickAction() throws IOException, SQLException {
        String song = jazzListView.getSelectionModel().getSelectedItem();
        if (song != null) {
            App.setSongID(getSongID(song));
            App.navigateTo("selectedSong");
        }
    }

    @FXML
    private void onClassicRowClickAction() throws IOException, SQLException {
        String song = classicListView.getSelectionModel().getSelectedItem();
        if (song != null) {
            App.setSongID(getSongID(song));
            App.navigateTo("selectedSong");
        }
    }

    @FXML
    private void viewSong() throws IOException, SQLException {
//         if(songs.contains(selectedSong.getText())){
//             App.setSongID(getSongID(selectedSong.getText()));
//             App.navigateTo("selectedSong");
//         }
    }

    @FXML
    private void addSong() throws SQLException {
//        String albumName = this.albumName.getText();
//         int user_id = getUserID(App.getUserEmail());
//         int song_id= getSongID(selectedSong.getText());
//         int song_category = getCategory(albumName);
//         int playListID = getPlayList(user_id,song_category);
//        String idQuery = "SELECT * FROM playlist_song WHERE playlist_id = ?";
//
//        PreparedStatement idStatement = App.connection.prepareStatement(idQuery);
//        idStatement.setInt(1, playListID);
//        ResultSet idResult = executeQuery(idStatement);
//        if (idResult.next()) {
//            int result_id = idResult.getInt("song_id");
//            if(result_id==song_id)
//            {
//                App.showError("This song is already added","You already have this song in your playlist");
//                return;
//            }
//        }
//        String query = "INSERT INTO playlist_song (playlist_id, song_id) VALUES (?, ?)";
//        PreparedStatement statement = App.connection.prepareStatement(query);
//        statement.setInt(1, playListID);
//        statement.setInt(2, song_id);
//        execute(statement);
//        App.showSuccessMessage("Song added","The song has now been added to your playlist");
    }


    @FXML
    public void instantiate(int userID) {
        user_id = userID;
        String query = "SELECT  * FROM app_user WHERE id =? ORDER BY name ";
        try (PreparedStatement statement = App.connection.prepareStatement(query)) {
            statement.setInt(1, userID);
            ResultSet resultSet = executeQuery(statement);
            resultSet.next();
            String name = resultSet.getString("name");
            userName.setText(name);
            try {
                File imageFile = new File(resultSet.getString("picture"));
                String imageLocation = imageFile.toURI().toString();
                Image pic = new Image(imageLocation, false);
                userPicture.setFill(new ImagePattern(pic));
            } catch (Exception ignored) {
            }
            populatePopChart();
            populateClassicChart();
            populateJazzChart();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private int getUserID(String email) throws SQLException {
        String query = "SELECT * FROM app_user WHERE email = ?";
        PreparedStatement statement = App.connection.prepareStatement(query);
        statement.setString(1, email);
        ResultSet resultSet = executeQuery(statement);
        resultSet.next();
        return resultSet.getInt("id");
    }

    private int getSongID(String name) throws SQLException {
        String query = "SELECT * FROM song WHERE name = ?";
        PreparedStatement statement = App.connection.prepareStatement(query);
        statement.setString(1, name);
        ResultSet resultSet = executeQuery(statement);
        resultSet.next();
        return resultSet.getInt("id");
    }

    private int getCategory(String name) throws SQLException {
        String query = "SELECT * FROM album WHERE name = ?";
        PreparedStatement statement = App.connection.prepareStatement(query);
        statement.setString(1, name);
        ResultSet resultSet = executeQuery(statement);
        resultSet.next();
        return resultSet.getInt("category_id");
    }

    private int getPlayList(int userID, int category_id) throws SQLException {
        String query = "SELECT * FROM playlist WHERE user_id = ? AND category_id=?";
        PreparedStatement statement = App.connection.prepareStatement(query);
        statement.setInt(1, userID);
        statement.setInt(2, category_id);
        ResultSet resultSet = executeQuery(statement);
        resultSet.next();
        return resultSet.getInt("id");
    }


    private void populateClassicChart() throws SQLException {
        final ObservableList<String> data = FXCollections.observableArrayList();
        String idQuery = "SELECT * FROM follower_following WHERE follower_id=? AND following_id = ?";

        PreparedStatement idStatement = App.connection.prepareStatement(idQuery);
        idStatement.setInt(1, getUserID(App.getUserEmail()));
        idStatement.setInt(2, user_id);
        ResultSet idResult = executeQuery(idStatement);
        if (!idResult.next()) {
            return;
        }
        String query = """
                select * from song where id in
                (select song_id
                from playlist_song
                         join playlist p on p.id = playlist_song.playlist_id
                    where category_id = ? and user_id = ?)""";
        try (PreparedStatement statement = App.connection.prepareStatement(query)) {
            statement.setInt(1, 3);
            statement.setInt(2, user_id);
            ResultSet resultSet = executeQuery(statement);
            int i = 1;
            while (resultSet.next()) {
                int songID = resultSet.getInt("id");
                String name = resultSet.getString("name");
                data.add(name);
                i++;
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        classicListView.setItems(data);
    }

    private void populateJazzChart() throws SQLException {
        final ObservableList<String> data = FXCollections.observableArrayList();
        String idQuery = "SELECT * FROM follower_following WHERE follower_id=? AND following_id = ?";
        PreparedStatement idStatement = App.connection.prepareStatement(idQuery);
        idStatement.setInt(1, getUserID(App.getUserEmail()));
        idStatement.setInt(2, user_id);
        ResultSet idResult = executeQuery(idStatement);
        if (!idResult.next()) {
            return;
        }
        String query = """
                select * from song where id in
                (select song_id
                from playlist_song
                         join playlist p on p.id = playlist_song.playlist_id
                    where category_id = ? and user_id = ?)""";
        try (PreparedStatement statement = App.connection.prepareStatement(query)) {
            statement.setInt(1, 2);
            statement.setInt(2, user_id);
            ResultSet resultSet = executeQuery(statement);
            int i = 1;
            while (resultSet.next()) {
                int songID = resultSet.getInt("id");
                String name = resultSet.getString("name");
                data.add(name);
                i++;
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        jazzListView.setItems(data);
    }

    private void populatePopChart() throws SQLException {
        final ObservableList<String> data = FXCollections.observableArrayList();
        String idQuery = "SELECT * FROM follower_following WHERE follower_id=? AND following_id = ?";

        PreparedStatement idStatement = App.connection.prepareStatement(idQuery);
        idStatement.setInt(1, getUserID(App.getUserEmail()));
        idStatement.setInt(2, user_id);
        ResultSet idResult = executeQuery(idStatement);
        if (!idResult.next()) {
            App.showInfoMessage("Follow this user to view playlists", "");
            return;
        }
        String query = """
                select * from song where id in
                (select song_id
                from playlist_song
                         join playlist p on p.id = playlist_song.playlist_id
                    where category_id = ? and user_id = ?)""";
        try (PreparedStatement statement = App.connection.prepareStatement(query)) {
            statement.setInt(1, 1);
            statement.setInt(2, user_id);
            ResultSet resultSet = executeQuery(statement);
            int i = 1;
            while (resultSet.next()) {
                int songID = resultSet.getInt("id");
                String name = resultSet.getString("name");
                data.add(name);
                i++;
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        popListView.setItems(data);
    }


    @FXML
    private void navigate() {
        Helper.navigateUser(navigator);
    }

    @FXML
    private void logoutApp() throws IOException {
        App.navigateTo("login");
    }


    @FXML
    private void getPop() throws SQLException {
        int category_id = 1;
        playlistCopy(category_id);
    }

    @FXML
    private void getJazz() throws SQLException {
        int category_id = 2;
        playlistCopy(category_id);
    }

    private void playlistCopy(int category_id) throws SQLException {
        int follower_id = getUserID(App.getUserEmail());
        String playlistIdquery = "select id from playlist where user_id = ? and category_id = ?";
        PreparedStatement firstStatement = App.connection.prepareStatement(playlistIdquery);
        firstStatement.setInt(1, follower_id);
        firstStatement.setInt(2, category_id);
        ResultSet rs = executeQuery(firstStatement);
        rs.next();
        int playlistId = rs.getInt("id");

        String playlistMergeQuery = """
                INSERT INTO playlist_song (playlist_id, song_id)
                    (select ? , playlist_2.song_id
                     from (select song_id
                           from playlist_song
                                    join playlist p on p.id = playlist_song.playlist_id
                           where category_id = ?
                             and user_id = ?) as playlist_1
                              RIGHT JOIN (
                         select song_id
                         from playlist_song
                                  join playlist p on p.id = playlist_song.playlist_id
                         where category_id = ?
                           and user_id = ?) as playlist_2
                                         ON playlist_1.song_id = playlist_2.song_id
                     WHERE playlist_1.song_id is null)
                     """;
        PreparedStatement insertStatement = App.connection.prepareStatement(playlistMergeQuery);
        insertStatement.setInt(1, playlistId);
        insertStatement.setInt(2, category_id);
        insertStatement.setInt(3, follower_id);
        insertStatement.setInt(4, category_id);
        insertStatement.setInt(5, user_id);
        execute(insertStatement);
    }

    @FXML
    private void getClassic() throws SQLException {
        int category_id = 3;
        playlistCopy(category_id);
    }


    @FXML
    private void follow() throws SQLException {
        String idQuery = "SELECT * FROM follower_following WHERE follower_id= ? AND following_id = ?";

        PreparedStatement idStatement = App.connection.prepareStatement(idQuery);
        idStatement.setInt(1, getUserID(App.getUserEmail()));
        idStatement.setInt(2, user_id);
        ResultSet idResult = executeQuery(idStatement);
        if (idResult.next()) {
            App.showError("Already following this user, ", "");
            return;
        }
        String query = "INSERT INTO follower_following (follower_id, following_id) VALUES (?, ?)";
        PreparedStatement statement = App.connection.prepareStatement(query);
        statement.setInt(1, getUserID(App.getUserEmail()));
        statement.setInt(2, user_id);
        execute(statement);
        App.showSuccessMessage("Added to following", "");
        populateJazzChart();
        populateClassicChart();
        populatePopChart();
    }


    @FXML
    private void close() {
        App.close();
    }

}
