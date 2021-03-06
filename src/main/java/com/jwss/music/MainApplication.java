package com.jwss.music;

import com.jwss.music.entity.AppContext;
import com.jwss.music.util.SqliteUtils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * @author jwss
 */
public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1022, 650);
        stage.setTitle("MusicEverywhere");
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(event -> SqliteUtils.close());
        // 将场景放入上下文
        AppContext.setFxmlLoader(fxmlLoader);
        AppContext.setStage(stage);
        // 初始化配置
        InitApp.config();
    }

    public static void main(String[] args) {
        launch();
    }
}