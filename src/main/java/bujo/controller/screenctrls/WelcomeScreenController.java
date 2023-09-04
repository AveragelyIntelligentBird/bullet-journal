package bujo.controller.screenctrls;

import static bujo.controller.AppUtils.getUserChosenFile;
import static bujo.controller.AppUtils.handleTogglePlayer;
import static bujo.controller.AppUtils.setupSplashScreenTimeline;

import bujo.controller.Lockbox;
import bujo.controller.StorageManager;
import bujo.model.Week;
import bujo.view.ScreenView;

import java.awt.event.KeyEvent;
import java.nio.file.Path;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

/**
 * Controller for the welcome screen
 */
public class WelcomeScreenController implements ScreenController {
  @FXML
  private Button createNewWeekButton;
  @FXML
  private Button openWeekButton;
  private final Stage primaryStage;
  private final Lockbox<Week> weekLockbox;
  private final MediaPlayer player;

  /**
   * Constructor for WelcomeScreenController
   *
   * @param primaryStage stage on which the welcome screen is to be displayed
   * @param weekLockbox lockbox for transferring entry point week into the main controller
   * @param player the media player
   */
  public WelcomeScreenController(Stage primaryStage, Lockbox<Week> weekLockbox,
                                 MediaPlayer player) {
    this.primaryStage = primaryStage;
    this.weekLockbox = weekLockbox;
    this.player = player;
  }

  /**
   * Runs the welcome screen by deploying a flash screen
   */
  public void run() {
    primaryStage.setTitle("Bullet Journal");
    primaryStage.getIcons().add(new Image("file:src/main/resources/media/icon.png"));

    primaryStage.setScene(new ScreenView(this, "splashScreen.fxml").load());
    setupSplashScreenTimeline(primaryStage).setOnFinished(e -> runWelcomeScreen());
    primaryStage.show();
  }

  /**
   * Sets the scene and initializes the controls to respond to events
   */
  private void runWelcomeScreen() {
    primaryStage.setScene(
        new ScreenView(this, "welcomeScreen.fxml").load());
    primaryStage.getScene().setOnKeyPressed(e -> handleKeyPress(e.getCode()));
    primaryStage.show();

    initButtons();
  }

  /**
   * Initializes button actions
   */
  private void initButtons() {
    this.createNewWeekButton.setOnAction(e -> handleNewWeek());
    this.openWeekButton.setOnAction(e -> handleOpenWeek());
  }

  /**
   * Handler for "new week" option
   * Opens a week init screen pop up and extracts a valid week object
   */
  private void handleNewWeek() {
    Stage popUpStage = new Stage();
    Lockbox<Week> newWeekLockbox = new Lockbox<>();
    ScreenController weekInitScreenController =
        new WeekInitScreenController(popUpStage, newWeekLockbox);
    weekInitScreenController.run();

    popUpStage.setOnHiding(e -> {
      try {
        weekLockbox.putItemInLockbox(newWeekLockbox.getItemInLockbox());
        primaryStage.hide();
      } catch (IllegalStateException ignored) {
        // Ignore
      }
    });
  }

  /**
   * Handler for "open week" option
   * Opens a file opener to specify the desired .bujo file
   */
  private void handleOpenWeek() {
    Path weekPath = getUserChosenFile(true, primaryStage);
    if (weekPath != null) {
      Week week = StorageManager.readFile(weekPath);
      week.setPath(weekPath);

      if (!week.getHashedPassword().isEmpty()) {
        Stage popUpStage = new Stage();
        popUpStage.getIcons().add(new Image("file:src/main/resources/media/icon.png"));

        Lockbox<Week> passwordAuthLockbox = new Lockbox<>();
        passwordAuthLockbox.putItemInLockbox(week);
        ScreenController checkPassController
            = new CheckPasswordScreenController(popUpStage, passwordAuthLockbox);
        checkPassController.run();

        popUpStage.setOnHiding(e -> {
          try {
            weekLockbox.putItemInLockbox(passwordAuthLockbox.getItemInLockbox());
            primaryStage.hide();
          } catch (IllegalStateException ignored) {
            // Ignore
          }
        });
      } else {
        weekLockbox.putItemInLockbox(week);
        primaryStage.hide();
      }
    }
  }

  /**
   * Handles key press events
   *
   * @param code the key code of the shortcut
   */
  private void handleKeyPress(KeyCode code) {
    int keyCode = code.getCode();
    switch (keyCode) {
      case KeyEvent.VK_N -> handleNewWeek();
      case KeyEvent.VK_O -> handleOpenWeek();
      case KeyEvent.VK_M -> handleTogglePlayer(player);
      case KeyEvent.VK_Q -> primaryStage.hide();
      default -> {
        // Ignore
      }
    }
  }
}