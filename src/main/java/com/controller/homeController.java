package com.controller;

import com.config.butler.FireStore;
import com.config.butler.License;
import com.config.butler.Local;
import com.config.butler.Mailer;
import com.data.dataManager;
import com.jfoenix.controls.*;
import com.model.skill;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.validator.EmailValidator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

import static de.jensd.fx.glyphs.GlyphsDude.createIcon;

/**
 * FXML Controller class
 *
 * @author Michael
 */
public class homeController implements Initializable {

    private final VBox vBoxMoreSkills = new VBox();
    private final ToggleGroup skillsGroup = new ToggleGroup();
    public ObservableList<skill> skillList = FXCollections.observableArrayList();
    JFXButton btnClose = new JFXButton();
    JFXButton btnSettings = new JFXButton();
    double X;
    PopupControl skillsPopup = new PopupControl();
    double popupX, popupY;
    boolean active = false;
    FireStore cloud = new FireStore();
    License license = new License();
    @FXML
    private StackPane stackPane;
    @FXML
    private AnchorPane motherPane;
    @FXML
    private JFXTextField txtBidRate;
    @FXML
    private JFXTextField txtProjectTopic;
    @FXML
    private JFXTextArea txtDescription;
    @FXML
    private VBox vBoxSkills;
    @FXML
    private JFXButton btnMoreSkills;
    @FXML
    private JFXButton btnRefresh;
    @FXML
    private JFXButton btnCloseOptions;
    @FXML
    private JFXButton btnSwap;
    @FXML
    private JFXButton btnMinimize;
    @FXML
    private Label lblFeedback;
    @FXML
    private JFXToggleButton tglbtnQuick;
    @FXML
    private Label lblGithub;
    @FXML
    private JFXProgressBar jfxProgress;
    private boolean isQuickProposal;
    private boolean isLocatedRight = true;

    private static void toClipboard(String text) {
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(text);
        clipboard.setContent(content);
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //new FireStore().init();

        JFXDialogLayout content = new JFXDialogLayout();
        JFXDialog dialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER, false);
        if (license.isPoisoned())
            System.exit(0);
        else if (license.readKey().length() != 500)
            CheckPoint();
        if (Local.isNetAvailable()) {

            String msg = cloud.upgrade(license.readKey());
            if (msg.contains("UPGRADE")) {
                content.setAlignment(Pos.CENTER);
                Text t = new Text("UPGRADE!");
                t.setTextAlignment(TextAlignment.CENTER);
                t.setFill(Color.WHITE);
                Text tb = new Text(msg);
                tb.setTextAlignment(TextAlignment.CENTER);
                tb.setFill(Color.WHITE);
                content.setHeading(t);
                content.setBody(tb);
                dialog.setStyle("-fx-background-color:#EF6C00;");
                VBox container = new VBox();
                container.getChildren().add(content);
                container.setAlignment(Pos.BOTTOM_CENTER);
                dialog.getChildren().add(container);
                dialog.show();
            } else if (cloud.isBlocked(license.readKey())) {
                new License().revokeLicense();
                System.exit(0);
            }
            if (!cloud.isValid(license.readKey()))
                CheckPoint();
        }
        floatingButtons();
        setIcons();
        getUserSkills();//Get Skills from database
        jfxProgress.setSecondaryProgress(0);
        skillList.forEach((Skill) -> {
            constructSkillRadioButton(Skill.getName());
        });
        txtBidRate.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                homeController.this.bidChanged();
            }
        });
        txtProjectTopic.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                homeController.this.topicChanged();
            }
        });
        txtDescription.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                homeController.this.descriptionChanged();
            }
        });
        skillsGroup.getToggles().stream().forEach(new Consumer<Toggle>() {
            @Override
            public void accept(Toggle field) {
                field.selectedProperty().addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                        homeController.this.fieldChanged(((RadioButton) field).getText());
                    }
                });
            }
        });
        tglbtnQuick.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                isQuickProposal = tglbtnQuick.isSelected();
                homeController.this.fieldChanged(((RadioButton) skillsGroup.getSelectedToggle()).getText());
            }
        });

    }

    private void CheckPoint() {
        final int timeout = 60000;
        Service<Void> service = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        //Background work

                        System.out.println("Running Background Stuff");

                        final CountDownLatch latch = new CountDownLatch(1);
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    //FX Stuff done here

                                    System.out.println("Start...");
                                    Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), ev -> {
                                        System.out.println("Run ping Task after 5 Seconds");
                                        if (Local.isNetAvailable()) {
                                            System.out.println("Net is Available");
                                            EmailValidator validator = EmailValidator.getInstance();

                                            JFXTextField txtUsr = new JFXTextField();
                                            JFXTextField txtEmail = new JFXTextField();
                                            JFXTextField txtCode = new JFXTextField();
                                            JFXButton btnActivate = new JFXButton("Verify Email");
                                            btnActivate.setStyle("-fx-background-color:gray;-fx-text-fill:white;-fx-font-weight:bold;");
                                            btnActivate.setDisable(true);
                                            txtUsr.textProperty().addListener(new ChangeListener<String>() {
                                                @Override
                                                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                                                    if (validator.isValid(txtEmail.getText()) && !txtUsr.getText().isEmpty())
                                                        btnActivate.setDisable(false);
                                                    else
                                                        btnActivate.setDisable(true);
                                                }
                                            });
                                            txtEmail.textProperty().addListener(new ChangeListener<String>() {
                                                @Override
                                                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                                                    if (validator.isValid(txtEmail.getText()) && !txtUsr.getText().isEmpty())
                                                        btnActivate.setDisable(false);
                                                    else
                                                        btnActivate.setDisable(true);
                                                }
                                            });
                                            txtCode.textProperty().addListener(new ChangeListener<String>() {
                                                @Override
                                                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                                                    if (!txtCode.getText().isEmpty())
                                                        btnActivate.setDisable(false);
                                                }
                                            });


                                            VBox container = new VBox();
                                            container.setSpacing(5);
                                            txtCode.setPromptText("Verification Code");
                                            txtUsr.setPromptText("How do they call you?");
                                            txtEmail.setPromptText("Enter Email");
                                            txtCode.setDisable(true);
                                            container.getChildren().addAll(txtUsr, txtEmail, txtCode, btnActivate);
                                            JFXDialogLayout content = new JFXDialogLayout();
                                            content.setHeading(new Text("Sorry for Disturbance."));
                                            content.setBody(container);
                                            JFXDialog dialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER, false);
                                            dialog.show();
                                            String code = RandomStringUtils.randomNumeric(6);
                                            btnActivate.setOnAction(e -> {
                                                if (btnActivate.getText() == "Verify Email") {
                                                    txtEmail.setDisable(true);
                                                    txtUsr.setDisable(true);
                                                    if (Mailer.send("glitchapp.miga@gmail.com", "Selvacito.", txtEmail.getText(), "Glitch Verification", "Use this Code: " + code)) {
                                                        btnActivate.setDisable(true);
                                                        txtCode.setDisable(false);
                                                        btnActivate.setText("Confirm");
                                                    }
                                                } else if (btnActivate.getText() == "Confirm") {
                                                    if (txtCode.getText().equals(code)) {
                                                        License lic = new License();
                                                        cloud.write(txtUsr.getText(), txtEmail.getText(), true, new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z").format(new Date(System.currentTimeMillis())), lic.writeKey(), "OK");
                                                        dialog.close();
                                                    } else {
                                                        System.out.println("Code: " + code);
                                                        System.out.println("Entered: " + txtCode.getText());
                                                        System.exit(0);
                                                    }
                                                }
                                            });
                                            cancel();
                                        }
                                    }));
                                    timeline.setDelay(Duration.seconds(5));
                                    timeline.play();

                                    System.out.println("Running FX Stuff Stuff");
                                } finally {
                                    latch.countDown();
                                }
                            }
                        });
                        latch.await();
                        //Keep with the background work
                        return null;
                    }
                };
            }
        };
        service.start();
    }

    private void floatingButtons() {
        double x = btnCloseOptions.getLayoutX();
        btnClose.getStyleClass().addAll("jfx-button", "Popup-close-button");
        btnClose.setButtonType(JFXButton.ButtonType.RAISED);
        btnSettings.getStyleClass().addAll("jfx-button", "Popup-button");
        btnSettings.setButtonType(JFXButton.ButtonType.RAISED);
        JFXNodesList nodeList = new JFXNodesList();
        nodeList.addAnimatedNode(btnCloseOptions);
        nodeList.addAnimatedNode(btnClose);
        nodeList.addAnimatedNode(btnSettings);
        nodeList.setSpacing(2);
        motherPane.getChildren().add(nodeList);
        nodeList.setLayoutX(x);
        btnClose.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.exit(0);
            }
        });
        btnSettings.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    homeController.this.loadSettings();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void loadSettings() throws IOException {
        //FIXME: Settings Scene Not Loading
        StackPane pane = FXMLLoader.load(getClass().getResource("/fxml/settingsScreen.fxml"));
        motherPane.getChildren().setAll(pane);
    }

    private void setIcons() {
        btnSwap.setGraphic(createIcon(MaterialDesignIcon.SWAP_HORIZONTAL, "20px"));
        btnMinimize.setGraphic(createIcon(MaterialDesignIcon.PIN, "20px"));
        // btnCloseOptions.setGraphic(createIcon(MaterialDesignIcon.POWER_SETTINGS, "20px"));
        //btnClose.setGraphic(createIcon(MaterialDesignIcon.POWER, "20px"));
        btnClose.setGraphic(createIcon(MaterialDesignIcon.POWER, "20px"));
        btnSettings.setGraphic(createIcon(MaterialDesignIcon.SETTINGS, "20px"));
    }

    private void bidChanged() {
        processProposal();
    }

    private void topicChanged() {
        processProposal();
    }

    private void descriptionChanged() {
        processProposal();
    }

    private void fieldChanged(String newField) {
        txtDescription.setText(getTemplate(newField));
        processProposal();
    }

    private String processProposal() {
        //TODO: Change the name variable to fetch from database
        String name;
        try {
            File p = new File("required/user.properties");
            p.createNewFile();
            FileInputStream input = new FileInputStream(p);
            Properties prop = new Properties();
            prop.load(input);
            name = "[SET YOUR NAME AT: Settings -> Profile Name]";
            if (prop.getProperty("profile") == null) {
                name = "[SET YOUR NAME AT: Settings -> Profile Name]";
            } else {
                name = prop.getProperty("profile");
            }
            String topic = txtProjectTopic.getText();
            String text = txtDescription.getText();
            text = text.replace("\\n", "\n");
            txtDescription.setText(text);
            text = text.replace("[name]", name);
            text = text.replace("[topic]", topic);
            text = text.replace("[rate]", txtBidRate.getText());
            //text.replace("[hours]",);
            toClipboard(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getTemplate(String field) {
        String sql = "Select * FROM Proposal Where Field=? and Length=?";
        try (Connection conn = dataManager.getSQLiteConnection(); PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, field);
            pst.setString(2, isQuickProposal ? "1" : "2");
            try (ResultSet rs = pst.executeQuery()) {
                return rs.getString(1);
            }
        } catch (SQLException e) {
            String n = "";
            return "Error: Unable to Load Template\nCaused By: " + e.getMessage();
        }

    }

    private void getUserSkills() {
        vBoxSkills.getChildren().clear();
        String sql = "SELECT Distinct Field FROM Proposal";
        try (Connection conn = dataManager.getSQLiteConnection()) {
            PreparedStatement pst = conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                skillList.add(new skill(0, rs.getString(1), rs.getString(1)));
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    private void constructSkillRadioButton(String skill) {
        //skill = skill.substring(0, 10);//Cut String if its Longer than 8 characters
        RadioButton btn = new RadioButton(skill);
        btn.setToggleGroup(skillsGroup);
        if (vBoxSkills.getChildren().size() < 4) {
            vBoxSkills.getChildren().add(btn);
        } else if (vBoxSkills.getChildren().size() == 4) {
            vBoxSkills.getChildren().add(btnMoreSkills);
            vBoxMoreSkills.getChildren().add(btn);
        } else {
            vBoxMoreSkills.getChildren().add(btn);
        }
    }

    @FXML
    private void ontxtBidRate_KeyReleased(KeyEvent event) {
    }

    @FXML
    private void ontxtProjectTopic_KeyReleased(KeyEvent event) {
    }

    @FXML
    private void ontxtDescription_KeyReleased(KeyEvent event) {
    }

    @FXML
    private void onbtnMoreSkills_ActionPerformed(ActionEvent event) {
        popupX = motherPane.getScene().getWindow().getX() + vBoxSkills.getLayoutX();
        popupY = motherPane.getScene().getWindow().getY() + vBoxSkills.getLayoutY();
        vBoxMoreSkills.setPrefSize(vBoxSkills.getWidth(), vBoxSkills.getHeight());
        skillsPopup.setOpacity(1);
        skillsPopup.setAutoHide(true);
        skillsPopup.setAutoFix(true);
        skillsPopup.setHideOnEscape(true);
        skillsPopup.getScene().setRoot(vBoxMoreSkills);
        vBoxSkills.setOpacity(0);
        skillsPopup.setOnHiding(evt -> vBoxSkills.setOpacity(1));
        skillsPopup.show(motherPane.getScene().getWindow(), popupX, popupY);

    }

    @FXML
    private void onbtnRefresh_ActionPerformed(ActionEvent event) {
    }

    @FXML
    private void onbtnCloseOptions_ActionPerformed(ActionEvent event) {
        active = !active;
        if (active)
            btnCloseOptions.setStyle("-fx-border-width:0px1px1px1px;-fx-border-color:teal;-fx-background-color:black;");
        else
            btnCloseOptions.setStyle("-fx-border-width:0px;-fx-border-color:transparent;-fx-background-color:black;");
    }


    @FXML
    private void onbtnSwap_ActionPerformed(ActionEvent event) {
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        if (motherPane.getScene().getWindow().getX() > 6) {
            motherPane.getScene().getWindow().setX(5);
        } else {
            motherPane.getScene().getWindow().setX(primaryScreenBounds.getMinX() + primaryScreenBounds.getWidth() - 432);
        }
    }

    @FXML
    private void onbtnMinimize_ActionPerformed(ActionEvent event) {
        ((Stage) (motherPane.getScene().getWindow())).setAlwaysOnTop(!((Stage) (motherPane.getScene().getWindow())).isAlwaysOnTop());
        if (((Stage) (motherPane.getScene().getWindow())).isAlwaysOnTop()) {
            btnMinimize.setGraphic(createIcon(MaterialDesignIcon.PIN, "20px"));
        } else {
            btnMinimize.setGraphic(createIcon(MaterialDesignIcon.PIN_OFF, "20px"));
        }
    }

    @FXML
    private void onlblFeedback_MouseReleased(MouseEvent event) {
        // TODO Handle Give Feedback MouseClick evt
    }

    @FXML
    private void ontglbtnQuick_ActionPerformed(ActionEvent event) {
        //Handled
    }

    @FXML
    private void onlblGithub_MouseReleased(MouseEvent event) {
        // TODO Handle Github MouseClick evt
    }

}
