module bujo {
  requires javafx.controls;
  requires javafx.fxml;
  requires com.fasterxml.jackson.annotation;
  requires com.fasterxml.jackson.core;
  requires com.fasterxml.jackson.databind;
  requires org.controlsfx.controls;
  requires javafx.media;
  requires java.desktop;
  requires java.base;

  opens bujo to javafx.fxml, javafx.media;
  exports bujo;
  exports bujo.controller;
  exports bujo.model;
  exports bujo.view;
  opens bujo.controller to javafx.fxml;
  opens bujo.model to com.fasterxml.jackson.databind;
  opens bujo.controller.screenctrls to javafx.fxml;

}