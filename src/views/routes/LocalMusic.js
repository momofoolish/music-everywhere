import React, { Fragment, useState } from "react";
import MusicList from "../../components/list/MusicList";
import { Button, Popover } from 'antd';
import { ImportOutlined, ReloadOutlined,LoadingOutlined } from '@ant-design/icons';
import './css/LocalMusic.css';

const IMPORT_TYPE = [{ code: 0, message: '导入文件' }, { code: 1, message: '导入文件夹' }]

/**
 * 本地音乐页
 */
function LocalMusic() {

    const [musicList, setMusicList] = useState([]);
    const [loading, setLoading] = useState(false);

    /**
     * 点击导入
     * @param importType 导入类型：0文件 1文件夹
     */
    const onImportClick = (importType) => {
        setLoading(true);
        let option;
        let musicData = musicList.map(item => item);
        let paths;
        // 操作类型判断
        if (importType === IMPORT_TYPE[0].code) {
            option = {
                properties: ['openFile', 'multiSelections'],
                filters: [{ name: 'Music', extensions: ['wav', 'mp3'] }]
            };
            paths = window.electron.showOpenDialogSync(option);
        } else if (importType === IMPORT_TYPE[1].code) {
            option = { properties: ['openDirectory'] };
            paths = window.electron.showOpenDialogSync(option);
            // 获取目录所有文件
            let fileNames = window.electron.readdirSync(paths[0]);
            let newPath = [];
            for (let i = 0; i < fileNames.length; i++) {
                let fileNamesArr = fileNames[i].split('.');
                // 判断文件格式
                if (fileNamesArr[fileNamesArr.length - 1] === 'mp3' ||
                    fileNamesArr[fileNamesArr.length - 1] === 'wav'
                ) {
                    let filePath = `${paths[0]}\\${fileNames[i]}`;
                    newPath.push(filePath);
                }
            }
            paths = newPath;
        }
        setTimeout(() => {
            // 遍历获取文件信息
            for (let i = 0; i < paths.length; i++) {
                let info = window.electron.readMediaProp(paths[i]);
                let stats = window.electron.statSync(paths[i]);
                let musicInfo = {
                    key: paths[i],
                    title: info.title,
                    singer: info.artist,
                    album: info.album ? info.album : info.title,
                    size: `${(stats.size / 1024 / 1024).toFixed(2)}M`
                }
                musicData.push(musicInfo);
            }
            setMusicList(musicData);
            setLoading(false);
        }, 200);
    }

    return (
        <div id="localMusicOutBox">
            <div className="local-music-control">
                {/* 导入对话框 */}
                <Popover
                    placement="bottom"
                    content={<ImportPopover onImportClick={onImportClick} />}
                    trigger="hover"
                >
                    <Button type="primary" shape="round">
                        <span>导入本地</span>
                        <ImportOutlined />
                        { loading ? <LoadingOutlined /> : '' }
                    </Button>
                </Popover>

                <Button shape="round">
                    <ReloadOutlined />
                    <span>刷新</span>
                </Button>
            </div>
            <MusicList musicSource={musicList} />
        </div>
    )
}

/**
 * 导入对话框
 */
const ImportPopover = (props) => {
    return (
        <Fragment>
            <Button
                type="link"
                className="local-music-import-popover-button"
                onClick={props.onImportClick.bind(this, 0)}
            >
                {IMPORT_TYPE[0].message}
            </Button>
            <Button
                type="link"
                className="local-music-import-popover-button"
                onClick={props.onImportClick.bind(this, 1)}
            >
                {IMPORT_TYPE[1].message}
            </Button>
        </Fragment>
    )
}

export default LocalMusic;