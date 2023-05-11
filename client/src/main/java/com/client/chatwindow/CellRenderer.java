package com.client.chatwindow;

import com.client.util.Database;
import com.messages.User;
import javafx.geometry.Pos;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.io.IOException;

/**
 * A Class for Rendering users images / name on the userlist.
 */
class CellRenderer implements Callback<ListView<User>,ListCell<User>>{
        @Override
    public ListCell<User> call(ListView<User> p) {

        ListCell<User> cell = new ListCell<User>(){

            @Override
            protected void updateItem(User user, boolean bln) {
                super.updateItem(user, bln);
                setGraphic(null);
                setText(null);
                if (user != null) {
                    HBox hBox = new HBox();

                    Text name = new Text(user.getName());

                    ImageView statusImageView = new ImageView();
                    Image statusImage = new Image(getClass().getResource("/images/online.png").toString(), 16, 16,true,true);
                    statusImageView.setImage(statusImage);

                    ImageView pictureImageView = new ImageView();
                    pictureImageView.setFitHeight(32);
                    pictureImageView.setFitWidth(32);
                    Circle clip = new Circle(32 / 2, 32 / 2, 32 / 2);
                    pictureImageView.setClip(clip);

                    String email = Database.getEmailFromMongoDB(user.getName()); // Get the email of the person that sends a message
                    Image image = null;

                    try {
                        image = new Image(Database.getImageFromMongoDB(email));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    pictureImageView.setImage(image);

                    hBox.getChildren().addAll(statusImageView, pictureImageView, name);
                    hBox.setAlignment(Pos.CENTER_LEFT);

                    setGraphic(hBox);
                }
            }
        };
        return cell;
    }
}