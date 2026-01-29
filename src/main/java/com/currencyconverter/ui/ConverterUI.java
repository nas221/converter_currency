package com.currencyconverter.ui;

import com.currencyconverter.service.ExchangeRateService;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class ConverterUI extends Application {

    private ExchangeRateService service;
    private TextField amountField;
    private ComboBox<String> fromCurrency;
    private ComboBox<String> toCurrency;
    private Label resultLabel;
    private ProgressIndicator loadingIndicator; // Moved here

    @Override
    public void start(Stage primaryStage) {
        service = new ExchangeRateService();

        // Title
        Label title = new Label("Currency Converter");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // Amount Section
        amountField = new TextField();
        amountField.setPromptText("Enter amount");
        amountField.setMaxWidth(200);

        // Currency selection in an HBox for better UI
        fromCurrency = new ComboBox<>();
        fromCurrency.getItems().addAll("USD", "EUR", "GBP", "JPY", "INR", "AUD");
        fromCurrency.setValue("USD");

        toCurrency = new ComboBox<>();
        toCurrency.getItems().addAll("USD", "EUR", "GBP", "JPY", "INR", "AUD");
        toCurrency.setValue("EUR");

        HBox currencyBox = new HBox(10, fromCurrency, new Label("➔"), toCurrency);
        currencyBox.setAlignment(Pos.CENTER);

        // Convert button
        Button convertButton = new Button("Convert");
        convertButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px;");
        convertButton.setPrefWidth(150);
        convertButton.setOnAction(e -> performConversion());

        // Progress Indicator
        loadingIndicator = new ProgressIndicator();
        loadingIndicator.setVisible(false);
        loadingIndicator.setMaxSize(40, 40);

        // Result label
        resultLabel = new Label("");
        resultLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");

        // Main Layout
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(
                title,
                new Label("Amount:"), amountField,
                currencyBox,
                convertButton,
                loadingIndicator,
                resultLabel
        );

        Scene scene = new Scene(layout, 400, 500);
        primaryStage.setTitle("Currency Converter");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void performConversion() {
        String input = amountField.getText();
        if (input.isEmpty()) return;

        loadingIndicator.setVisible(true);
        resultLabel.setText("");

        try {
            double amount = Double.parseDouble(input);
            String from = fromCurrency.getValue();
            String to = toCurrency.getValue();

            // Note: If service.convert performs a network call,
            // you should ideally wrap this in a Task/Thread to avoid freezing the UI.
            double result = service.convert(amount, from, to);

            resultLabel.setText(String.format("%.2f %s = %.2f %s", amount, from, result, to));
        } catch (NumberFormatException e) {
            resultLabel.setText("Please enter a valid number");
        } catch (Exception e) {
            resultLabel.setText("Error: " + e.getMessage());
        } finally {
            loadingIndicator.setVisible(false);
        }
    }
}