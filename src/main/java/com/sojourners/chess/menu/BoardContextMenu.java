package com.sojourners.chess.menu;

import com.sojourners.chess.App; // Import lớp App để lấy chuỗi đa ngôn ngữ
import com.sojourners.chess.config.Properties;
import com.sojourners.chess.enginee.Engine;
import javafx.scene.control.*;

public class BoardContextMenu extends ContextMenu {

    /** * 单例 */
    private static volatile BoardContextMenu INSTANCE = null;
    // Khai báo các thuộc tính menu để có thể cập nhật lại nhãn chữ khi đổi ngôn ngữ
    private final MenuItem editMenuItem;
    private final MenuItem copyFenMenuItem;
    private final MenuItem pasteFenMenuItem;
    private final MenuItem copyImageMenuItem;
    private final MenuItem pasteImageMenuItem;
    private final MenuItem copyManualMenuItem;
    private final MenuItem pasteManualMenuItem;
    private final Menu timeMenu;
    private final MenuItem switchMenuItem;

    /** * 私有构造函数 */
    private BoardContextMenu() {
        editMenuItem = new MenuItem();
        getItems().add(editMenuItem);
        getItems().add(new SeparatorMenuItem());

        copyFenMenuItem = new MenuItem();
        pasteFenMenuItem = new MenuItem();
        getItems().addAll(copyFenMenuItem, pasteFenMenuItem);
        getItems().add(new SeparatorMenuItem());

        copyImageMenuItem = new MenuItem();
        pasteImageMenuItem = new MenuItem();
        getItems().addAll(copyImageMenuItem, pasteImageMenuItem);
        getItems().add(new SeparatorMenuItem());

        copyManualMenuItem = new MenuItem();
        pasteManualMenuItem = new MenuItem();
        getItems().addAll(copyManualMenuItem, pasteManualMenuItem);
        getItems().add(new SeparatorMenuItem());

        timeMenu = new Menu();
        MenuItem timeOf01 = new MenuItem("0.1s");
        MenuItem timeOf03 = new MenuItem("0.3s");
        MenuItem timeOf05 = new MenuItem("0.5s");
        MenuItem timeOf1 = new MenuItem("1s");
        MenuItem timeOf2 = new MenuItem("2s");
        MenuItem timeOf3 = new MenuItem("3s");
        MenuItem timeOf5 = new MenuItem("5s");
        MenuItem timeOf10 = new MenuItem("10s");
        MenuItem timeOf15 = new MenuItem("15s");
        
		timeMenu.setOnAction(event -> {
            /**String time = ((MenuItem) event.getTarget()).getText();
            if (!"对局时间".equals(time)) {
                time = time.substring(0, time.length() - 1);
                long t = (long) (Double.parseDouble(time) * 1000);
                Properties prop = Properties.getInstance();
                prop.setAnalysisModel(Engine.AnalysisModel.FIXED_TIME);
                prop.setAnalysisValue(t);
            }
        });*/
            MenuItem targetItem = (MenuItem) event.getTarget();
            // CHÚ Ý LOGIC ĐA NGÔN NGỮ: Kiểm tra nếu mục click không phải là chính thanh Menu chính
            if (targetItem != timeMenu) {
                String time = targetItem.getText();
                time = time.substring(0, time.length() - 1); // Cắt bỏ chữ 's' ở cuối
                long t = (long) (Double.parseDouble(time) * 1000);
                Properties prop = Properties.getInstance();
                prop.setAnalysisModel(Engine.AnalysisModel.FIXED_TIME);
                prop.setAnalysisValue(t);
            }
        });		
        timeMenu.getItems().addAll(timeOf01, timeOf03, timeOf05, timeOf1, timeOf2, timeOf3, timeOf5, timeOf10, timeOf15);
		
        getItems().add(timeMenu);
        getItems().add(new SeparatorMenuItem());
/**
        MenuItem switchMenuItem = new MenuItem("交换行棋方");
        getItems().add(switchMenuItem);
    }
*/
        switchMenuItem = new MenuItem();
        getItems().add(switchMenuItem);

        // Nạp ngôn ngữ lần đầu tiên khi khởi tạo cấu trúc menu
        updateLocale();
    }

    /**
     * Hàm cập nhật lại toàn bộ nhãn chữ hiển thị của Menu chuột phải theo ngôn ngữ hiện tại.
     * Cần được gọi mỗi khi người dùng thực hiện chuyển đổi ngôn ngữ trong ứng dụng.
     */
    public void updateLocale() {
        editMenuItem.setText(App.getBundleString("control.edit"));
        copyFenMenuItem.setText(App.getBundleString("control.copyfen"));
        pasteFenMenuItem.setText(App.getBundleString("control.pastefen"));
        copyImageMenuItem.setText(App.getBundleString("control.copyimage"));
        pasteImageMenuItem.setText(App.getBundleString("control.pasteimage"));
        copyManualMenuItem.setText(App.getBundleString("control.copygame"));
        pasteManualMenuItem.setText(App.getBundleString("control.pastegame"));
        timeMenu.setText(App.getBundleString("contextmenu.time"));
        switchMenuItem.setText(App.getBundleString("control.switchplayer"));
    }
	
    /** * 获取实例 * @return GlobalMenu */
    public static BoardContextMenu getInstance() {
        if (INSTANCE == null) {
            synchronized (BoardContextMenu.class) {
                if (INSTANCE == null) {
                    INSTANCE = new BoardContextMenu();
                }
            }
        }
        return INSTANCE;
    }
}
