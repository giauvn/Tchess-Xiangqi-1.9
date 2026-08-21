package com.sojourners.chess;

import com.sojourners.chess.config.Properties;
import com.sojourners.chess.controller.ColorSettingController;
import com.sojourners.chess.controller.Controller;
import com.sojourners.chess.controller.EditChessBoardController;
import com.sojourners.chess.controller.LocalBookController;
import javafx.application.Application;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import java.util.Locale;
import java.util.ResourceBundle;
import java.net.URL;
/**
 * Main window
 */
public class App extends Application {
    public static final String VERSION = "1.9";
    public static final String BUILT_ON = "20260801";
    private static Stage engineAdd;
    private static Stage engineSetting;
    private static Stage localBookSetting;
    private static Stage mainStage;
    private static Stage timeSetting;
    private static Stage bookSetting;
    private static Stage linkSetting;
    private static Stage editChessBoard;
    private static final String LIGHT_THEME = themeResource("/style/light-theme.css");
    private static final String DARK_THEME = themeResource("/style/dark-theme.css");
    // --- THÊM DÒNG NÀY cho việc chuyển đổi menu sang tiếng Anh và Trung---
    private static Locale currentLocale = new Locale("en", "US"); // Mặc định tiếng Anh
    private static ResourceBundle currentBundle = ResourceBundle.getBundle("fxml.langue", currentLocale);
	// Thêm hàm này vào trong file App.java của bạn nó sẽ dùng để getString trong Controller.java
	public static String getBundleString(String key) {
    if (currentBundle != null && currentBundle.containsKey(key)) {
        return currentBundle.getString(key);
		}
    return key; // Trả về chính key nếu không tìm thấy chuỗi tương ứng
	}
    // ---kết thúc cho việc chuyển đổi menu sang tiếng Anh và Trung----------------------
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/fxml/app.fxml"));
        //--Nạp Resources Bundle cho việc thay đổi ngôn ngữ
        fxmlLoader.setResources(currentBundle);
        //-------------------------------
        Parent root = fxmlLoader.load();
        //Tiêu đề cũ gốc primaryStage.setTitle("TCHESS  V" + VERSION); set tiêu đề mới dòng dưới
        primaryStage.setTitle(currentBundle.getString("app.title"));
        //------------------
        Scene scene = new Scene(root);
		// THÊM DÒNG NÀY ĐỂ LƯU BIẾN CONTROLLER:
		scene.setUserData(fxmlLoader.getController()); 
		
        applyTheme(scene);
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/image/icon.png")));

        primaryStage.setOnCloseRequest(new EventHandler() {
            @Override
            public void handle(Event event) {
                Controller controller = fxmlLoader.getController();
                controller.exit();
            }
        });
        primaryStage.setOnShowing(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                Controller controller = fxmlLoader.getController();
                controller.initStage();
            }
        });

        mainStage = primaryStage;

        primaryStage.show();
    }
    public static void topWindow(boolean top) {
        mainStage.setAlwaysOnTop(top);
    }  
		// Engine Management Dialog

	public static void switchLanguage(String languageCode, String countryCode) {
		try {
			currentLocale = new Locale(languageCode, countryCode);
			currentBundle = ResourceBundle.getBundle("fxml.langue", currentLocale);
			// Không setRoot lại giao diện nữa mà gọi Controller cập nhật chữ
			mainStage.setTitle(currentBundle.getString("app.title"));
			// (Cần lưu trữ biến controller gốc khi chạy start() để gọi tại đây)
			// globalController.refreshUILanguage();
			// Thêm dòng này vào cuối khối try của hàm switchLanguage trong App.java:
			Controller controller = (Controller) mainStage.getScene().getUserData(); 
			if (controller != null) {
				controller.refreshUILanguage();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    public static void openEngineDialog() {
        engineSetting = createStage("/fxml/engineDialog.fxml");
		if (engineSetting != null && currentBundle != null) {
        engineSetting.setTitle(currentBundle.getString("engine.title"));
		} else {
			engineSetting.setTitle("Engine Management"); // Chuỗi tiếng ngôn ngữ gốc phòng khi lỗi ngôn ngữ
		}	
        engineSetting.initModality(Modality.APPLICATION_MODAL);
        engineSetting.initOwner(mainStage);

        engineSetting.showAndWait();
    }

    /**
     * Local Library Management Dialog Box
     */
    public static boolean openLocalBookDialog() {
        localBookSetting = createStage("/fxml/localBook.fxml");
        // Đoạn code chèn vào để chuyển đổi ngôn ngữ
		if (localBookSetting != null && currentBundle != null) {
        localBookSetting.setTitle(currentBundle.getString("booksettingdialog.localbookmanage")); 
		} else {
			localBookSetting.setTitle("Local Book Manage"); // Chuỗi tiếng ngôn ngữ gốc phòng khi lỗi ngôn ngữ
		}	
		localBookSetting.initModality(Modality.APPLICATION_MODAL);
        localBookSetting.initOwner(mainStage);
        localBookSetting.showAndWait();
        return LocalBookController.change;
    }
    /**
     * Add engine
     */
    public static void openEngineAdd() {
        engineAdd = createStage("/fxml/engineAdd.fxml");
       // Đoạn code chèn vào để chuyển đổi ngôn ngữ
		if (engineAdd != null && currentBundle != null) {
        engineAdd.setTitle(currentBundle.getString("appjava.addengine")); 
		} else {
			engineAdd.setTitle("Add Engine"); // Chuỗi tiếng ngôn ngữ gốc phòng khi lỗi ngôn ngữ
		}			
        engineAdd.initModality(Modality.APPLICATION_MODAL);
        engineAdd.initOwner(engineSetting);
        engineAdd.showAndWait();
    }
    public static void closeEngineAdd() {
        engineAdd.close();
    }
    /**
     * Time Settings
     */
    public static void openTimeSetting() {

        timeSetting = createStage("/fxml/timeSetting.fxml");
       // Đoạn code chèn vào để chuyển đổi ngôn ngữ
		if (timeSetting != null && currentBundle != null) {
        timeSetting.setTitle(currentBundle.getString("appjava.timetitle")); 
		} else {
			timeSetting.setTitle("Time Settings"); // Chuỗi tiếng ngôn ngữ gốc phòng khi lỗi ngôn ngữ
		}			
        timeSetting.initModality(Modality.APPLICATION_MODAL);
        timeSetting.initOwner(mainStage);

        timeSetting.showAndWait();
    }
    public static void closeTimeSetting() {
        timeSetting.close();
    }

    /**
     * Inventory Replenishment Settings
     */
    public static void openBookSetting() {

        bookSetting = createStage("/fxml/bookSetting.fxml");
       // Đoạn code chèn vào để chuyển đổi ngôn ngữ
		if (bookSetting != null && currentBundle != null) {
        bookSetting.setTitle(currentBundle.getString("appjava.booksetting")); 
		} else {
			bookSetting.setTitle("Opening Book Settings"); // Chuỗi tiếng ngôn ngữ gốc phòng khi lỗi ngôn ngữ
		}			
        bookSetting.initModality(Modality.APPLICATION_MODAL);
        bookSetting.initOwner(mainStage);

        bookSetting.showAndWait();
    }
    public static void closeBookSetting() {
        bookSetting.close();
    }
    /**
     * Connection settings
     */
    public static void openLinkSetting() {

        linkSetting = createStage("/fxml/linkSetting.fxml");
		// Đoạn code chèn vào để chuyển đổi ngôn ngữ
		if (currentBundle != null) {
			linkSetting.setTitle(currentBundle.getString("linksetting.title"));
		} else {
			linkSetting.setTitle("Connection Settings"); // Chuỗi tiếng ngôn ngữ gốc phòng khi lỗi ngôn ngữ
		}
        linkSetting.initModality(Modality.APPLICATION_MODAL);
        linkSetting.initOwner(mainStage);
        linkSetting.showAndWait();
    }
    public static void closeLinkSetting() {
        linkSetting.close();
    }
    public static String openEditChessBoard(char[][] board, boolean redGo, boolean isReverse) {
        try {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(App.class.getResource("/fxml/editChessBoard.fxml"));
            
			 // --- SỬA TẠI ĐÂY: Thêm dòng này để nạp gói ngôn ngữ cho editChessBoard.fxml ---
			 fxmlLoader.setResources(currentBundle);
			 //------------------------------------------------------------------------			
			
			Parent pane = fxmlLoader.load();
            Scene scene = new Scene(pane);
            applyTheme(scene);
            stage.setScene(scene);

            editChessBoard = stage;
			// Đoạn code chèn vào để chuyển đổi ngôn ngữ
			if (currentBundle != null) {
			editChessBoard.setTitle(currentBundle.getString("appjava.edit"));
			} else {
			editChessBoard.setTitle("Edit Position"); // Phòng hờ nếu currentBundle bị null thì vẫn có tiêu đề hiển thị
			}
			//end code langue
            editChessBoard.initModality(Modality.APPLICATION_MODAL);
            editChessBoard.initOwner(mainStage);

            EditChessBoardController controller = fxmlLoader.getController();
            controller.setBoard(board, isReverse);
            controller.setFirstMover(redGo);

            editChessBoard.showAndWait();
            return controller.getFenCode();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static void closeEditChessBoard() {
        editChessBoard.close();
    }

    public static boolean openColorSetting() {
        try {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(App.class.getResource("/fxml/colorSetting.fxml"));
			
			 // --- SỬA TẠI ĐÂY: Thêm dòng này để nạp gói ngôn ngữ cho colorSetting.fxml ---
			 fxmlLoader.setResources(currentBundle);
			 //-----------------------------------------------------------------	
			Parent pane = fxmlLoader.load();
            Scene scene = new Scene(pane);
            applyTheme(scene);
            stage.setScene(scene);
			// Đoạn code chèn vào để chuyển đổi ngôn ngữ
			if (currentBundle != null) {
			stage.setTitle(currentBundle.getString("appjava.theme"));
			} else {
			stage.setTitle("Theme Configuration"); // Phòng hờ nếu currentBundle bị null thì vẫn có tiêu đề hiển thị
			}
			//end code langue			
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(mainStage);
            ColorSettingController controller = fxmlLoader.getController();
            stage.showAndWait();
            return controller.isSaved();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void refreshTheme() {
        for (Window window : Window.getWindows()) {
            if (window.getScene() != null) {
                applyTheme(window.getScene());
            }
        }
    }

    public static void applyTheme(Scene scene) {
        scene.getStylesheets().removeAll(LIGHT_THEME, DARK_THEME);
        scene.getRoot().getStyleClass().removeAll("light-theme", "dark-theme");
        String theme;
        if (Properties.getInstance().getColorTheme() == Properties.ColorTheme.DARK) {
            theme = DARK_THEME;
            scene.getRoot().getStyleClass().add("dark-theme");
        } else {
            theme = LIGHT_THEME;
            scene.getRoot().getStyleClass().add("light-theme");
        }
        scene.getStylesheets().add(theme);
        applyThemeToLocalStylesheets(scene.getRoot(), theme);
    }

    private static void applyThemeToLocalStylesheets(Parent parent, String theme) {
        boolean hasLocalStylesheet = parent.getStylesheets().stream()
                .anyMatch(stylesheet -> !LIGHT_THEME.equals(stylesheet) && !DARK_THEME.equals(stylesheet));
        parent.getStylesheets().removeAll(LIGHT_THEME, DARK_THEME);
        if (hasLocalStylesheet) {
            parent.getStylesheets().add(theme);
        }
        for (Node child : parent.getChildrenUnmodifiable()) {
            if (child instanceof Parent childParent) {
                applyThemeToLocalStylesheets(childParent, theme);
            }
        }
    }

    private static Stage createStage(String resource) {
        try {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(App.class.getResource(resource));
            // --- THÊM DÒNG NÀY thay đổi ngôn ngữ ---
            fxmlLoader.setResources(currentBundle);
            // ------------------------
            Parent pane = fxmlLoader.load();
            Scene scene = new Scene(pane);
            applyTheme(scene);
            stage.setScene(scene);
            return stage;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Stage getEngineAdd() {
        return engineAdd;
    }

    public static Stage getEngineDialog() {
        return engineSetting;
    }

    public static Stage getMainStage() {
        return mainStage;
    }

    public static Stage getLocalBookSetting() {
        return localBookSetting;
    }

	private static String themeResource(String resource) {
		URL url = App.class.getResource(resource);
		if (url == null) {
			// Lấy chuỗi thông báo theo ngôn ngữ hiện tại và ghép với tên resource
			String errorMsg = (currentBundle != null) 
				? currentBundle.getString("appjava.stylenotfound") 
				: "Theme stylesheet not found:";		
			throw new IllegalStateException(errorMsg + " " + resource);
		}
    return url.toExternalForm();
	}
}
