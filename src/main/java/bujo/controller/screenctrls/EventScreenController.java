package bujo.controller.screenctrls;

import static bujo.controller.AppUtils.highlightLinksInTextFlow;
import static bujo.controller.AppUtils.raisePopup;

import bujo.controller.Lockbox;
import bujo.model.DayOfWeek;
import bujo.model.Event;
import bujo.model.ItemType;
import bujo.view.ScreenView;

import java.awt.event.KeyEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

/**
 * Represents a controller for the EventScreen
 */
public class EventScreenController implements ScreenController {
  @FXML
  private TextField eventNameField;
  @FXML
  private SplitMenuButton eventDayMenu;
  @FXML
  private TextField eventTimeHour;
  @FXML
  private TextField eventTimeMinutes;
  @FXML
  private TextField eventDurationHour;
  @FXML
  private TextField eventDurationMinutes;
  @FXML
  private TextArea eventDescriptionField;
  @FXML
  private Button eventDeleteButton;
  @FXML
  private Button eventSaveButton;
  @FXML
  private SplitMenuButton amPmSelector;
  @FXML
  private Label eventNameView;
  @FXML
  private Label eventDayView;
  @FXML
  private Label startTimeView;
  @FXML
  private Label eventDurationView;
  @FXML
  private HBox durationBox;
  @FXML
  private TextFlow eventDescriptionView;
  @FXML
  private Button eventEditButton;
  @FXML
  private Text eventTimeColon;

  private final Stage primaryStage;
  private Lockbox<Event> eventLockbox;
  private boolean isInViewMode;

  /**
   * Constructor for EventScreenController
   *
   * @param primaryStage stage on which the event screen is to be displayed
   * @param eventLockbox lockbox for transferring this event into the main controller
   */
  public EventScreenController(Stage primaryStage, Lockbox<Event> eventLockbox) {
    this.primaryStage = primaryStage;
    this.eventLockbox = eventLockbox;
  }

  /**
   * Sets the scene and initializes the controls to respond to events
   */
  @Override
  public void run() {
    primaryStage.setTitle("New Event");
    primaryStage.setScene(
        new ScreenView(this, "eventScreen.fxml").load());
    primaryStage.getScene().setOnKeyPressed(e -> handleKeyPress(e.getCode()));
    primaryStage.getIcons().add(new Image("file:src/main/resources/media/icon.png"));
    primaryStage.show();

    setInitWindowMode();
    toggleVisibilityForMode();
    initButtons();
    initFields();
  }

  /**
   * Initializes the buttons to respond to events
   */
  private void initButtons() {
    this.eventDeleteButton.setOnAction(e -> handleDelete());
    this.eventSaveButton.setOnAction(e -> handleSave());
    this.eventEditButton.setOnAction(e -> handleGoToEditMode());
    for (MenuItem item : this.eventDayMenu.getItems()) {
      item.setOnAction(e -> handleDaySelection(item));
    }
    for (MenuItem item : this.amPmSelector.getItems()) {
      item.setOnAction(e -> handleAmPmSelection(item));
    }
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
   * Determines if in view-only or edit mode.
   */
  private void setInitWindowMode() {
    try {
      eventLockbox.getItemInLockbox();
      isInViewMode = true;
    } catch (IllegalStateException ignored) {
      isInViewMode = false;
    }
  }

  /**
   * Toggles the visibility of the controls based on the mode
   */
  private void toggleVisibilityForMode() {
    // Visible when in view mode
    eventEditButton.setVisible(isInViewMode);
    eventNameView.setVisible(isInViewMode);
    eventDayView.setVisible(isInViewMode);
    startTimeView.setVisible(isInViewMode);
    eventDurationView.setVisible(isInViewMode);
    eventDescriptionView.setVisible(isInViewMode);
    eventDescriptionField.setDisable(isInViewMode);

    // Visible when in edit/create mode
    eventNameField.setVisible(!isInViewMode);
    eventDayMenu.setVisible(!isInViewMode);
    eventTimeHour.setVisible(!isInViewMode);
    eventTimeMinutes.setVisible(!isInViewMode);
    eventDurationHour.setVisible(!isInViewMode);
    amPmSelector.setVisible(!isInViewMode);
    eventDurationMinutes.setVisible(!isInViewMode);
    eventDescriptionField.setVisible(!isInViewMode);
    durationBox.setVisible(!isInViewMode);
    eventTimeColon.setVisible(!isInViewMode);
  }

  /**
   * Sets the text of the AM/PM selector to the selection
   *
   * @param item the selected item
   */
  private void handleAmPmSelection(MenuItem item) {
    this.amPmSelector.setText(item.getText());
  }


  /**
   * Sets the text of the day menu to the text of the selected item (makes it dynamic)
   *
   * @param item the selected item
   */
  private void handleDaySelection(MenuItem item) {
    this.eventDayMenu.setText(item.getText());
  }

  /**
   * Sets the text of the fields to the values of the event in the lockbox, if applicable
   */
  private void initFields() {
    try {
      Event event = this.eventLockbox.getItemInLockbox();
      this.eventNameField.setText(event.getName());
      if (isInViewMode) {
        this.eventNameView.setText(event.getName());
        String eventDay = event.getDay().toString();
        this.eventDayView.setText(eventDay.charAt(0)
            + eventDay.substring(1).toLowerCase());
        this.startTimeView.setText(event.getStartTime());
        this.eventDurationView.setText(event.getDuration());
        if (event.getDescription().isEmpty()) {
          Text text = new Text("---");
          text.setFont(Font.font("Niramit Regular", 15));
          this.eventDescriptionView.getChildren().add(text);
        } else {
          highlightLinksInTextFlow(eventDescriptionView, event.getDescription());
        }
      } else {
        String dayString = switch (event.getDay()) {
          case MONDAY -> "Monday";
          case TUESDAY -> "Tuesday";
          case WEDNESDAY -> "Wednesday";
          case THURSDAY -> "Thursday";
          case FRIDAY -> "Friday";
          case SATURDAY -> "Saturday";
          case SUNDAY -> "Sunday";
          default -> null;
        };
        this.eventDayMenu.setText(dayString);
        String time = event.getStartTime();
        int colonIndex = time.indexOf(":");
        String hour = (time.substring(0, colonIndex));
        String minutes = (time.substring(colonIndex + 1, colonIndex + 3));
        amPmSelector.setText(time.substring(colonIndex + 4));
        this.eventTimeHour.setText(hour);
        this.eventTimeMinutes.setText(minutes);
        String duration = event.getDuration();
        int[] parsedDuration = parseTime(duration);
        this.eventDurationHour.setText(Integer.toString(parsedDuration[0]));
        this.eventDurationMinutes.setText(Integer.toString(parsedDuration[1]));
        this.eventDescriptionField.setText(event.getDescription());
      }
    } catch (IllegalStateException ignored) {
      // Ignore
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
      case KeyEvent.VK_S -> handleSave();
      case KeyEvent.VK_D, KeyEvent.VK_Q -> handleDelete();
      default -> {
        // Ignore
      }
    }
  }

  /**
   * Handles the save button
   */
  private void handleSave() {
    if (isInViewMode) {
      this.primaryStage.close();
      return;
    }

    // Now, checking edit mode input validity
    // Create a new Event with the specified fields
    String eventName;
    if (!this.eventNameField.getText().equals("")) {
      eventName = this.eventNameField.getText();
    } else {
      raisePopup("Error - Invalid Input", "Please enter an event name.");
      return;
    }

    DayOfWeek dayEnum;
    try {
      dayEnum = DayOfWeek.valueOf(this.eventDayMenu.getText().toUpperCase());
    } catch (Exception e) {
      raisePopup("Error - Invalid Input", "Please select a day of the week.");
      return;
    }

    int startHour;
    int startMinute;
    try {
      startHour = Integer.parseInt(this.eventTimeHour.getText());
      startMinute = Integer.parseInt(this.eventTimeMinutes.getText());

      if ((startHour > 12 || startHour < 1) || (startMinute < 0 || startMinute > 59)) {
        raisePopup("Error - Invalid Input", "Please enter a valid start time.");
        return;
      }
    } catch (NumberFormatException e) {
      raisePopup("Error - Invalid Input", "Please enter a valid start time.");
      return;
    }

    String selectedAmPm;
    if (amPmSelector.getText().equals("AM") || amPmSelector.getText().equals("PM")) {
      selectedAmPm = amPmSelector.getText();
    } else {
      raisePopup("Error - Invalid Input",
              "Please select AM or PM for the start time.");
      return;
    }

    String formattedStartTime;
    if (startMinute >= 10) {
      formattedStartTime = startHour + ":" + startMinute + " " + selectedAmPm;
    } else {
      formattedStartTime = startHour + ":0" + startMinute + " " + selectedAmPm;
    }

    int durHour;
    int durMinute;
    try {
      if (this.eventDurationHour.getText().equals("")) {
        durHour = 0;
      } else {
        durHour = Integer.parseInt(this.eventDurationHour.getText());
      }

      if (this.eventDurationMinutes.getText().equals("")) {
        durMinute = 0;
      } else {
        durMinute = Integer.parseInt(this.eventDurationMinutes.getText());
      }

      if ((durHour == 0 && durMinute == 0) || (durHour < 0) || (durMinute < 0 || durMinute > 59)) {
        raisePopup("Error - Invalid Input", "Please enter a valid event duration.");
        return;
      }
    } catch (NumberFormatException e) {
      raisePopup("Error - Invalid Input", "Please enter a valid event duration.");
      return;
    }

    String formattedDuration = "";
    if (!(durHour == 0)) {
      formattedDuration += ((durHour % 10) == 1) ? (durHour + " hr ") : (durHour + " hrs ");
    }
    if (!(durMinute == 0)) {
      formattedDuration += ((durMinute % 10) == 1) ? (durMinute + " min") : (durMinute + " mins");
    }

    Event event = new Event(
            eventName,
            this.eventDescriptionField.getText(),
            dayEnum,
            formattedStartTime,
            formattedDuration,
            ItemType.EVENT);

    this.eventLockbox.putItemInLockbox(event);
    this.primaryStage.close();
  }

  /**
   * Handles the delete button
   */
  private void handleDelete() {
    this.eventLockbox.putItemInLockbox(null); // Set the lockbox to null if we want to delete
    this.primaryStage.hide();
  }

  /**
   * Parses a string of the form "Hr Min" into an array of ints of the form [Hr, Min]
   *
   * @param time string to be parsed
   * @return array of ints representing the hours and minutes
   */
  private int[] parseTime(String time) {
    int hours = 0;
    int minutes = 0;

    // Regular expression pattern for extracting hours and minutes
    String pattern = "(?:(\\d+) hr)?\\s*(?:(\\d+) mins)?";

    Matcher matcher = Pattern.compile(pattern).matcher(time);
    if (matcher.find()) {
      if (matcher.group(1) != null) {
        hours = Integer.parseInt(matcher.group(1));
      }
      if (matcher.group(2) != null) {
        minutes = Integer.parseInt(matcher.group(2));
      }
    }
    return new int[]{hours, minutes};
  }

}