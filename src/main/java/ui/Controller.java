package ui;

import dataStorage.*;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import simplex.SimplexMethod;

import java.util.ArrayList;
import java.util.Objects;


public class Controller {
    public String ARTIFICIAL_BASIS = "искусственный";
    public String MANUAL_BASIS = "готовый";

    private SimplexTable currentSimplexTable;

    public TextField numVars;          // количество переменных
    public TextField numRestrictions;  // количество ограничений
    public ComboBox<String> comboTask;         // тип задачи (min, max)
    public TabPane tabPane;            // панель вкладок
    public Tab simplexTab;             // вкладка симплекс метода
    public Tab artificialBasisTab;     // вкладка искусственного базиса
    public Button simplexButton;       // кнопка перехода к симплекс методу
    public Button artificialBasisButton; // кнопка перехода к ИБ
    public ComboBox<String> comboMode;         // выбор режима (авто, пошаговый)
    public ComboBox<String> comboFrac;         // выбор дробей (обычные, десятичные)
    public ComboBox<String> comboBasis;        // выбор базиса (искусственный, готовый)
    public VBox vBoxBasisVars;         // контейнер для чекбокса базиса

    public HBox taskTypeHBox;
    public HBox fracTypeHBox;
    public HBox modeHBox;
    public HBox basisHBox;
    public GridPane gridPaneFunctionSM;
    public GridPane gridPaneMatrixSM;
    public GridPane xSolutionSMGridPane;
    public GridPane ySolutionSMGridPane;
    public VBox buttonsSMVBox;


    @FXML
    private GridPane gridPaneMatrix;

    @FXML
    private GridPane gridPaneFunction;


    @FXML
    private void handleCreateFields() {
        // обработчик ввода количества ограничений и количества переменных
        try {
            int rows = Integer.parseInt(numRestrictions.getText());
            int columns = Integer.parseInt(numVars.getText());

            String basisType = comboBasis.getValue();

            if (Objects.equals(basisType, ARTIFICIAL_BASIS)) {
                vBoxBasisVars.getChildren().clear();

                simplexButton.setDisable(true);
                artificialBasisButton.setDisable(false);
            }
            else if (Objects.equals(basisType, MANUAL_BASIS)) {
                vBoxBasisVars.getChildren().clear();

                artificialBasisButton.setDisable(true);
                simplexButton.setDisable(false);

                Label basisLabel = new Label("Выберите базис");
                vBoxBasisVars.getChildren().add(basisLabel);
                for (int i = 1; i <= columns; i++) {
                    CheckBox checkBox = new CheckBox("X%d".formatted(i));
                    checkBox.setUserData(i);
                    vBoxBasisVars.getChildren().add(checkBox);
                }
            }

            if (rows > 16 || columns > 16 || rows < 1 || columns < 1) {
                ErrorMessage.showError("Некорректные данные", "Введенные параметры не подходят для создания задачи.");
            }


            createMatrixFields(rows, columns);
            createFunctionFields(columns);
        } catch (NumberFormatException e) {
            ErrorMessage.showError("Некорректные данные", "Ошибка при чтении числа: " + e);
        }
    }


    private void createMatrixFields(int rows, int columns) {
        // Очищаем старые поля
        gridPaneMatrix.getChildren().clear();
        // Создаем нужное количество TextField
        for (int row = 0; row < rows; row++) {
            // Добавляем Label "f(x)" в первую ячейку на первой строке
            Label fxLabel = new Label("f%d(x) ".formatted(row + 1));
            fxLabel.setPrefWidth(40);
            fxLabel.setPrefHeight(20);
            gridPaneMatrix.add(fxLabel, 0, row); // Помещаем "f(x)" в ячейку (0, 0)
            for (int col = 1; col <= columns; col++) {
                TextField textField = new TextField();
                textField.setAlignment(Pos.CENTER);

                textField.setPrefWidth(60); // Максимальная ширина
                textField.setPrefHeight(20); // Максимальная высота

                // Добавляем поля в GridPane, задаем их позицию
                gridPaneMatrix.add(textField, col, row);
                GridPane.setMargin(textField, new Insets(2.5, 2.5, 2.5, 2.5)); // отступы слева и справа
            }

            TextField textField = new TextField();
            textField.setAlignment(Pos.CENTER);

            textField.setPrefWidth(60); // Максимальная ширина
            textField.setPrefHeight(20); // Максимальная высота

            gridPaneMatrix.add(textField, columns + 1, row);
            GridPane.setMargin(textField, new Insets(2.5, 5, 2.5, 10)); // отступы слева и справа
        }
    }


    private void createFunctionFields(int columns) {
        gridPaneFunction.getChildren().clear();

        // Добавляем Label "f(x)" в первую ячейку на первой строке
        Label fxLabel = new Label("f(x) ");
        fxLabel.setPrefWidth(40);
        fxLabel.setPrefHeight(20);
        gridPaneFunction.add(fxLabel, 0, 1); // Помещаем "f(x)" в ячейку (0, 0)

        // Цикл для добавления полей и меток "Xi"
        for (int col = 1; col <= columns; col++) {
            // Добавляем метку "Xi"
            Label xiLabel = new Label("X" + col);
            xiLabel.setPrefWidth(60);
            xiLabel.setPrefHeight(20);

            xiLabel.setAlignment(Pos.CENTER);
            gridPaneFunction.add(xiLabel, col, 0); // Помещаем метку "Xi" в строку 0, колонку col
            GridPane.setMargin(xiLabel, new Insets(2.5, 2.5, 2.5, 2.5)); // отступы слева и справа


            // Добавляем текстовое поле для ввода значений
            TextField textField = new TextField();
            textField.setAlignment(Pos.CENTER);
            textField.setPrefWidth(60); // Предпочтительная ширина
            textField.setPrefHeight(20); // Предпочтительная высота
            // Добавляем текстовое поле в строку 1, колонку col
            gridPaneFunction.add(textField, col, 1);
            GridPane.setMargin(textField, new Insets(2.5, 2.5, 2.5, 2.5)); // отступы слева и справа
        }

        // Добавляем метку "Xi"
        Label xiLabel = new Label("b");
        xiLabel.setPrefWidth(60);
        xiLabel.setPrefHeight(20);
        xiLabel.setAlignment(Pos.CENTER);
        gridPaneFunction.add(xiLabel, columns + 1, 0); // Помещаем метку "Xi" в строку 0, колонку col
        GridPane.setMargin(xiLabel, new Insets(2.5, 5, 2.5, 10)); // отступы слева и справа


        // Добавляем текстовое поле для ввода значений
        TextField textField = new TextField();
        textField.setAlignment(Pos.CENTER);
        textField.setText("0");

        textField.setPrefWidth(60); // Предпочтительная ширина
        textField.setPrefHeight(20); // Предпочтительная высота

        textField.setEditable(false);
        // Добавляем текстовое поле в строку 1, колонку col
        gridPaneFunction.add(textField, columns + 1, 1);
        GridPane.setMargin(textField, new Insets(2.5, 5, 2.5, 10)); // отступы слева и справа
    }

    private void createFunctionFieldsSM(ArrayList<Integer> freeVars, ArrayList<Fraction> function) {
        // заполнение коэффициентов функции
        gridPaneFunctionSM.getChildren().clear();

        // Добавляем Label "f(x)" в первую ячейку на первой строке
        Label fxLabel = new Label("f(x) ");
        fxLabel.setPrefWidth(40);
        fxLabel.setPrefHeight(20);
        gridPaneFunctionSM.add(fxLabel, 0, 1);

        // Цикл для добавления полей и меток "Xi"
        for (int col : freeVars) {
            int i = freeVars.indexOf(col);
            // Добавляем метку "Xi"
            Label xiLabel = new Label("X" + col);
            xiLabel.setPrefWidth(60);
            xiLabel.setPrefHeight(20);

            xiLabel.setAlignment(Pos.CENTER);
            gridPaneFunctionSM.add(xiLabel, i + 1, 0); // Помещаем метку "Xi" в строку 0, колонку col
            GridPane.setMargin(xiLabel, new Insets(2.5, 2.5, 2.5, 2.5)); // отступы слева и справа


            // Добавляем текстовое поле для ввода значений
            TextField textField = new TextField();
            textField.setText(function.get(i).toString());
            textField.setEditable(false);
            textField.setAlignment(Pos.CENTER);
            textField.setPrefWidth(60); // Предпочтительная ширина
            textField.setPrefHeight(20); // Предпочтительная высота
            // Добавляем текстовое поле в строку 1, колонку col
            gridPaneFunctionSM.add(textField, i + 1, 1);
            GridPane.setMargin(textField, new Insets(2.5, 2.5, 2.5, 2.5)); // отступы слева и справа
        }

        // Добавляем метку "b"
        Label xiLabel = new Label("b");
        xiLabel.setPrefWidth(60);
        xiLabel.setPrefHeight(20);
        xiLabel.setAlignment(Pos.CENTER);
        gridPaneFunctionSM.add(xiLabel, freeVars.size() + 1, 0); // Помещаем метку "Xi" в строку 0, колонку col
        GridPane.setMargin(xiLabel, new Insets(2.5, 5, 2.5, 10)); // отступы слева и справа


        // Добавляем текстовое поле для ввода значений
        TextField textField = new TextField();
        textField.setAlignment(Pos.CENTER);
        textField.setText(function.getLast().toString());

        textField.setPrefWidth(60); // Предпочтительная ширина
        textField.setPrefHeight(20); // Предпочтительная высота

        textField.setEditable(false);
        // Добавляем текстовое поле в строку 1, колонку col
        gridPaneFunctionSM.add(textField, freeVars.size() + 1, 1);
        GridPane.setMargin(textField, new Insets(2.5, 5, 2.5, 10)); // отступы слева и справа
    }

    private void createMatrixFieldsSM(Fraction[][] mtx, ArrayList<Integer> base) {
        // Очищаем старые поля
        gridPaneMatrixSM.getChildren().clear();

        int rows = mtx.length;
        int columns = mtx[0].length;

        // Создаем нужное количество TextField
        for (int row = 0; row < rows; row++) {

            Label fxLabel = new Label("X%d ".formatted(base.get(row)));

            fxLabel.setPrefWidth(40);
            fxLabel.setPrefHeight(20);

            gridPaneMatrixSM.add(fxLabel, 0, row);
            for (int col = 1; col < columns; col++) {
                TextField textField = new TextField();
                textField.setAlignment(Pos.CENTER);

                textField.setText(mtx[row][col-1].toString());
                textField.setEditable(false);


                textField.setPrefWidth(60); // Максимальная ширина
                textField.setPrefHeight(20); // Максимальная высота

                // Добавляем поля в GridPane, задаем их позицию
                gridPaneMatrixSM.add(textField, col, row);
                GridPane.setMargin(textField, new Insets(2.5, 2.5, 2.5, 2.5)); // отступы слева и справа
            }

            TextField textField = new TextField();
            textField.setAlignment(Pos.CENTER);

            textField.setText(mtx[row][columns - 1].toString());
            textField.setEditable(false);

            textField.setPrefWidth(60); // Максимальная ширина
            textField.setPrefHeight(20); // Максимальная высота

            gridPaneMatrixSM.add(textField, columns, row);
            GridPane.setMargin(textField, new Insets(2.5, 5, 2.5, 10)); // отступы слева и справа
        }
    }

    private void createSolutionFieldsSM(Solution solution) {
        xSolutionSMGridPane.getChildren().clear();
        ySolutionSMGridPane.getChildren().clear();

        // заполнение полей для вектора х*

        // добавляем Label "Х*" в первую ячейку на первой строке
        Label xLabel = new Label("X* = ");
        xLabel.setPrefWidth(70);
        xLabel.setPrefHeight(20);
        xLabel.setAlignment(Pos.CENTER);
        xSolutionSMGridPane.add(xLabel, 0, 1);

        for (int i = 1; i <= solution.getVectorSolution().size(); i++) {
            // Добавляем метку "Xi"
            Label xiLabel = new Label("X" + i);
            xiLabel.setPrefWidth(60);
            xiLabel.setPrefHeight(20);

            xiLabel.setAlignment(Pos.CENTER);
            xSolutionSMGridPane.add(xiLabel, i + 1, 0); // Помещаем метку "Xi" в строку 0, колонку col
            GridPane.setMargin(xiLabel, new Insets(2.5, 2.5, 2.5, 2.5)); // отступы слева и справа

            // Добавляем текстовое поле для ввода значений
            TextField textField = new TextField();
            textField.setText(solution.getVectorSolution().get(i - 1).toString());
            textField.setEditable(false);
            textField.setAlignment(Pos.CENTER);
            textField.setPrefWidth(60); // Предпочтительная ширина
            textField.setPrefHeight(20); // Предпочтительная высота
            // Добавляем текстовое поле в строку 1, колонку col
            xSolutionSMGridPane.add(textField, i + 1, 1);
            GridPane.setMargin(textField, new Insets(2.5, 2.5, 2.5, 2.5)); // отступы слева и справа
        }

        // заполнение поля для значения функции
        // добавляем Label "Х*" в первую ячейку на первой строке
        Label yLabel = new Label("f(x)%s = ".formatted(currentSimplexTable.getTaskType()));
        yLabel.setPrefWidth(70);
        yLabel.setPrefHeight(20);
        ySolutionSMGridPane.add(yLabel, 0, 0);

        TextField textField = new TextField();
        textField.setText(solution.getFunctionValue().toString());
        textField.setEditable(false);
        textField.setAlignment(Pos.CENTER);
        textField.setPrefWidth(60); // Предпочтительная ширина
        textField.setPrefHeight(20); // Предпочтительная высота
        ySolutionSMGridPane.add(textField, 1, 0);
        GridPane.setMargin(textField, new Insets(2.5, 2.5, 2.5, 2.5)); // отступы слева и справа

    }

    // Обработчик для кнопки "Симплекс метод"
    @FXML
    private void handleSimplexMethod() {
        // Переходим на вкладку "симплекс метод"
        tabPane.getSelectionModel().select(simplexTab);

        int rows = Integer.parseInt(numRestrictions.getText());
        int columns = Integer.parseInt(numVars.getText());


        // Получаем данные матрицы
        Fraction[][] matrixData = getMatrixData(rows, columns + 1);
        ArrayList<Fraction> function = getFunctionData(columns);
        ArrayList<Integer> base = getCheckBoxData();
        String taskType = comboTask.getValue();
        String fracType = Objects.equals(comboFrac.getValue(), "обычные") ? "ordinary" : "decimal";
        String mode = Objects.equals(comboMode.getValue(), "авто") ? "auto" : "manual";

        Task task = new Task(
                function,
                new Matrix(matrixData),
                false,
                base,
                taskType,
                fracType,
                mode
        );

        currentSimplexTable = SimplexMethod.gauss(task);

        if (currentSimplexTable.getErrorMassage() != null) {
            ErrorMessage.showError("Некорректные данные", currentSimplexTable.getErrorMassage());
        } else {
            updateSimplexTab();
        }
    }


    private void updateSimplexTab() {
        // метод для добавления данных во вкладку симплекс метода

        // добавление данных задачи
        taskTypeHBox.getChildren().clear();
        fracTypeHBox.getChildren().clear();
        modeHBox.getChildren().clear();
        buttonsSMVBox.getChildren().clear();

        taskTypeHBox.getChildren().add(new Label("Тип задачи - " + currentSimplexTable.getTaskType()));
        fracTypeHBox.getChildren().add(new Label("Тип дробей - " + currentSimplexTable.getFracType()));
        modeHBox.getChildren().add(new Label("Режим - " + currentSimplexTable.getMode()));

        if (Objects.equals(currentSimplexTable.getMode(), "auto")) {
            Button autoModeButton = new Button("Решить");
            autoModeButton.setOnAction(actionEvent -> getSolutionSM()); // назначем действие
            buttonsSMVBox.getChildren().add(autoModeButton);
        } else {
            Button stepUp = new Button("Шаг вперед");
            Button stepBack = new Button("Шаг назад");
            buttonsSMVBox.getChildren().addAll(stepUp, stepBack);
        }

        // заполнение функции
        createFunctionFieldsSM(currentSimplexTable.getFreeVars(), currentSimplexTable.getFunction());

        // заполнение матрицы
        createMatrixFieldsSM(currentSimplexTable.getMatrix(), currentSimplexTable.getBase());

    }

    // Обработчик для кнопки "Метод искусственного базиса"
    @FXML
    private void handleArtificialBasisMethod() {
        // Переходим на вкладку "метод искусственного базиса"
        tabPane.getSelectionModel().select(artificialBasisTab);
        int rows = Integer.parseInt(numRestrictions.getText());
        int columns = Integer.parseInt(numVars.getText());


        // Получаем данные матрицы
        Fraction[][] matrixData = getMatrixData(rows, columns + 1);
        ArrayList<Fraction> function = getFunctionData(columns);
        String taskType = comboTask.getValue();
        String fracType = Objects.equals(comboFrac.getValue(), "обычные") ? "ordinary" : "decimal";
        String mode = Objects.equals(comboMode.getValue(), "авто") ? "auto" : "manual";

        Task task = new Task(
                function,
                new Matrix(matrixData),
                false,
                taskType,
                fracType,
                mode
        );

        ErrorMessage.showInfo("считанная задача", task.toString());
    }

    // Метод для получения данных из матрицы
    private Fraction[][] getMatrixData(int rows, int columns) {
        Fraction[][] matrixData = new Fraction[rows][columns];

        // Пробегаем по всем ячейкам матрицы и считываем данные
        boolean errorFlag = false;
        for (int row = 0; row < rows; row++) {
            for (int col = 1; col <= columns; col++) {  // Пропускаем первый столбец с Label "f(x)"
                TextField textField = (TextField) getNodeFromGridPane(row, col, "matrix");
                try {
                    // Считываем данные и сохраняем их в матрице
                    matrixData[row][col - 1] = Fraction.parseFraction(textField.getText());
                } catch (NumberFormatException e) {
                    // Если не удалось преобразовать в число, выводим ошибку
                    ErrorMessage.showError("Ошибка ввода", "Невозможно преобразовать введенные данные в число.");
                    errorFlag = true;
                    break;
                }
            }
            if (errorFlag) break;
        }

        return matrixData;
    }


    // метод для получения коэффициентов функции
    private ArrayList<Fraction> getFunctionData(int columns) {
        ArrayList<Fraction> functionData = new ArrayList<>();

        for (int col = 1; col <= columns; col++) {
            TextField textField = (TextField) getNodeFromGridPane(1, col, "function");
            try {
                functionData.add(Fraction.parseFraction(textField.getText()));
            } catch (NumberFormatException e) {
                ErrorMessage.showError("Ошибка ввода", "Невозможно преобразовать введенные данные в число.");
                break;
            }
        }
        return functionData;
    }


    private ArrayList<Integer> getCheckBoxData() {
        ArrayList<Integer> basisNums = new ArrayList<>();

        for (Node node : vBoxBasisVars.getChildren()) {
            if (node instanceof CheckBox checkBox) {
                boolean isSelected = checkBox.isSelected();
                Object userData = checkBox.getUserData(); // Получаем скрытое значение

                if (isSelected) {
                    basisNums.add(Integer.parseInt(userData.toString()));
                }
            }
        }
        return basisNums;
    }


    // Метод для получения узла по его позиции в GridPane
    private Node getNodeFromGridPane(int row, int col, String type) {
        if (Objects.equals(type, "matrix")) {
            for (Node node : gridPaneMatrix.getChildren()) {
                if (GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == col) {
                    return node;
                }
            }
        } else if (Objects.equals(type, "function")) {
            for (Node node : gridPaneFunction.getChildren()) {
                if (GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == col) {
                    return node;
                }
            }
        }
        return null;
    }

    private void getSolutionSM() {
        // обработка действий при нажатии на кнопку "решить"
        buttonsSMVBox.getChildren().getFirst().setDisable(true);
        Solution solution = SimplexMethod.autoMode(currentSimplexTable);

        if (solution.getErrorMessage() != null) {
            ErrorMessage.showError("Ошибка", solution.getErrorMessage());
        } else {
            createSolutionFieldsSM(solution);
//            ErrorMessage.showInfo("Решение!", solution.toString());
        }
    }
}
