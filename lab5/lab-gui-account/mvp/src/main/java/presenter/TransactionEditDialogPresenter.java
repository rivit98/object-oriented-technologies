package presenter;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.converter.LocalDateStringConverter;
import model.Category;
import model.Transaction;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.format.DateTimeFormatter;

public class TransactionEditDialogPresenter {

    private Transaction transaction;

    @FXML
    private TextField dateTextField;

    @FXML
    private TextField payeeTextField;

    @FXML
    private TextField categoryTextField;

    @FXML
    private TextField inflowTextField;

    private Stage dialogStage;

    private boolean approved;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setData(Transaction transaction) {
        this.transaction = transaction;
        updateControls();
    }

    public boolean isApproved() {
        return approved;
    }

    @FXML
    private void handleOkAction(ActionEvent event) throws ParseException {
        updateModel();
        approved = true;
        dialogStage.close();
    }

    @FXML
    private void handleCancelAction(ActionEvent event) {
        dialogStage.close();
    }

    private void updateModel() throws ParseException {
        transaction.setCategory(new Category(categoryTextField.textProperty().getValue()));

        transaction.setPayee(payeeTextField.textProperty().getValue());

        DecimalFormat decimalFormatter = new DecimalFormat();
        decimalFormatter.setParseBigDecimal(true);
        transaction.setInflow((BigDecimal) decimalFormatter.parse(inflowTextField.getText()));

        String pattern = "yyyy-MM-dd";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        LocalDateStringConverter converter = new LocalDateStringConverter(formatter, formatter);
        transaction.setDate(converter.fromString(dateTextField.getText()));
    }

    private void updateControls() {
        String pattern = "yyyy-MM-dd";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        LocalDateStringConverter converter = new LocalDateStringConverter(formatter, formatter);
        dateTextField.textProperty().setValue(converter.toString(transaction.getDate()));

        var category = transaction.getCategory();
        categoryTextField.textProperty().setValue(category.getName());

        var inflow = transaction.getInflow();
        inflowTextField.textProperty().setValue(inflow.toEngineeringString());

        var payee = transaction.getPayee();
        payeeTextField.textProperty().setValue(payee);
    }
}
