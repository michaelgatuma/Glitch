package com.controller;

import com.jfoenix.controls.*;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.*;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

public class settingsController implements Initializable {


    @FXML
    private AnchorPane motherPane;

    @FXML
    private JFXButton btn_Close;

    @FXML
    private FontAwesomeIconView iconClose_FontAwesomeIcon;

    @FXML
    private Label lbl_Github;

    @FXML
    private MaterialDesignIconView iconGithub_MaterialDesIcon;

    @FXML
    private Label lbl_Feedback;

    @FXML
    private Label lbl_Settings;

    @FXML
    private JFXProgressBar jfxProgress;

    @FXML
    private Label lblPercent;

    @FXML
    private JFXButton btnProfile;

    @FXML
    private JFXButton btnEditTemplate;

    @FXML
    private Label lblGithub;
    @FXML
    private StackPane stackPane;

    @FXML
    void onBtnCloseOptions_ActionPerformed(ActionEvent event) throws IOException {
        AnchorPane pane = FXMLLoader.load(getClass().getResource("/fxml/homeScreen.fxml"));
        stackPane.getChildren().setAll(pane);
    }

    @FXML
    void onBtnEditTemplate_ActionPerformed(ActionEvent event) throws IOException {
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/templateEditorScreen.fxml"));
        Scene scene = new Scene(root);
        stage.initStyle(StageStyle.UNIFIED);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.centerOnScreen();
        stage.setScene(scene);
//        stage.setWidth(432);
        //      stage.setHeight(219);
        stage.setAlwaysOnTop(true);
        stage.show();
    }

    JFXTextField txtName = new JFXTextField();
    JFXButton btnSave = new JFXButton("Save");
    VBox container = new VBox();
    JFXDialog dialog;

    void nameDialogConstructor() {
        try {
            File p = new File("configuration/butler/user.properties");
            p.createNewFile();
            FileInputStream input = new FileInputStream(p);
            Properties prop = new Properties();
            prop.load(input);
            String name = prop.getProperty("profile");
            if (name != null) {
                txtName.setText(name);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        btnSave.setOnAction(e -> {
            try {
                File p = new File("configuration/butler/user.properties");
                p.createNewFile();
                OutputStream output = new FileOutputStream(p);
                Properties prop = new Properties();
                prop.setProperty("profile", txtName.getText());
                prop.store(output, null);
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            dialog.close();
        });
        txtName.setPromptText("Michael Gates");
        JFXDialogLayout content = new JFXDialogLayout();
        content.setHeading(new Text("Enter your Name"));
        txtName.setAlignment(Pos.CENTER);
        container.getChildren().add(txtName);
        btnSave.setPrefWidth(350);
        btnSave.setStyle("-fx-background-color:teal;-fx-text-fill:white;-fx-background-radius:2px;-fx-padding:5px;");
        container.getChildren().add(btnSave);
        container.setSpacing(10);
        container.setAlignment(Pos.CENTER);
        content.setBody(container);
        dialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER, true);
    }

    @FXML
    void onBtnProfile_ActionPerformed(ActionEvent event) {
        dialog.show();

    }

    @FXML
    void onlblFeedback_MouseReleased(MouseEvent event) {

    }

    @FXML
    void onLblGithub_MouseReleased(MouseEvent event) {

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        nameDialogConstructor();
    }

}