package ui;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

public class ErrorMessage {

    // Метод для отображения сообщения об ошибке
    public static void showError(String header, String content) {
        // Создаем сообщение об ошибке
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(header); // Заголовок сообщения
        alert.setContentText(content); // Основной текст ошибки

        // Отображаем сообщение и ждем закрытия
        alert.showAndWait();
    }

    // Метод для отображения информационного сообщения
    public static void showInfo(String header, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Информация");
        alert.setHeaderText(header);
        alert.setContentText(content);

        alert.showAndWait();
    }

    // Метод для отображения предупреждения
    public static void showWarning(String header, String content) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("Предупреждение");
        alert.setHeaderText(header);
        alert.setContentText(content);

        alert.showAndWait();
    }

    // Метод для отображения подтверждения
    public static boolean showConfirmation(String header, String content) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение");
        alert.setHeaderText(header);
        alert.setContentText(content);

        // Ожидаем ответ пользователя (Да / Нет)
        return alert.showAndWait().get() == ButtonType.OK;
    }
}
