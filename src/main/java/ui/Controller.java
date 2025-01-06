package ui;

import artificialBasis.ArtificialBasisMethod;
import dataStorage.*;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import simplex.ReadTask;
import simplex.SimplexMethod;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;


public class Controller {
    public String MANUAL_BASIS = "готовый";

    public VBox buttonsABVBox;
    public GridPane gridPaneFunctionAB;
    public GridPane gridPaneMatrixAB;
    public GridPane basisABGridPane;
    public GridPane newFunctionABGridPane;
    public TextField taskTypeSM;
    public TextField fracTypeSM;
    public TextField modeSM;
    public TextField basisSM;
    public VBox buttonsSMVBox;
    public TextField taskTypeAB;
    public TextField fracTypeAB;
    public TextField modeAB;
    public TextField basisAB;
    public Button readTaskButton;

    private SimplexTable currentSimplexTable;
    private Task currentTask;

    public TextField numVars;          // количество переменных
    public TextField numRestrictions;  // количество ограничений
    public ComboBox<String> comboTask;         // тип задачи (min, max)
    public TabPane tabPane;            // панель вкладок
    public Tab simplexTab;             // вкладка симплекс метода
    public Tab artificialBasisTab;     // вкладка искусственного базиса
    public Button simplexButton;       // кнопка перехода к симплекс-методу
    public Button artificialBasisButton; // кнопка перехода к ИБ
    public ComboBox<String> comboMode;         // выбор режима (авто, пошаговый)
    public ComboBox<String> comboFrac;         // выбор дробей (обычные, десятичные)
    public ComboBox<String> comboBasis;        // выбор базиса (искусственный, готовый)
    public VBox vBoxBasisVars;         // контейнер для чекбокса базиса

    public GridPane gridPaneFunctionSM;
    public GridPane gridPaneMatrixSM;
    public GridPane xSolutionSMGridPane;
    public GridPane ySolutionSMGridPane;


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

            if (rows > 16 || columns > 16 || rows < 1 || columns < 1) {
                ErrorMessage.showError("Некорректные данные", "Введенные параметры не подходят для создания задачи.");
                return;
            }

            readTaskButton.setDisable(false); // разблокировка кнопки "считать задачу"

            String basisType = comboBasis.getValue();
            vBoxBasisVars.getChildren().clear(); // очистка поля для выбора базиса

            if (Objects.equals(basisType, MANUAL_BASIS)) {
                Label basisLabel = new Label("Выберите базис");
                vBoxBasisVars.getChildren().add(basisLabel);
                for (int i = 1; i <= columns; i++) {
                    CheckBox checkBox = new CheckBox("X%d".formatted(i));
                    checkBox.setUserData(i);
                    vBoxBasisVars.getChildren().add(checkBox);
                }
            }

            createMatrixFields(rows, columns);
            createFunctionFields(columns);
        } catch (NumberFormatException e) {
            ErrorMessage.showError("Некорректные данные", "Ошибка при чтении числа: " + e);
        }
    }

    @FXML
    private void handleReadTask() {
        // метод для считывания введенной задачи
        int rows = Integer.parseInt(numRestrictions.getText());
        int columns = Integer.parseInt(numVars.getText());

        Fraction[][] matrixData;
        ArrayList<Fraction> function;
        // Получаем данные матрицы
        try {
            matrixData = getMatrixData(rows, columns + 1);
            function = getFunctionData(columns);
        } catch (NumberFormatException e) {
            ErrorMessage.showError("Ошибка ввода", "Невозможно преобразовать введенные данные в числа.");
            return;
        }

        String taskType = comboTask.getValue();
        String fracType = Objects.equals(comboFrac.getValue(), "обычные") ? "ordinary" : "decimal";
        String mode = Objects.equals(comboMode.getValue(), "авто") ? "auto" : "manual";
        ArrayList<Integer> base = getCheckBoxData();

        readTaskButton.setDisable(true); // блокируем кнопку считывания задачи
        if (base == null) {
            // случай не переданного базиса
            simplexButton.setDisable(true);
            artificialBasisButton.setDisable(false);
            currentTask = new Task(
                    function,
                    new Matrix(matrixData),
                    false,
                    taskType,
                    fracType,
                    mode
            );
        } else {
            // случай переданного базиса
            simplexButton.setDisable(false);
            artificialBasisButton.setDisable(true);
            currentTask = new Task(
                    function,
                    new Matrix(matrixData),
                    false,
                    base,
                    taskType,
                    fracType,
                    mode
            );
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

    private void createFunctionFieldsSMAB(ArrayList<Integer> freeVars, ArrayList<String> function, GridPane gridPane) {
        // заполнение коэффициентов функции
        gridPane.getChildren().clear();

        // Добавляем Label "f(x)" в первую ячейку на первой строке
        Label fxLabel = new Label("f(x) ");
        fxLabel.setPrefWidth(40);
        fxLabel.setPrefHeight(20);
        gridPane.add(fxLabel, 0, 1);

        // Цикл для добавления полей и меток "Xi"
        for (int col : freeVars) {
            int i = freeVars.indexOf(col);
            // Добавляем метку "Xi"
            Label xiLabel = new Label("X" + col);
            xiLabel.setPrefWidth(60);
            xiLabel.setPrefHeight(20);

            xiLabel.setAlignment(Pos.CENTER);
            gridPane.add(xiLabel, i + 1, 0); // Помещаем метку "Xi" в строку 0, колонку col
            GridPane.setMargin(xiLabel, new Insets(2.5, 2.5, 2.5, 2.5)); // отступы слева и справа


            // Добавляем текстовое поле для ввода значений
            TextField textField = new TextField();
            textField.setText(function.get(i));
            textField.setEditable(false);
            textField.setAlignment(Pos.CENTER);
            textField.setPrefWidth(60); // Предпочтительная ширина
            textField.setPrefHeight(20); // Предпочтительная высота
            // Добавляем текстовое поле в строку 1, колонку col
            gridPane.add(textField, i + 1, 1);
            GridPane.setMargin(textField, new Insets(2.5, 2.5, 2.5, 2.5)); // отступы слева и справа
        }

        // Добавляем метку "b"
        Label xiLabel = new Label("b");
        xiLabel.setPrefWidth(60);
        xiLabel.setPrefHeight(20);
        xiLabel.setAlignment(Pos.CENTER);
        gridPane.add(xiLabel, freeVars.size() + 1, 0); // Помещаем метку "Xi" в строку 0, колонку col
        GridPane.setMargin(xiLabel, new Insets(2.5, 5, 2.5, 10)); // отступы слева и справа


        // Добавляем текстовое поле для ввода значений
        TextField textField = new TextField();
        textField.setAlignment(Pos.CENTER);
        textField.setText(function.getLast());

        textField.setPrefWidth(60); // Предпочтительная ширина
        textField.setPrefHeight(20); // Предпочтительная высота

        textField.setEditable(false);
        // Добавляем текстовое поле в строку 1, колонку col
        gridPane.add(textField, freeVars.size() + 1, 1);
        GridPane.setMargin(textField, new Insets(2.5, 5, 2.5, 10)); // отступы слева и справа
    }

    private void createMatrixFieldsSMAB(String[][] mtx, ArrayList<Integer> base, GridPane gridPane) {
        // Очищаем старые поля
        gridPane.getChildren().clear();

        int rows = mtx.length;
        int columns = mtx[0].length;

        // Создаем нужное количество TextField
        for (int row = 0; row < rows; row++) {

            Label fxLabel = new Label("X%d ".formatted(base.get(row)));

            fxLabel.setPrefWidth(40);
            fxLabel.setPrefHeight(20);

            gridPane.add(fxLabel, 0, row);
            for (int col = 1; col < columns; col++) {
                TextField textField = new TextField();
                textField.setAlignment(Pos.CENTER);

                textField.setText(mtx[row][col-1]);
                textField.setEditable(false);


                textField.setPrefWidth(60); // Максимальная ширина
                textField.setPrefHeight(20); // Максимальная высота

                // Добавляем поля в GridPane, задаем их позицию
                gridPane.add(textField, col, row);
                GridPane.setMargin(textField, new Insets(2.5, 2.5, 2.5, 2.5)); // отступы слева и справа
            }

            TextField textField = new TextField();
            textField.setAlignment(Pos.CENTER);

            textField.setText(mtx[row][columns - 1]);
            textField.setEditable(false);

            textField.setPrefWidth(60); // Максимальная ширина
            textField.setPrefHeight(20); // Максимальная высота

            gridPane.add(textField, columns, row);
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

        for (int i = 1; i <= solution.getVectorSolutionStr().size(); i++) {
            // Добавляем метку "Xi"
            Label xiLabel = new Label("X" + i);
            xiLabel.setPrefWidth(60);
            xiLabel.setPrefHeight(20);

            xiLabel.setAlignment(Pos.CENTER);
            xSolutionSMGridPane.add(xiLabel, i + 1, 0); // Помещаем метку "Xi" в строку 0, колонку col
            GridPane.setMargin(xiLabel, new Insets(2.5, 2.5, 2.5, 2.5)); // отступы слева и справа

            // Добавляем текстовое поле для ввода значений
            TextField textField = new TextField();
            textField.setText(solution.getVectorSolutionStr().get(i - 1));
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
        textField.setText(solution.getFunctionValueStr());
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
        currentSimplexTable = SimplexMethod.gauss(currentTask);

        if (currentSimplexTable.getErrorMassage() != null) {
            ErrorMessage.showError("Некорректные данные", currentSimplexTable.getErrorMassage());
            return;
        }
        updateSimplexTab();
        tabPane.getSelectionModel().select(simplexTab);
    }


    private void updateSimplexTab() {
        // метод для добавления данных во вкладку симплекс метода

        // добавление данных задачи

        // очистка от предыдущих данных
        taskTypeSM.clear();
        fracTypeSM.clear();
        modeSM.clear();
        buttonsSMVBox.getChildren().clear();

        // добавление новых данных
        taskTypeSM.setText(currentSimplexTable.getTaskType());
        fracTypeSM.setText(currentSimplexTable.getFracType());
        modeSM.setText(currentTask.getMode());
        basisSM.setText(getBasisStr());

        // запрет на редактирование
        taskTypeSM.setEditable(false);
        fracTypeSM.setEditable(false);
        modeSM.setEditable(false);
        basisSM.setEditable(false);

        // генерация кнопок
        if (Objects.equals(currentSimplexTable.getMode(), "auto")) {
            Button autoModeButton = new Button("Решить");
            autoModeButton.setOnAction(actionEvent -> getSolutionSM()); // назначаем действие
            buttonsSMVBox.getChildren().add(autoModeButton);
        } else {
            Button stepUp = new Button("Шаг вперед");
            Button stepBack = new Button("Шаг назад");
            buttonsSMVBox.getChildren().addAll(stepUp, stepBack);
        }
        // заполнение функции
        createFunctionFieldsSMAB(currentSimplexTable.getFreeVars(), currentSimplexTable.getFunctionStr(), gridPaneFunctionSM);

        // заполнение матрицы
        createMatrixFieldsSMAB(currentSimplexTable.getMatrixStr(), currentSimplexTable.getBase(), gridPaneMatrixSM);
    }


    // Обработчик для кнопки "Метод искусственного базиса"
    @FXML
    private void handleArtificialBasisMethod() {
        // Переходим на вкладку "метод искусственного базиса"
        currentSimplexTable = ArtificialBasisMethod.createSimplexTable(currentTask);

        if (currentSimplexTable.getErrorMassage() != null) {
            ErrorMessage.showError("Некорректные данные", currentSimplexTable.getErrorMassage());
            return;
        }
        updateArtificialBasisTab();
        tabPane.getSelectionModel().select(artificialBasisTab);
    }


    private void updateArtificialBasisTab() {
        // метод для добавления данных во вкладку симплекс метода

        // очистка от предыдущих данных
        taskTypeAB.clear();
        fracTypeAB.clear();
        modeAB.clear();
        buttonsABVBox.getChildren().clear();

        // добавление новых данных
        taskTypeAB.setText(currentSimplexTable.getTaskType());
        fracTypeAB.setText(currentSimplexTable.getFracType());
        modeAB.setText(currentTask.getMode());
        basisAB.setText(getBasisStr());

        // запрет на редактирование
        taskTypeAB.setEditable(false);
        fracTypeAB.setEditable(false);
        modeSM.setEditable(false);
        basisSM.setEditable(false);

        if (Objects.equals(currentSimplexTable.getMode(), "auto")) {
            Button autoModeButton = new Button("Решить");
            autoModeButton.setOnAction(actionEvent -> getSolutionAB()); // назначаем действие
            buttonsABVBox.getChildren().add(autoModeButton);
        } else {
            Button stepUp = new Button("Шаг вперед");
            Button stepBack = new Button("Шаг назад");
            buttonsABVBox.getChildren().addAll(stepUp, stepBack);
        }

        // заполнение функции
        createFunctionFieldsSMAB(currentSimplexTable.getFreeVars(), currentSimplexTable.getFunctionStr(), gridPaneFunctionAB);

        // заполнение матрицы
        createMatrixFieldsSMAB(currentSimplexTable.getMatrixStr(), currentSimplexTable.getBase(), gridPaneMatrixAB);

    }

    // Метод для получения данных из матрицы
    private Fraction[][] getMatrixData(int rows, int columns) throws NumberFormatException {
        Fraction[][] matrixData = new Fraction[rows][columns];

        // Пробегаем по всем ячейкам матрицы и считываем данные
        for (int row = 0; row < rows; row++) {
            for (int col = 1; col <= columns; col++) {  // Пропускаем первый столбец с Label "f(x)"
                TextField textField = (TextField) getNodeFromGridPane(row, col, "matrix");
                // Считываем данные и сохраняем их в матрице
                matrixData[row][col - 1] = Fraction.parseFraction(textField.getText());
            }
        }

        return matrixData;
    }


    // метод для получения коэффициентов функции
    private ArrayList<Fraction> getFunctionData(int columns) throws NumberFormatException{
        ArrayList<Fraction> functionData = new ArrayList<>();

        for (int col = 1; col <= columns; col++) {
            TextField textField = (TextField) getNodeFromGridPane(1, col, "function");
            functionData.add(Fraction.parseFraction(textField.getText()));
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
        return basisNums.isEmpty() ? null : basisNums;
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
        // обработка действий при нажатии на кнопку "решить" во вкладке СМ

        Solution solution = SimplexMethod.autoMode(currentSimplexTable);

        if (solution.getErrorMessage() != null) {
            ErrorMessage.showError("Ошибка", solution.getErrorMessage());
            return;
        }
        updateSimplexTab();
        createSolutionFieldsSM(solution);
        buttonsSMVBox.getChildren().getFirst().setDisable(true);
    }


    private void getSolutionAB() {
        // обработка действий при нажатии на кнопку "решить" во вкладке ИБ
        currentSimplexTable = ArtificialBasisMethod.autoMode(currentSimplexTable, currentTask.getFunction());

        // записываем 0 шаг симплекс метода
        if (currentSimplexTable.getErrorMassage() != null) {
            ErrorMessage.showError("Некорректные данные", currentSimplexTable.getErrorMassage());
            return;
        }
        updateArtificialBasisTab();
        buttonsABVBox.getChildren().clear(); // удаляем кнопку "решить"
        Button goToSMButton = new Button("Решить полностью");
        goToSMButton.setOnAction(actionEvent -> goToSM());
        buttonsABVBox.getChildren().add(goToSMButton);
    }

    private void goToSM() {
        // метод для перехода от ИБ к СМ
        buttonsABVBox.getChildren().getFirst().setDisable(true);
        tabPane.getSelectionModel().select(simplexTab); // переход на другую вкладку

        updateSimplexTab(); // обновление симплекс таблицы
        getSolutionSM(); // получение решания симплекс методом
    }

    private String getBasisStr() {
        // метод для создания строки с текущим базисом
        StringBuilder basisStr = new StringBuilder();
        for (int i : currentSimplexTable.getBase()) {
            basisStr.append("X%d ".formatted(i));
        }
        return basisStr.toString();
    }

    @FXML
    private void handleLoadTask() {
        // Реализация загрузки задачи
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Загрузить задачу");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Task Files", "*.json"));
        File file = fileChooser.showOpenDialog(tabPane.getScene().getWindow());
        currentTask = ReadTask.readSMFromJson(file);

        if (currentTask.getErrorMessage() != null) {
            ErrorMessage.showError("Ошибка при считывании", currentTask.getErrorMessage());
        } else if (currentTask.getBase() == null) {
            currentSimplexTable = ArtificialBasisMethod.createSimplexTable(currentTask);
            updateArtificialBasisTab();
            tabPane.getSelectionModel().select(artificialBasisTab);

        } else {
            currentSimplexTable = SimplexMethod.gauss(currentTask);
            updateSimplexTab();
            tabPane.getSelectionModel().select(simplexTab);
        }
    }


    @FXML
    private void handleSaveTask() {
        // Реализация сохранения задачи (например, в файл)
        if (currentTask != null) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Сохранить задачу");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Task Files", "*.json"));
            File file = fileChooser.showSaveDialog(tabPane.getScene().getWindow());

            try {
                ReadTask.saveSMasJson(currentTask, file);
            } catch (IOException e) {
                ErrorMessage.showError("Ошибка сохранения", e.toString());
            }


        } else {
            ErrorMessage.showError("Ошибка", "Нет данных для сохранения!");
        }
    }
}
