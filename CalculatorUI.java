import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
        import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
        import javafx.stage.Stage;

public class CalculatorUI extends Application {

    private TextField display = new TextField();
    private String currentInput = "";
    private double firstOperand = 0;
    private String operator = "";
    private boolean startNewInput = true;

    @Override
    public void start(Stage primaryStage) {
        display.setEditable(false);
        display.setAlignment(Pos.CENTER_RIGHT);
        display.setStyle("-fx-font-size: 20px;");
        display.setPrefHeight(50);

        GridPane grid = createButtonGrid();
        VBox root = new VBox(10, display, grid);
        root.setPadding(new Insets(15));
        root.setStyle("-fx-background-color: #f0f0f0;");

        Scene scene = new Scene(root, 300, 400);
        scene.setOnKeyPressed(e -> handleKeyPress(e.getCode()));

        primaryStage.setTitle("Simple Calculator");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private GridPane createButtonGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        String[][] layout = {
                {"7", "8", "9", "/"},
                {"4", "5", "6", "*"},
                {"1", "2", "3", "-"},
                {"0", ".", "=", "+"},
                {"C"}
        };

        for (int row = 0; row < layout.length; row++) {
            for (int col = 0; col < layout[row].length; col++) {
                String label = layout[row][col];
                Button btn = new Button(label);
                btn.setPrefSize(60, 60);
                btn.setStyle("-fx-font-size: 18px;");
                btn.setTooltip(new Tooltip("Click or use keyboard"));
                btn.setOnAction(e -> handleButton(label));
                grid.add(btn, col, row);
            }
        }

        return grid;
    }

    private void handleButton(String label) {
        switch (label) {
            case "C" -> clear();
            case "=" -> calculate();
            case "+", "-", "*", "/" -> setOperator(label);
            default -> appendInput(label);
        }
    }

    private void handleKeyPress(KeyCode code) {
        if (code.isDigitKey()) {
            appendInput(code.getName());
        } else if (code == KeyCode.ADD || code == KeyCode.PLUS) {
            setOperator("+");
        } else if (code == KeyCode.SUBTRACT || code == KeyCode.MINUS) {
            setOperator("-");
        } else if (code == KeyCode.MULTIPLY) {
            setOperator("*");
        } else if (code == KeyCode.DIVIDE || code == KeyCode.SLASH) {
            setOperator("/");
        } else if (code == KeyCode.ENTER || code == KeyCode.EQUALS) {
            calculate();
        } else if (code == KeyCode.BACK_SPACE) {
            backspace();
        } else if (code == KeyCode.C) {
            clear();
        } else if (code == KeyCode.PERIOD || code == KeyCode.DECIMAL) {
            appendInput(".");
        }
    }

    private void appendInput(String value) {
        if (startNewInput) {
            currentInput = "";
            startNewInput = false;
        }
        currentInput += value;
        display.setText(currentInput);
    }

    private void setOperator(String op) {
        try {
            firstOperand = Double.parseDouble(currentInput);
            operator = op;
            startNewInput = true;
        } catch (NumberFormatException e) {
            display.setText("Invalid Input");
        }
    }

    private void calculate() {
        try {
            double secondOperand = Double.parseDouble(currentInput);
            double result = switch (operator) {
                case "+" -> firstOperand + secondOperand;
                case "-" -> firstOperand - secondOperand;
                case "*" -> firstOperand * secondOperand;
                case "/" -> {
                    if (secondOperand == 0) throw new ArithmeticException("Divide by zero");
                    yield firstOperand / secondOperand;
                }
                default -> secondOperand;
            };
            display.setText(String.valueOf(result));
            currentInput = String.valueOf(result);
            startNewInput = true;
        } catch (NumberFormatException e) {
            display.setText("Error");
        } catch (ArithmeticException e) {
            display.setText("Cannot divide by zero");
        }
    }

    private void clear() {
        currentInput = "";
        firstOperand = 0;
        operator = "";
        display.setText("");
        startNewInput = true;
    }

    private void backspace() {
        if (!currentInput.isEmpty()) {
            currentInput = currentInput.substring(0, currentInput.length() - 1);
            display.setText(currentInput);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

