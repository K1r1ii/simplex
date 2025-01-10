import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class FXMLAppLauncher extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Загрузка FXML файла
            FXMLLoader loader = new FXMLLoader(getClass().getResource("application.fxml"));
            Parent root = loader.load();

            // Создание сцены
            Scene scene = new Scene(root);

            // Установка заголовка и отображение окна
            primaryStage.setTitle("JavaFX FXML Example");
            primaryStage.setResizable(false);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            // Обработка ошибок
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
