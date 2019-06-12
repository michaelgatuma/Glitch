package com.controller;

import com.data.dataManager;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXSnackbar;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class templatesController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private JFXListView listviewTemplates;

    @FXML
    private TextArea textTemplate;

    @FXML
    private StackPane stackPane;

    JFXSnackbar toast = new JFXSnackbar(stackPane);

    @FXML
    void initialize() {
        listviewTemplates.setVerticalGap(16.0);
        listviewTemplates.setExpanded(true);
        listviewTemplates.depthProperty().set(1);
        listviewTemplates.expandedProperty();
        processList();
    }

    void processList() {
        String query = "SELECT * FROM Proposal";
        try (Connection conn = dataManager.getSQLiteConnection()) {
            PreparedStatement pst = conn.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                listviewTemplates.getItems().add(getTemplate(rs.getString("Field"), rs.getString("Length").equals("2") ? "Long Version" : "Short Version"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        listviewTemplates.setOnMouseClicked(e -> {
            Pane selectedPane = (Pane) listviewTemplates.getSelectionModel().getSelectedItem();
            String field = ((Label) selectedPane.getChildren().get(0)).getText();
            String length = ((Label) selectedPane.getChildren().get(1)).getText().contains("Long") ? "2" : "1";
            try (Connection conn = dataManager.getSQLiteConnection()) {
                PreparedStatement pst = conn.prepareStatement("SELECT * FROM Proposal WHERE Field=? AND Length=?");
                pst.setString(1, field);
                pst.setString(2, length);
                ResultSet rs = pst.executeQuery();
                if (rs.next())
                    textTemplate.setText(rs.getString("Description"));
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    Pane getTemplate(String a, String b) {
        Pane pane = new Pane();
        pane.setPrefSize(236, 42);
        Label title = new Label(a), desc = new Label(b);
        title.setStyle("-fx-font-size:16px;");
        desc.setStyle("-fx-font-size:12px;-fx-font-style:italic;");
        title.setPrefSize(100, 25);
        desc.setPrefSize(110, 25);
        pane.getChildren().addAll(title, desc);
        title.setLayoutX(6);
        title.setLayoutY(8);
        desc.setLayoutX(105);
        desc.setLayoutY(8);
        pane.setOnMouseReleased(e -> {
            Popup popup = new Popup();
            if (e.getButton().equals(MouseButton.SECONDARY)) {
                JFXButton btnDelete = new JFXButton("Delete");
                btnDelete.setStyle("-fx-background-color:white;");
                JFXButton btnEdit = new JFXButton("Edit");
                btnDelete.setOnAction(event -> {
                    if (delete(title.getText(), desc.getText().contains("Long") ? "2" : "1")) {
                        toast.getStyleClass().addAll("jfx-snackbar-content");
                        toast.show("Deleted", 3);
                        listviewTemplates.refresh();
                    }
                });
                btnEdit.setOnAction(event -> {
                    //todo: handle Editing action
                });
                VBox container = new VBox();
                //container.getChildren().addAll(btnDelete, btnEdit);
                container.getChildren().add(btnDelete);
                popup.getContent().add(container);
                popup.setX(e.getScreenX());
                popup.setY(e.getScreenY());
                popup.setAutoHide(true);
                popup.show(((Node) e.getSource()).getScene().getWindow());
            }

        });
        return pane;
    }

    /*
        Popup popupMenu(String p1, String p2) {
            Popup popup = new Popup();
            JFXButton btnDelete = new JFXButton("Delete");
            JFXButton btnEdit = new JFXButton("Edit");
            btnDelete.setOnAction(e -> {
                delete(p1, p2.contains("long") ? "2" : "1");
            });
            btnEdit.setOnAction(e -> {
                //todo: handle Editing action
                //todo: Delete this Commented Section
            });
            VBox container = new VBox();
            //container.getChildren().addAll(btnDelete, btnEdit);
            container.getChildren().add(btnDelete);
            popup.getContent().add(container);
            return popup;
        }
    */
    private boolean delete(String p1, String aLong) {
        String sql = "DELETE FROM Proposal WHERE Field=? AND Length=?";
        try (Connection conn = dataManager.getSQLiteConnection()) {
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, p1);
            pst.setString(2, aLong);
            return pst.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
