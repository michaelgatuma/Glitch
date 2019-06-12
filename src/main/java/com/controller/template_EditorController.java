package com.controller;

import com.data.dataManager;
import com.jfoenix.controls.*;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class template_EditorController {

    @FXML
    private AnchorPane motherPane;

    @FXML
    private JFXTextArea txtTemplate;

    @FXML
    private TextArea txtFlowTemplate;

    @FXML
    private JFXComboBox cmbBoxField;

    @FXML
    private HBox hBoxTags;

    @FXML
    private JFXRadioButton rdbtnQuick;

    @FXML
    private JFXButton btnAdd;

    @FXML
    private JFXButton btnCancel;

    @FXML
    private JFXButton btnManageTemplates;
    @FXML
    private StackPane stackPane;

    JFXSnackbar toast = new JFXSnackbar(stackPane);

    @FXML
    void onbtnAdd_ActionPerformed(ActionEvent event) {
        upsert();
    }

    @FXML
    void onbtnCancel_ActionPerformed(ActionEvent event) {
        motherPane.getScene().getWindow().hide();
    }

    @FXML
    void onbtnManageTemplates_ActionPerformed(ActionEvent event) throws IOException {
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/templatesScreen.fxml"));
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

    @FXML
    void initialize() {
        fetchSkills();
        txtTemplate.textProperty().addListener(textChanged -> {
            previewTemplate();
        });


        ObservableList<Node> tags = hBoxTags.getChildren();
        for (Node tag : tags)
            tag.setOnMouseReleased(e -> {
                addTag(((JFXButton) tag).getText());
            });
    }

    void fetchSkills() {
        String sql = "SELECT DISTINCT Field FROM Proposal";
        try (Connection conn = dataManager.getSQLiteConnection()) {
            PreparedStatement pst = conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                cmbBoxField.getItems().add(rs.getString("Field"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String processPreview(String txt) {
        txt = txt.replace("[name]", "Kelvin Mwangi");
        txt = txt.replace("[topic]", "American History Report");
        txt = txt.replace("[rate]", "$23");
        // txt.replace("[Hours]", "40 Hrs/Week");
        return txt;
    }

    void previewTemplate() {

        txtFlowTemplate.setText(processPreview(txtTemplate.getText()));
    }

    void upsert() {
        String sql = "SELECT * FROM Proposal WHERE Field =? AND Length=?";
        if (cmbBoxField.getSelectionModel().isEmpty()) {
            cmbBoxField.setBackground(new Background(new BackgroundFill(Color.RED, new CornerRadii(0), Insets.EMPTY)));
        } else {
            try (Connection conn = dataManager.getSQLiteConnection()) {
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setString(1, cmbBoxField.getSelectionModel().getSelectedItem().toString());
                pst.setString(2, rdbtnQuick.isSelected() ? "1" : "2");
                ResultSet rs = pst.executeQuery();
                if (rs.next())
                    updateTemplate();
                else
                    insertTemplate();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void updateTemplate() {
        cmbBoxField.setBackground(new Background(new BackgroundFill(Color.color(0, 0, 0, 0), new CornerRadii(0), Insets.EMPTY)));
        String sql = "UPDATE Proposal SET Description=? WHERE Field=?, Length=?";
        try (Connection conn = dataManager.getSQLiteConnection()) {
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, txtTemplate.getText());
            pst.setString(2, cmbBoxField.getSelectionModel().getSelectedItem().toString());
            pst.setString(3, rdbtnQuick.isSelected() ? "1" : "2");
            if (pst.execute()) {
                //fixme: Snackbar not showing
                toast.getStyleClass().addAll("jfx-snackbar-content");
                toast.show("Template Added", 3);
            }
            //todo: Show Snack bar

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    void insertTemplate() {

        cmbBoxField.setStyle("-jfx-background-color: transparent;");
        String sql = "INSERT INTO Proposal (Description, Section, Field, Length) VALUES (?,?,?,?)";
        try (Connection conn = dataManager.getSQLiteConnection()) {
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, txtTemplate.getText());
            pst.setString(3, cmbBoxField.getSelectionModel().getSelectedItem().toString());
            pst.setString(4, rdbtnQuick.isSelected() ? "1" : "2");
            if (pst.execute()) {
                //fixme: Snackbar not showing
                toast.getStyleClass().addAll("jfx-snackbar-content-error");
                toast.show("Template not Added!", 3);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    void addTag(String tagName) {
        int p1 = txtTemplate.getCaretPosition();
        String text = txtTemplate.getText();
        text = text + " [" + tagName + "]";
        txtTemplate.setText(text);
        txtTemplate.positionCaret(p1 + tagName.length() + 3);
        previewTemplate();
    }
}
