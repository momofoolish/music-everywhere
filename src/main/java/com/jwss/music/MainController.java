package com.jwss.music;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.jwss.component.button.JuButton;
import com.jwss.music.entity.AppContext;
import com.jwss.music.entity.Music;
import com.jwss.music.factory.ServiceFactory;
import com.jwss.music.observer.ViewDataObserver;
import com.jwss.music.observer.ViewObserver;
import com.jwss.music.service.ICacheService;
import com.jwss.music.service.IMediaPlayerService;
import com.jwss.music.service.IMusicImportService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import java.util.List;

/**
 * @author jwss
 * @date 2022-2-6 22:13:40
 */
public class MainController {
    private static final Log logger = LogFactory.get();

    @FXML
    private HBox topHBox;

    @FXML
    private TableView<Music> musicTableView;
    @FXML
    private TableColumn<Music, String> nameTableColumn;
    @FXML
    private TableColumn<Music, String> authorTableColumn;
    @FXML
    private TableColumn<Music, String> albumTableColumn;
    @FXML
    private TableColumn<Music, String> durationTableColumn;
    @FXML
    private TableColumn<Music, String> sizeTableColumn;

    @FXML
    private ProgressBar musicPlayProgress;
    @FXML
    private Label currentPlayLabel;
    @FXML
    private Label endPlayLabel;

    @FXML
    private Button playOrPauseMusicBtn;
    @FXML
    private Button previewBtn;
    @FXML
    private Button nextBtn;

    @FXML
    private Button simpleImportButton;
    @FXML
    private Button folderImportButton;

    private final JuButton webImportButton = new JuButton();

    private final IMediaPlayerService mediaPlayerService = ServiceFactory.getMediaPlayer();
    private final IMusicImportService musicImportService = ServiceFactory.getMusicImport();
    private final ICacheService cacheService = ServiceFactory.getCacheService();

    @FXML
    protected void onImportMusicClick() {
        List<Music> musicList = musicImportService.importMusic();
        if (musicList.size() > 0) {
            renderTableView(musicList);
            cacheService.saveMusicList(musicList);
        }
    }

    @FXML
    protected void onImportMusicClickByFolder() {
        // ???????????????
        List<Music> musicList = musicImportService.importMusicByFolder();
        if (musicList.size() > 0) {
            renderTableView(musicList);
            cacheService.saveMusicList(musicList);
        }
    }

    @FXML
    protected void onPreview() {
        // ?????????
        mediaPlayerService.preview();
    }

    @FXML
    protected void onPlayOrPause() {
        // ????????????????????????
        if (!AppContext.getIsPlay()) {
            mediaPlayerService.play();
        } else {
            mediaPlayerService.pause();
        }
    }

    @FXML
    protected void onNext() {
        // ?????????
        mediaPlayerService.next();
    }

    /**
     * ???fxml????????????????????????????????????
     */
    @FXML
    protected void initialize() {
        // ????????????
        nameTableColumn.setCellValueFactory(cell -> cell.getValue().nameProperty());
        authorTableColumn.setCellValueFactory(cell -> cell.getValue().authorProperty());
        albumTableColumn.setCellValueFactory(cell -> cell.getValue().albumProperty());
        durationTableColumn.setCellValueFactory(cell -> cell.getValue().durationProperty());
        sizeTableColumn.setCellValueFactory(cell -> cell.getValue().sizeProperty());
        // ?????????????????????
        nameTableColumn.prefWidthProperty().bind(musicTableView.widthProperty().multiply(0.2));
        authorTableColumn.prefWidthProperty().bind(musicTableView.widthProperty().multiply(0.28));
        albumTableColumn.prefWidthProperty().bind(musicTableView.widthProperty().multiply(0.3));
        durationTableColumn.prefWidthProperty().bind(musicTableView.widthProperty().multiply(0.1));
        sizeTableColumn.prefWidthProperty().bind(musicTableView.widthProperty().multiply(0.1));
        // ???????????????????????????
        nameTableColumn.setResizable(false);
        authorTableColumn.setResizable(false);
        albumTableColumn.setResizable(false);
        durationTableColumn.setResizable(false);
        sizeTableColumn.setResizable(false);

        // ????????????
        ImageView importView = new ImageView(new Image("file:icons/import.png"));
        ImageView folderView = new ImageView(new Image("file:icons/folder.png"));
        ImageView webView = new ImageView(new Image("file:icons/web.png"));
        simpleImportButton.setGraphic(importView);
        folderImportButton.setGraphic(folderView);

        webImportButton.setGraphic(webView);
        webImportButton.setText("????????????");
        webImportButton.setOnMouseClicked(event -> {
            // ????????????????????????
            musicImportService.importMusicByShare();
        });
        topHBox.getChildren().add(webImportButton);

        // ???????????????
        ImageView playView = new ImageView(new Image("file:icons/play.png"));
        ImageView nextView = new ImageView(new Image("file:icons/next.png"));
        ImageView previewView = new ImageView(new Image("file:icons/preview.png"));
        playOrPauseMusicBtn.setGraphic(playView);
        previewBtn.setGraphic(previewView);
        nextBtn.setGraphic(nextView);

        // ??????????????????
        renderTableView(cacheService.getMusicList());
        // ???????????????????????????
        ViewObserver.load(playOrPauseMusicBtn, currentPlayLabel, endPlayLabel, musicPlayProgress);
        // ??????????????????????????????
        AppContext.setPlayList(cacheService.getMusicList());
    }

    /**
     * ??????????????????
     *
     * @param musicList ????????????
     */
    private void renderTableView(List<Music> musicList) {
        ObservableList<Music> musicObservableList = FXCollections.observableArrayList();
        musicObservableList.addAll(musicList);
        mediaPlayerService.setEvent(musicTableView);
        musicTableView.setItems(musicObservableList);
        ViewDataObserver.musicObservableList = musicObservableList;
    }
}