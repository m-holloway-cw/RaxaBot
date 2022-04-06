/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.raxa.gui.handlers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.util.Callback;

public class ChatController implements Initializable {

    @FXML
    private Label label;

    public final static ObservableList<String> ob = FXCollections.observableArrayList();
    private String selected = "";

    @FXML
    private static ListView<String> chat;

    @FXML
    Button channel;

    @FXML
    private void goSettings(ActionEvent event) {

    }

    @FXML
    private void goWhispers(ActionEvent event) {

    }

    @FXML
    private void goChannel(ActionEvent event) {

    }

    @FXML
    private void goModTools(ActionEvent event) {

    }

    @FXML
    private void clearCache(ActionEvent event) {

    }

    @FXML
    private void sendMessage(ActionEvent event) {
        System.out.println("You clicked me!");
        label.setText("Hello World!");
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        chat.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                selected = chat.getSelectionModel().getSelectedItem();
                System.out.println("Selected message: " + selected);
            }
        });
        chat.setItems(ob);
        // Test values
       // ob.add("Connected to chat");
        /*getChat("fake message est message ea;;y ;long test emssage rea;;y"
                + ";long test emssage rea;;y ;long test emssage rea;;y"
                + ";long test emssage rea;;y ;long test emssage tesadsfjkasf1");*/
        chat.setCellFactory(cell
                -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (!isEmpty()) {
                    setText(item);
                    setWrapText(true);
                    setPrefWidth(10);
                } else {
                    setText(null);
                    setGraphic(null);
                }
            }
        });
    }

    @FXML
    private void testMessage(ActionEvent event) {
        getChat("fake message est message ea;;y ;long test emssage rea;;y"
                + ";long test emssage rea;;y ;long test emssage rea;;y"
                + ";long test emssage rea;;y ;long test emssage tesadsfjkasf1"
                + "end of long testing message."
        );
    }

    public static void getChat(String msg) {
        System.out.println(msg);
        Text text = new Text(msg);
        text.setWrappingWidth(430);
        ob.add(text.getText());
        //chat.setItems(ob);
    }
}
