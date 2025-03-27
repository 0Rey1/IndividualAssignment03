package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import databasePart1.DatabaseHelper;
import java.sql.SQLException;
import java.util.List;

public class AnswerManagementPage {
    private TableView<Answer> tableView;
    private ObservableList<Answer> answerData;
    
    public AnswerManagementPage() {
    }
    
    public void show(Stage primaryStage, DatabaseHelper databaseHelper, User currentUser, Question question) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        
        tableView = new TableView<>();
        tableView.setPrefHeight(300);
        
        TableColumn<Answer, Number> idCol = new TableColumn<>("Answer ID");
        idCol.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getAnswerID()));
        
        TableColumn<Answer, Number> questionIdCol = new TableColumn<>("Question ID");
        questionIdCol.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getQuestionID()));
        
        TableColumn<Answer, String> answerTextCol = new TableColumn<>("Answer Text");
        answerTextCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getAnswerText()));
        
        TableColumn<Answer, String> authorCol = new TableColumn<>("Author");
        authorCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getAuthor()));
        
        TableColumn<Answer, String> acceptedCol = new TableColumn<>("Accepted");
        acceptedCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().isAccepted() ? "Yes" : "No"));
        
        tableView.getColumns().addAll(idCol, questionIdCol, answerTextCol, authorCol, acceptedCol);
        answerData = FXCollections.observableArrayList(databaseHelper.getAnswers(question.getQuestionID()));
        tableView.setItems(answerData);
        
        TextField questionIdField = new TextField();
        questionIdField.setPromptText("Question ID");
        
        TextArea answerTextArea = new TextArea();
        answerTextArea.setPromptText("Answer Text");
        
        HBox formBox = new HBox(10,
            new Label("Answer:"), answerTextArea
        );
        
        HBox questionBox = new HBox(10, 
        	new Label ("Title: " + question.getTitle()), 
        	new Label ("Description: " + question.getDescription()),
        	new Label ("Author: " + question.getAuthor())
        );
        
        Button addButton = new Button("Add Answer");
        Button updateButton = new Button("Update Selected");
        Button deleteButton = new Button("Delete Selected");
        Button acceptButton = new Button("Accept Selected");
        
        HBox buttonBox = new HBox(10, addButton, updateButton, deleteButton, acceptButton);
      
        addButton.setOnAction(e -> {
            int qId = question.getQuestionID();
            String answerText = answerTextArea.getText();
            String author = currentUser.getUserName();
            
            if (answerText.trim().isEmpty()) {
                showAlert("Error", "Answer text cannot be empty.");
                return;
            }
            Answer newAnswer = new Answer(qId, answerText, author);
            databaseHelper.addAnswer(newAnswer);
            clearFields(questionIdField, answerTextArea);
            answerData = FXCollections.observableArrayList(databaseHelper.getAnswers(question.getQuestionID()));
            tableView.setItems(answerData);
            tableView.refresh();
        });
        
        updateButton.setOnAction(e -> {
            Answer selected = tableView.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("Error", "No answer selected.");
                return;
            }
            
            if (!selected.getAuthor().equals(currentUser.getUserName())) {
            	showAlert("Error", "Answer is not yours to edit.");
            	return;
            }
            
            String newAnswerText = answerTextArea.getText();
            if (newAnswerText.trim().isEmpty()) {
                showAlert("Error", "Answer text cannot be empty.");
                return;
            }
            
            selected.setAnswerText(newAnswerText);
                
            if (databaseHelper.updateAnswer(selected)) {
            	tableView.refresh();    
            	clearFields(questionIdField, answerTextArea);
            } else {
            	showAlert("Error", "Failed to update answer.");    
            }
        });
        
        deleteButton.setOnAction(e -> {
            Answer selected = tableView.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("Error", "No answer selected.");
                return;
            }
            
            if (!currentUser.getUserName().equals(question.getAuthor()) || !currentUser.getUserName().equals(selected.getAuthor())) {
            	showAlert("Error", "Not your question to edit.");
            	return;
            }
            
            if (databaseHelper.deleteAnswer(selected.getAnswerID())) {
            	answerData.remove(selected);
            } else {
            	showAlert("Error", "Failed to delete answer.");
            }
            
        });
        
        acceptButton.setOnAction(e -> {
            Answer selected = tableView.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("Error", "No answer selected.");
                return;
            }
            
            if (!question.getAuthor().equals(currentUser.getUserName())) {
            	showAlert("Error", "Not your question to edit.");
            	return;
            }
            
            if (databaseHelper.acceptAnswer(selected.getAnswerID())) {
            	selected.setAccepted(true);
            	tableView.refresh();
            } else {
            	showAlert("Error", "Failed to accept answer.");
            }
        });
        
        Button backButton = new Button("Back to Student Menu");
        backButton.setOnAction(e -> {
            new StudentHomePage(databaseHelper, currentUser).show(primaryStage);
        });
        
        root.getChildren().addAll(questionBox, formBox, buttonBox, tableView, backButton);
        
        Scene scene = new Scene(root, 900, 500);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Answer Management");
        primaryStage.show();
    }
    
    private void clearFields(TextField questionIdField, TextArea answerTextArea) {
        questionIdField.clear();
        answerTextArea.clear();
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
