package bujo.controller.screenctrls;

import bujo.controller.AppUtils;
import bujo.controller.Lockbox;
import bujo.model.DayOfWeek;
import bujo.model.Task;
import bujo.view.ScreenView;

import java.awt.event.KeyEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import static bujo.controller.AppUtils.raisePopup;

/**
 * Represents a controller for the TaskScreen
 */
public class TaskScreenController implements ScreenController {
  @FXML
  private TextField taskNameEditField;
  @FXML
  private Label taskNameViewLabel;
  @FXML
  private SplitMenuButton taskDayMenu;
  @FXML
  private Label taskDayViewLabel;
  @FXML
  private TextArea taskDescriptionField;
  @FXML
  private TextFlow taskDescriptionView;
  @FXML
  private ScrollPane taskDescriptionScrollPane;
  @FXML
  private Button taskEditButton;
  @FXML
  private Button taskDeleteButton;
  @FXML
  private Button taskSaveButton;
  private final Stage primaryStage;
  private Lockbox<Task> taskLockbox;
  private boolean isInViewMode;

  /**
   * Constructor for TaskScreenController
   *
   * @param primaryStage stage on which the task screen is to be displayed
   * @param taskLockbox lockbox for transferring this task into the main controller
   *                    Should be null if creating a new task, populated if viewing and editing.
   */
  public TaskScreenController(Stage primaryStage, Lockbox<Task> taskLockbox) {
    this.taskLockbox = taskLockbox;
    this.primaryStage = primaryStage;
  }

  /**
   * Sets the scene and initializes the controls to respond to events
   */
  @Override
  public void run() {
    primaryStage.setTitle("New Task");
    primaryStage.setScene(
        new ScreenView(this, "taskScreen.fxml").load());
    primaryStage.getScene().setOnKeyPressed(e -> handleKeyPress(e.getCode()));
    primaryStage.getIcons().add(new Image("file:src/main/resources/media/icon.png"));
    primaryStage.show();

    setInitWindowMode();
    toggleVisibilityForMode();
    initFields();
    initButtons();
  }

  /**
   * Handles key press events
   *
   * @param code the key code of the shortcut
   */
  private void handleKeyPress(KeyCode code) {
    int keyCode = code.getCode();
    switch (keyCode) {
      case KeyEvent.VK_S -> handleSave();
      case KeyEvent.VK_D, KeyEvent.VK_Q -> handleDelete();
      default -> {
        // Ignore
      }
    }
  }

  /**
   * Sets the initial window mode
   * If an empty lockbox is given, then this is a creation mode, otherwise - view mode
   */
  private void setInitWindowMode() {
    try {
      this.taskLockbox.getItemInLockbox();
      isInViewMode = true;
    } catch (IllegalStateException ignored) {
      isInViewMode = false;
    }
  }

  /**
   * Toggles the necessary controls and fields visible for the current mode
   */
  private void toggleVisibilityForMode() {
    // Visible when in View Mode
    taskEditButton.setVisible(isInViewMode);
    taskNameViewLabel.setVisible(isInViewMode);
    taskDayViewLabel.setVisible(isInViewMode);
    taskDescriptionScrollPane.setVisible(isInViewMode);

    // Visible when in edit/create mode
    taskNameEditField.setVisible(!isInViewMode);
    taskDayMenu.setVisible(!isInViewMode);
    taskDescriptionField.setVisible(!isInViewMode);
  }

  /**
   * Populate the fields with data from the passed-in task, if applicable
   */
  private void initFields() {
    try {
      Task task = this.taskLockbox.getItemInLockbox();
      String dayString = task.getDay().toString().toLowerCase();
      dayString = dayString.substring(0, 1).toUpperCase()
          + dayString.substring(1).toLowerCase();

      if (isInViewMode) {
        taskNameViewLabel.setText(task.getName());
        taskDayViewLabel.setText(dayString);
        if (task.getDescription().isEmpty()) {
          Text text = new Text(" ---");
          text.setFont(Font.font("Niramit Regular", 15));
          taskDescriptionView.getChildren().add(text);
        } else {
          AppUtils.highlightLinksInTextFlow(taskDescriptionView, task.getDescription());
        }
      } else {
        this.taskNameEditField.setText(task.getName());
        this.taskDayMenu.setText(dayString);
        this.taskDescriptionField.setText(task.getDescription());
      }
    } catch (IllegalStateException ignored) {
      // Ignore
    }
  }

  /**
   * Set button on-action event handlers
   */
  private void initButtons() {
    this.taskSaveButton.setOnAction(e -> handleSave());
    this.taskDeleteButton.setOnAction(e -> handleDelete());
    this.taskEditButton.setOnAction(e -> handleGoToEditMode());

    for (MenuItem item : this.taskDayMenu.getItems()) {
      item.setOnAction(e -> handleDayMenu(item));
    }
  }

  /**
   * Handler for "save" option
   */
  private void handleSave() {
    if (isInViewMode) {
      this.primaryStage.close();
      return;
    }

    // Now, checking edit mode input validity
    // Create a new Task with the specified fields
    DayOfWeek dayEnum;
    try {
      dayEnum = DayOfWeek.valueOf(this.taskDayMenu.getText().toUpperCase());
    } catch (Exception e) {
      raisePopup("Error - Invalid Input", "Please select a day of the week.");
      return;
    }

    if (this.taskNameEditField.getText().equals("")) {
      AppUtils.raisePopup("Error - Invalid Input", "Please enter a task name.");
      return;
    }

    Task saveTask = new Task(
            this.taskNameEditField.getText(),
            this.taskDescriptionField.getText(),
            dayEnum);

    // Set the lockbox to the new task if the input is valid
    this.taskLockbox.putItemInLockbox(saveTask);
    this.primaryStage.hide();
  }

  /**
   * Handler for "delete" option
   */
  private void handleDelete() {
    this.taskLockbox.putItemInLockbox(null);
    this.primaryStage.hide();
  }

  /**
   * Handler for changing from view mode into edit mode
   */
  private void handleGoToEditMode() {
    this.isInViewMode = false;

    toggleVisibilityForMode();
    initFields();
  }

  /**
   * Handles a given MenuItem to set a value in the SplitMenu
   *
   * @param item MenuItem to be set
   */
  private void handleDayMenu(MenuItem item) {
    this.taskDayMenu.setText(item.getText());
  }
}
