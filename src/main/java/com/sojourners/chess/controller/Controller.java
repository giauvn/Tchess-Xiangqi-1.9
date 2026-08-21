package com.sojourners.chess.controller;

import com.sojourners.chess.App;
import com.sojourners.chess.board.ChessBoard;
import com.sojourners.chess.config.Properties;
import com.sojourners.chess.controller.handle.ChessManualCallBack;
import com.sojourners.chess.controller.handle.ChessManualHandle;
import com.sojourners.chess.enginee.Engine;
import com.sojourners.chess.enginee.EngineCallBack;
import com.sojourners.chess.linker.*;
import com.sojourners.chess.menu.BoardContextMenu;
import com.sojourners.chess.model.BookData;
import com.sojourners.chess.model.EngineConfig;
import com.sojourners.chess.model.ManualRecord;
import com.sojourners.chess.model.ThinkData;
import com.sojourners.chess.openbook.OpenBookManager;
import com.sojourners.chess.util.*;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.canvas.Canvas;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.*;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.Callback;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public class Controller implements EngineCallBack, LinkerCallBack, ChessManualCallBack {

    @FXML
    private Canvas canvas;

    @FXML
    private BorderPane borderPane;
    @FXML
    private Label infoShowLabel;
    @FXML
    private ToolBar statusToolBar;
    @FXML
    private Label timeShowLabel;
    @FXML
    private SplitPane splitPane;
    @FXML
    private SplitPane splitPane2;

    @FXML
    private ListView<ThinkData> listView;

    @FXML
    private ComboBox<String> engineComboBox;

    @FXML
    private ComboBox<String> linkComboBox;

    @FXML
    private ComboBox<String> hashComboBox;

    @FXML
    private ComboBox<String> threadComboBox;

    @FXML
    private RadioMenuItem menuOfLargeBoard;
    @FXML
    private RadioMenuItem menuOfBigBoard;
    @FXML
    private RadioMenuItem menuOfMiddleBoard;
    @FXML
    private RadioMenuItem menuOfSmallBoard;
    @FXML
    private RadioMenuItem menuOfAutoFitBoard;

    @FXML
    private RadioMenuItem menuOfDefaultBoard;
    @FXML
    private RadioMenuItem menuOfCustomBoard;

    @FXML
    private CheckMenuItem menuOfStepTip;
    @FXML
    private CheckMenuItem menuOfStepSound;
    @FXML
    private CheckMenuItem menuOfLinkBackMode;
    @FXML
    private CheckMenuItem menuOfLinkAnimation;
    @FXML
    private CheckMenuItem menuOfShowStatus;
    @FXML
    private CheckMenuItem menuOfShowNumber;

    @FXML
    private CheckMenuItem menuOfTopWindow;

    private Properties prop;

    private Engine engine;

    private ChessBoard board;

    private AbstractGraphLinker graphLinker;

    @FXML
    private Button analysisButton;
    @FXML
    private Button blackButton;
    @FXML
    private Button redButton;
    @FXML
    private Button reverseButton;
    @FXML
    private Button newButton;
    @FXML
    private Button copyButton;
    @FXML
    private Button pasteButton;
    @FXML
    private Button regretButton;

    @FXML
    private BorderPane charPane;
    private XYChart.Series lineChartSeries;

    @FXML
    private Button immediateButton;
    @FXML
    private Button bookSwitchButton;
    @FXML
    private Button linkButton;
    @FXML
    private Button changeTacticButton;

    @FXML
    private TableView<ManualRecord> recordTable;

    @FXML
    private TableView<BookData> bookTable;

    private SimpleObjectProperty<Boolean> robotRed = new SimpleObjectProperty<>(false);
    private SimpleObjectProperty<Boolean> robotBlack = new SimpleObjectProperty<>(false);
    private SimpleObjectProperty<Boolean> robotAnalysis = new SimpleObjectProperty<>(false);
    private SimpleObjectProperty<Boolean> isReverse = new SimpleObjectProperty<>(false);
    private SimpleObjectProperty<Boolean> linkMode = new SimpleObjectProperty<>(false);
    private SimpleObjectProperty<Boolean> useOpenBook = new SimpleObjectProperty<>(false);

    /**
     * The player whose turn it is to move
     */
    private boolean redGo;

    /**
     * Thinking... (used for connection status determination)
     */
    private volatile boolean isThinking;
	/**
	 * Kiểm tra xem công cụ (engine) đã được tải hay chưa.
	 * Nếu chưa tải, hàm sẽ tự động hiển thị hộp thoại cảnh báo đa ngôn ngữ.
	 * 
	 * @return true nếu engine CHƯA tải (cần dừng xử lý), false nếu engine đã tải thành công.
	 */
	//Hàm private boolean isEngineNotLoaded() bên dưới khai báo tiêu đề và văn bản chưa nạp engine
	private boolean isEngineNotLoaded() {
		if (engine == null) {
			DialogUtils.showWarningDialog(
				App.getBundleString("control.notice"), 
				App.getBundleString("control.enginenoload")
			);
			return true; // Engine trống, cần ngăn chặn các thao tác tiếp theo
		}
		return false; // Engine hợp lệ, có thể tiếp tục chạy
	}

    /**
     * List of Alternative Moves
     */
    private List<String> tacticList;
	
	
    @FXML
    public void newButtonClick(ActionEvent event) {
        if (linkMode.getValue()) {
            stopGraphLink();
        }

        newChessBoard(null);
    }

    @FXML
    void boardStyleSelected(ActionEvent event) {
        RadioMenuItem item = (RadioMenuItem) event.getTarget();
        if (item.equals(menuOfDefaultBoard)) {
            prop.setBoardStyle(ChessBoard.BoardStyle.DEFAULT);
        } else {
            prop.setBoardStyle(ChessBoard.BoardStyle.CUSTOM);
        }
        board.setBoardStyle(prop.getBoardStyle(), this.canvas);
    }

    @FXML
    void boardSizeSelected(ActionEvent event) {
        RadioMenuItem item = (RadioMenuItem) event.getTarget();
        if (item.equals(menuOfLargeBoard)) {
            prop.setBoardSize(ChessBoard.BoardSize.LARGE_BOARD);
        } else if (item.equals(menuOfBigBoard)) {
            prop.setBoardSize(ChessBoard.BoardSize.BIG_BOARD);
        } else if (item.equals(menuOfMiddleBoard)) {
            prop.setBoardSize(ChessBoard.BoardSize.MIDDLE_BOARD);
        } else if (item.equals(menuOfAutoFitBoard)) {
            prop.setBoardSize(ChessBoard.BoardSize.AUTOFIT_BOARD);
        } else {
            prop.setBoardSize(ChessBoard.BoardSize.SMALL_BOARD);
        }
        board.setBoardSize(prop.getBoardSize());
        if (prop.getBoardSize() == ChessBoard.BoardSize.AUTOFIT_BOARD) {
            board.autoFitSize(borderPane.getWidth(), borderPane.getHeight(), splitPane.getDividerPositions()[0]);
        }
    }
    @FXML
    void stepTipChecked(ActionEvent event) {
        CheckMenuItem item = (CheckMenuItem) event.getTarget();
        prop.setStepTip(item.isSelected());
        board.setStepTip(prop.isStepTip());
    }

    @FXML
    void showNumberClick(ActionEvent event) {
        CheckMenuItem item = (CheckMenuItem) event.getTarget();
        prop.setShowNumber(item.isSelected());
        board.setShowNumber(prop.isShowNumber());
    }

    @FXML
    void topWindowClick(ActionEvent event) {
        CheckMenuItem item = (CheckMenuItem) event.getTarget();
        prop.setTopWindow(item.isSelected());
        App.topWindow(prop.isTopWindow());
    }

    @FXML
    void linkBackModeChecked(ActionEvent event) {
        CheckMenuItem item = (CheckMenuItem) event.getTarget();
        if (linkMode.getValue()) {
            stopGraphLink();
        }
        prop.setLinkBackMode(item.isSelected());
    }

    @FXML
    void linkAnimationChecked(ActionEvent event) {
        CheckMenuItem item = (CheckMenuItem) event.getTarget();
        prop.setLinkAnimation(item.isSelected());
    }

    @FXML
    void stepSoundClick(ActionEvent event) {
        CheckMenuItem item = (CheckMenuItem) event.getTarget();
        prop.setStepSound(item.isSelected());
        board.setStepSound(prop.isStepSound());
    }

    @FXML
    void showStatusBarClick(ActionEvent event) {
        CheckMenuItem item = (CheckMenuItem) event.getTarget();
        prop.setLinkShowInfo(item.isSelected());
        statusToolBar.setVisible(item.isSelected());
        board.autoFitSize(borderPane.getWidth(), borderPane.getHeight(), splitPane.getDividerPositions()[0]);
    }

    @FXML
    public void analysisButtonClick(ActionEvent event) {
		if (isEngineNotLoaded()) return;//Tiêu đề và văn bản báo chưa nạp engine
        robotAnalysis.setValue(!robotAnalysis.getValue());
        if (robotAnalysis.getValue()) {
            robotRed.setValue(false);
            robotBlack.setValue(false);
            engineGo();
        } else {
            engineStop();
        }
        redButton.setDisable(robotAnalysis.getValue());
        blackButton.setDisable(robotAnalysis.getValue());
        immediateButton.setDisable(robotAnalysis.getValue());

        if (linkMode.getValue() && !robotAnalysis.getValue()) {
            stopGraphLink();
        }
    }

    private void engineStop() {
        if (engine != null) {
            engine.stop();
        }
    }
	public void refreshUILanguage() {
		if (borderPane.getTop() instanceof javafx.scene.control.MenuBar menuBar) {
			java.util.List<javafx.scene.control.Menu> menus = menuBar.getMenus();
			if (menus.size() > 0) {
				javafx.scene.control.Menu menuFile = menus.get(0);
				menuFile.setText(App.getBundleString("menubar.file"));
				if (menuFile.getItems().size() >= 1) {
					menuFile.getItems().get(0).setText(App.getBundleString("menuitem.exit"));
				}
			}
			if (menus.size() > 1) {
				javafx.scene.control.Menu menuPos = menus.get(1);
				menuPos.setText(App.getBundleString("menubar.position"));
				java.util.List<javafx.scene.control.MenuItem> items = menuPos.getItems();
				if (items.size() >= 8) {
					items.get(0).setText(App.getBundleString("menuitemposition.new"));
					items.get(1).setText(App.getBundleString("menuitemposition.edit"));
					items.get(2).setText(App.getBundleString("menuitemposition.copyfen"));
					items.get(3).setText(App.getBundleString("menuitemposition.pastefen"));
					items.get(4).setText(App.getBundleString("menuitemposition.copyimage"));
					items.get(5).setText(App.getBundleString("menuitemposition.pasteimage"));
					items.get(6).setText(App.getBundleString("menuitemposition.importimage"));
					items.get(7).setText(App.getBundleString("menuitemposition.exportimage"));
				}
			}
			if (menus.size() > 2) {
				javafx.scene.control.Menu menuRecord = menus.get(2);
				menuRecord.setText(App.getBundleString("menubar.gamerecord"));
				java.util.List<javafx.scene.control.MenuItem> items = menuRecord.getItems();
				if (items.size() >= 4) {
					items.get(0).setText(App.getBundleString("menuitemrecord.new"));
					items.get(1).setText(App.getBundleString("menuitemrecord.open"));
					items.get(2).setText(App.getBundleString("menuitemrecord.save"));
					items.get(3).setText(App.getBundleString("menuitemrecord.saveas"));
				}
			}
			if (menus.size() > 3) {
				javafx.scene.control.Menu menuBook = menus.get(3);
				menuBook.setText(App.getBundleString("menubar.openbook"));
				if (menuBook.getItems().size() >= 2) {
					menuBook.getItems().get(0).setText(App.getBundleString("menuitemopenbook.setting"));
					menuBook.getItems().get(1).setText(App.getBundleString("menuitemopenbook.manager"));
				}
			}
			if (menus.size() > 4) {
				javafx.scene.control.Menu menuEngine = menus.get(4);
				menuEngine.setText(App.getBundleString("menubar.engine"));
				if (menuEngine.getItems().size() >= 2) {
					menuEngine.getItems().get(0).setText(App.getBundleString("menuitemengine.manager"));
					menuEngine.getItems().get(1).setText(App.getBundleString("menuitemengine.timesetting"));
				}
			}
			if (menus.size() > 5) {
				javafx.scene.control.Menu menuConn = menus.get(5);
				menuConn.setText(App.getBundleString("menubar.connection"));
				if (menuConn.getItems().size() >= 1) {
					menuConn.getItems().get(0).setText(App.getBundleString("menuitemconnect.setting"));
				}
			}
			if (menus.size() > 6) {
				javafx.scene.control.Menu menuSettings = menus.get(6);
				menuSettings.setText(App.getBundleString("menubar.settings"));
				for (javafx.scene.control.MenuItem subItem : menuSettings.getItems()) {
					if (subItem instanceof javafx.scene.control.Menu subMenu) {
						if ("%menuitemsettings.boardstyle".equals(subMenu.getText()) || subMenu.getText().contains("Style") || subMenu.getText().contains("kiểu") || subMenu.getText().contains("主题")) {
							subMenu.setText(App.getBundleString("menuitemsettings.boardstyle"));
						} else if ("%menuitemsettings.boardsize".equals(subMenu.getText()) || subMenu.getText().contains("Size") || subMenu.getText().contains("cỡ") || subMenu.getText().contains("棋盘")) {
							subMenu.setText(App.getBundleString("menuitemsettings.boardsize"));
						}
					} else if (subItem.getOnAction() != null && subItem.getText().contains("theme")) {
						subItem.setText(App.getBundleString("menuitemsettings.theme"));
					}
				}
			}
			if (menus.size() > 7) {
				javafx.scene.control.Menu menuHelp = menus.get(7);
				menuHelp.setText(App.getBundleString("menubar.help"));
				java.util.List<javafx.scene.control.MenuItem> items = menuHelp.getItems();
				if (items.size() >= 4) {
					items.get(0).setText(App.getBundleString("menuitemhelp.home"));
					items.get(1).setText(App.getBundleString("menuitemhelp.guide"));
					items.get(2).setText(App.getBundleString("menuitemhelp.update"));
					items.get(3).setText(App.getBundleString("menuitemhelp.about"));
				}
			}
			if (menus.size() > 8) {
				menus.get(8).setText(App.getBundleString("menu.language"));
				java.util.List<javafx.scene.control.MenuItem> items = menus.get(8).getItems();
				if (items.size() >= 2) {
					items.get(0).setText(App.getBundleString("menu.lang.en"));
					items.get(1).setText(App.getBundleString("menu.lang.vi"));
				}
			}
		}
		if (chessManualHandle != null) {
			chessManualHandle.initButton(); 
		}
		initBoardContextMenu();
		setButtonTips(); 
		if (menuOfChessNotation != null) menuOfChessNotation.setText(App.getBundleString("menuitemrecord.manager"));
		if (menuOfShowTactic != null) menuOfShowTactic.setText(App.getBundleString("menuitemrecord.showmove"));
		if (menuOfLinkBackMode != null) menuOfLinkBackMode.setText(App.getBundleString("menuitemconnect.background"));
		if (menuOfLinkAnimation != null) menuOfLinkAnimation.setText(App.getBundleString("menuitemconnect.enableconfir"));
		if (menuOfStepTip != null) menuOfStepTip.setText(App.getBundleString("menuitemsettings.movehint"));
		if (menuOfStepSound != null) menuOfStepSound.setText(App.getBundleString("menuitemsettings.movesound"));
		if (menuOfShowNumber != null) menuOfShowNumber.setText(App.getBundleString("menuitemsettings.showline"));
		if (menuOfTopWindow != null) menuOfTopWindow.setText(App.getBundleString("menuitemsettings.ontop"));
		if (menuOfShowStatus != null) menuOfShowStatus.setText(App.getBundleString("menuitemsettings.showstatusbar"));
		if (menuOfDefaultBoard != null) menuOfDefaultBoard.setText(App.getBundleString("menuitemsettings.default"));
		if (menuOfCustomBoard != null) menuOfCustomBoard.setText(App.getBundleString("menuitemsettings.redblack"));
		if (menuOfSmallBoard != null) menuOfSmallBoard.setText(App.getBundleString("menuitemsettings.small"));
		if (menuOfMiddleBoard != null) menuOfMiddleBoard.setText(App.getBundleString("menuitemsettings.medium"));
		if (menuOfBigBoard != null) menuOfBigBoard.setText(App.getBundleString("menuitemsettings.large"));
		if (menuOfLargeBoard != null) menuOfLargeBoard.setText(App.getBundleString("menuitemsettings.extra"));
		if (menuOfAutoFitBoard != null) menuOfAutoFitBoard.setText(App.getBundleString("menuitemsettings.autofit"));		
		if (linkComboBox != null) {
			linkComboBox.getItems().clear();
			linkComboBox.getItems().addAll(App.getBundleString("control.autoplay"), App.getBundleString("control.background"));
			linkComboBox.setValue(App.getBundleString("control.autoplay"));
		}
		if (engineComboBox != null && engineComboBox.getParent() instanceof javafx.scene.control.ToolBar engineBar) {
			if (!engineBar.getItems().isEmpty() && engineBar.getItems().get(0) instanceof javafx.scene.control.Label engineLabel) {
				engineLabel.setText(App.getBundleString("enginepanle.text"));
			}
		}
		if (recordTable != null && recordTable.getColumns().size() >= 3) {
			recordTable.getColumns().get(0).setText(App.getBundleString("gamerecord.serial"));
			recordTable.getColumns().get(1).setText(App.getBundleString("gamerecord.move"));
			recordTable.getColumns().get(2).setText(App.getBundleString("gamerecord.score"));
		}
		if (remarkText != null && remarkText.getParent() instanceof javafx.scene.layout.BorderPane notePane) {
			if (notePane.getTop() instanceof javafx.scene.control.Label noteLabel) {
				noteLabel.setText(App.getBundleString("gamerecord.notes"));
			}
		}
		if (subRecordTable != null && subRecordTable.getParent() instanceof javafx.scene.layout.BorderPane varPane) {
			if (varPane.getTop() instanceof javafx.scene.control.Label varLabel) {
				varLabel.setText(App.getBundleString("gamerecord.variation"));
			}
		}
		if (recordTable != null) {
			javafx.scene.control.TabPane tabPane = timTabPaneCha(recordTable);
			if (tabPane != null && tabPane.getTabs().size() >= 3) {
				tabPane.getTabs().get(0).setText(App.getBundleString("gamerecord.text"));
				tabPane.getTabs().get(1).setText(App.getBundleString("paneltabletext.chart"));
				tabPane.getTabs().get(2).setText(App.getBundleString("paneltabletext.bookmove"));
			}
		}
		if (bookTable != null && bookTable.getColumns().size() >= 3) {
			bookTable.getColumns().get(0).setText(App.getBundleString("panelbookmove.move"));
			bookTable.getColumns().get(1).setText(App.getBundleString("panelbookmove.score"));
			bookTable.getColumns().get(2).setText(App.getBundleString("panelbookmove.winrate"));
			bookTable.getColumns().get(3).setText(App.getBundleString("panelbookmove.win"));
			bookTable.getColumns().get(4).setText(App.getBundleString("panelbookmove.draw"));
			bookTable.getColumns().get(5).setText(App.getBundleString("panelbookmove.loss"));
			bookTable.getColumns().get(6).setText(App.getBundleString("panelbookmove.remark"));			
			
		}		
		if (competitionNameText != null && competitionNameText.getParent() instanceof javafx.scene.layout.GridPane infoGrid) {
			for (javafx.scene.Node node : infoGrid.getChildren()) {
				if (node instanceof javafx.scene.control.Label gridLabel && javafx.scene.layout.GridPane.getColumnIndex(node) == null) {
					Integer rowIndex = javafx.scene.layout.GridPane.getRowIndex(node);
					int row = (rowIndex == null) ? 0 : rowIndex;
					switch (row) {
						case 0 -> gridLabel.setText(App.getBundleString("gameinfotext.eventname"));
						case 1 -> gridLabel.setText(App.getBundleString("gameinfotext.location"));
						case 2 -> gridLabel.setText(App.getBundleString("gameinfotext.date"));
						case 3 -> gridLabel.setText(App.getBundleString("gameinfotext.redplaye"));
						case 4 -> gridLabel.setText(App.getBundleString("gameinfotext.blackplaye"));
					}
				}
			}
		}

	}

    @FXML
    public void immediateButtonClick(ActionEvent event) {
        if (redGo && robotRed.getValue() || !redGo && robotBlack.getValue()) {
            if (engine != null) {
                engine.moveNow();
            }
        }
    }
	// Hàm chuyển đổi ngôn ngữ
    @FXML
    private void onSelectEnglish() {
        App.switchLanguage("en", "US");
		// Thêm dòng này vào hàm chuyển đổi ngôn ngữ sau khi đổi cấu hình App
		BoardContextMenu.getInstance().updateLocale();
    }
	@FXML
	private void onSelectVietnamese() {
		// 1. Chuyển đổi ngôn ngữ hệ thống sang Tiếng Việt
		App.switchLanguage("vi", "VN");
		BoardContextMenu.getInstance().updateLocale();
		// 2. Làm mới toàn bộ chữ trên giao diện (sử dụng hàm refreshUILanguage đã làm ở bước trước)
	}
    @FXML
    private void onSelectChinese() {
        App.switchLanguage("zh", "CN");
		// Thêm dòng này vào hàm chuyển đổi ngôn ngữ sau khi đổi cấu hình App
		BoardContextMenu.getInstance().updateLocale();
    }
    //Kết thúc lệnh gọi hàm chuyển đổi ngôn ngữ
    @FXML
    public void changeTacticButtonClick(ActionEvent event) {
        if (robotRed.getValue() && redGo || robotBlack.getValue() && !redGo || robotAnalysis.getValue()) {
            engineStop();
            if (tacticList == null || tacticList.size() <= 1) {
                tacticList = board.getTacticList(redGo);
            }
            if (!listView.getItems().isEmpty()) {
                for (ThinkData td : listView.getItems()) {
                    if (td.getPv() == 1) {
                        tacticList.remove(td.getDetail().get(0));
                        break;
                    }
                }
            }
            engine.setThreadNum(prop.getThreadNum());
            engine.setHashSize(prop.getHashSize());
            engine.setAnalysisModel(robotAnalysis.getValue() ? Engine.AnalysisModel.INFINITE : prop.getAnalysisModel(), prop.getAnalysisValue());
            engine.analysis(chessManualHandle.getFenCode(), chessManualHandle.getMoveList(), tacticList);
        }
    }

    @FXML
    public void blackButtonClick(ActionEvent event) {
		if (isEngineNotLoaded()) return;//Tiêu đề và văn bản báo chưa nạp engine
			robotBlack.setValue(!robotBlack.getValue());
        if (robotBlack.getValue() && !redGo) {
            engineGo();
        }
        if (!robotBlack.getValue() && !redGo) {
            engineStop();
        }

        if (linkMode.getValue() && !robotBlack.getValue()) {
            stopGraphLink();
        }
    }

    @FXML
    public void engineManageClick(ActionEvent e) {
        App.openEngineDialog();
        refreshEngineComboBox();
        if (StringUtils.isEmpty(prop.getEngineName())) {

            robotRed.setValue(false);
            robotBlack.setValue(false);
            robotAnalysis.setValue(false);

            if (engine != null) {
                engine.close();
                engine = null;
            }
        }
    }

    @FXML
    public void redButtonClick(ActionEvent event) {
		if (isEngineNotLoaded()) return; //Tiêu đề và văn bản báo chưa nạp engine
        robotRed.setValue(!robotRed.getValue());
        if (robotRed.getValue() && redGo) {
            engineGo();
        }
        if (!robotRed.getValue() && redGo) {
            engineStop();
        }

        if (linkMode.getValue() && !robotRed.getValue()) {
            stopGraphLink();
        }
    }

    private void stopGraphLink() {
        graphLinker.stop();

        engineStop();

        redButton.setDisable(false);
        robotRed.setValue(false);

        blackButton.setDisable(false);
        robotBlack.setValue(false);

        analysisButton.setDisable(false);
        robotAnalysis.setValue(false);

        linkMode.setValue(false);
    }

    private void engineGo() {
		if (isEngineNotLoaded()) return;//Tiêu đề và văn bản báo chưa nạp engine
        if (robotRed.getValue() && redGo || robotBlack.getValue() && !redGo) {
            this.isThinking = true;
        } else {
            this.isThinking = false;
        }

        tacticList = null;
        engine.setThreadNum(prop.getThreadNum());
        engine.setHashSize(prop.getHashSize());
        engine.setAnalysisModel(robotAnalysis.getValue() ? Engine.AnalysisModel.INFINITE : prop.getAnalysisModel(), prop.getAnalysisValue());
        engine.analysis(chessManualHandle.getFenCode(), chessManualHandle.getMoveList(), this.board.getBoard(), redGo);
    }

    @FXML
    public void canvasClick(MouseEvent event) {

        if (event.getButton() == MouseButton.PRIMARY) {
            String move = board.mouseClick((int) event.getX(), (int) event.getY(),
                    redGo && !robotRed.getValue(), !redGo && !robotBlack.getValue());

            if (move != null) {
                goCallBack(move);
            }

            BoardContextMenu.getInstance().hide();

        } else if (event.getButton() == MouseButton.SECONDARY) {

            BoardContextMenu.getInstance().show(this.canvas, Side.RIGHT, event.getX() - this.canvas.widthProperty().doubleValue(), event.getY());
        }

    }
    private void goCallBack(String move) {

        List<String> nextList = chessManualHandle.boardMove(move, board.translate(move, true));
        board.setManualList(nextList);

        refreshLineChart();

        redGo = !redGo;

        if (redGo && robotRed.getValue() || !redGo && robotBlack.getValue() || robotAnalysis.getValue()) {
            engineGo();
        } else {
            doOpenBook();
        }
    }

    @Override
    public void refreshLineChart() {
        List<XYChart.Data> oldList = lineChartSeries.getData();
        List<XYChart.Data> newList = chessManualHandle.getScoreList();
        int i = 0;
        while (i < oldList.size() && i < newList.size()) {
            XYChart.Data o = oldList.get(i);
            XYChart.Data n = newList.get(i);
            if (!o.getXValue().equals(n.getXValue()) || !o.getYValue().equals(n.getYValue())) {
                for (int j = oldList.size() - 1; j >= i; j--) {
                    oldList.remove(j);
                }
                break;
            }
            i++;
        }
        if (i < oldList.size()) {
            for (int j = oldList.size() - 1; j >= i; j--) {
                oldList.remove(j);
            }
        } else if (i < newList.size()) {
            oldList.addAll(newList.subList(i, newList.size()));
        }
    }

    private void doOpenBook() {
        if (useOpenBook.getValue()) {
            Thread.startVirtualThread(() -> {
                List<BookData> results = OpenBookManager.getInstance().queryBook(board.getBoard(), redGo, chessManualHandle.getP() / 2 >= Properties.getInstance().getOffManualSteps());
                this.showBookResults(results);
            });
        } else {
            this.bookTable.getItems().clear();
        }
    }

    @FXML
    public void copyButtonClick(ActionEvent e) {
        String fenCode = board.fenCode(redGo);
        ClipboardUtils.setText(fenCode);
    }

    @FXML
    public void pasteButtonClick(ActionEvent e) {
        String fenCode = ClipboardUtils.getText();
        if (StringUtils.isNotEmpty(fenCode) && fenCode.split("/").length == 10) {
            newFromOriginFen(fenCode);
        }
    }

    @FXML
    public void importImageMenuClick(ActionEvent e) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(PathUtils.getJarPath()));
        File file = fileChooser.showOpenDialog(App.getMainStage());
        if (file != null) {
            importFromImgFile(file);
        }
    }

    @FXML
    public void exportImageMenuClick(ActionEvent e) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(PathUtils.getJarPath()));
        fileChooser.setInitialFileName("tchess_export_" + DateUtils.getDateTimeString(new Date()) + ".png");
        File file = fileChooser.showSaveDialog(App.getMainStage());
        if (file != null) {
            try {
                WritableImage writableImage = new WritableImage((int) this.canvas.getWidth(), (int) this.canvas.getHeight());
                canvas.snapshot(null, writableImage);
                RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
                ImageIO.write(renderedImage, "png", file);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @FXML
    public void aboutClick(ActionEvent e) {
        DialogUtils.showInfoDialog(
			App.getBundleString("control.about"), "TCHESS"
                + System.lineSeparator() + "Built on : " + App.BUILT_ON
                + System.lineSeparator() + "Author : T"
                + System.lineSeparator() + "Version : " + App.VERSION);
    }

    @FXML
    public void upgradeClick(ActionEvent e) {
        SystemUtils.openBrowser("https://github.com/sojourners/public-Xiangqi/releases");
    }

    @FXML
    public void instructionClick(ActionEvent e) {
        SystemUtils.openBrowser("https://github.com/sojourners/public-Xiangqi/blob/master/MANUAL.md");
    }

    @FXML
    public void homeClick(ActionEvent e) {
        SystemUtils.openBrowser("https://github.com/sojourners/public-Xiangqi");
    }

    @FXML
    void localBookManageButtonClick(ActionEvent e) {
        if (App.openLocalBookDialog()) {
            OpenBookManager.getInstance().setLocalOpenBooks();
        }

    }

    @FXML
    void timeSettingButtonClick(ActionEvent e) {
        App.openTimeSetting();
    }

    @FXML
    void bookSettingButtonClick(ActionEvent e) {
        App.openBookSetting();
    }

    @FXML
    void linkSettingClick(ActionEvent e) {
        App.openLinkSetting();

    }

    @FXML
    public void reverseButtonClick(ActionEvent event) {
        isReverse.setValue(!isReverse.getValue());
        board.reverse(isReverse.getValue());
    }

    @FXML
    void colorSettingClick(ActionEvent e) {
        if (App.openColorSetting()) {
            App.refreshTheme();
            board.refresh();
        }
    }

    @FXML
    private void bookSwitchButtonClick(ActionEvent e) {
        useOpenBook.setValue(!useOpenBook.getValue());
        prop.setBookSwitch(useOpenBook.getValue());

        doOpenBook();
    }

    @FXML
    private void linkButtonClick(ActionEvent e) {

		if (isEngineNotLoaded()) return;//Tiêu đề và văn bản báo chưa nạp engine
        linkMode.setValue(!linkMode.getValue());
        if (linkMode.getValue()) {
            graphLinker.start();
        } else {
            stopGraphLink();
        }
    }

    private void initLineChart() {
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis(-1000, 1000, 500);
        xAxis.setTickLabelsVisible(false);
        xAxis.setTickMarkVisible(false);
        xAxis.setMinorTickVisible(false);
        yAxis.setTickMarkVisible(false);
        yAxis.setMinorTickVisible(false);

        LineChart<Number,Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setMinHeight(100);
        lineChart.setLegendVisible(false);
        lineChart.setCreateSymbols(false);
        lineChart.setVerticalGridLinesVisible(false);
        lineChart.getStylesheets().add(this.getClass().getResource("/style/table.css").toString());

        lineChartSeries = new XYChart.Series();
        lineChart.getData().add(lineChartSeries);

        charPane.setCenter(lineChart);
    }
    public void initialize() {
        
        prop = Properties.getInstance();
        
        listView.setCellFactory(new Callback() {
            @Override
            public Object call(Object param) {
                ListCell<ThinkData> cell = new ListCell<ThinkData>() {
                    @Override
                    protected void updateItem(ThinkData item, boolean bln) {
                        super.updateItem(item, bln);
                        if (!bln) {
                            VBox box = new VBox();

                            Label title = new Label();
                            title.setText(item.getTitle());
                            setScoreStyle(title, item.getScore());
                            box.getChildren().add(title);

                            Label body = new Label();
                            body.setText(item.getBody());
                            body.setWrapText(true);
                            body.setMaxWidth(listView.getWidth() / 1.124);//bind(listView.widthProperty().divide(1.124));
                            box.getChildren().add(body);

                            setGraphic(box);
                        }
                    }
                };
                return cell;
            }

        });

        setButtonTips();

        initChessBoard();

        initBookTable();

        initEngineView();

        initGraphLinker();

        initButtonListener();
        // autofit board size listener
        initAutoFitBoardListener();
        // canvas drag listener
        initCanvasDragListener();
        // line chart
        initLineChart();
        // init chess manual
        chessManualHandle = new ChessManualHandle(chessManualPane, menuOfChessNotation, menuOfShowTactic, notationTree,
                manualTitleLabel, recordTable, subRecordTable, remarkText,
                manualBackButton, manualDeleteButton, manualDownButton, manualFinalButton,
                manualForwardButton, manualFrontButton, manualPlayButton, manualUpButton,
                openManualButton, saveManualButton, manualScoreButton, competitionNameText, competitionCityText, competitionDateText,
                competitionRedText, competitionBlackText, this);

        useOpenBook.setValue(prop.getBookSwitch());

        newChessBoard(null);

        loadEngine(prop.getEngineName());
    }

    private void importFromBufferImage(BufferedImage img) {
        char[][] result = graphLinker.findChessBoard(img);
        if (result != null) {
            if (!XiangqiUtils.validateChessBoard(result) && !DialogUtils.showConfirmDialog("提示", "检测到局面不合法，可能会导致引擎退出或者崩溃，是否继续？")) {
                return;
            }
            String fenCode = ChessBoard.fenCode(result, true);
            newFromOriginFen(fenCode);
        }
    }

    private void importFromImgFile(File f) {
        if (f.exists() && PathUtils.isImage(f.getAbsolutePath())) {
            try {
                BufferedImage img = ImageIO.read(f);
                importFromBufferImage(img);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void initCanvasDragListener() {
        this.canvas.setOnDragDropped(event -> {
            File f = event.getDragboard().getFiles().get(0);
            importFromImgFile(f);
        });
        this.canvas.setOnDragOver(event -> {
            event.acceptTransferModes(TransferMode.ANY);
            event.consume();
        });
    }

    private void initAutoFitBoardListener() {
        borderPane.widthProperty().addListener((observableValue, number, t1) -> {
            board.autoFitSize(t1.doubleValue(), borderPane.getHeight(), splitPane.getDividerPositions()[0]);
        });
        borderPane.heightProperty().addListener((observableValue, number, t1) -> {
            board.autoFitSize(borderPane.getWidth(), t1.doubleValue(), splitPane.getDividerPositions()[0]);
        });
        splitPane.getDividers().get(0).positionProperty().addListener((observableValue, number, t1) -> {
            board.autoFitSize(borderPane.getWidth(), borderPane.getHeight(), t1.doubleValue());
        });
    }

    private void initBookTable() {
        TableColumn moveCol = bookTable.getColumns().get(0);
        moveCol.setCellValueFactory(new PropertyValueFactory<BookData, String>("word"));
        TableColumn scoreCol = bookTable.getColumns().get(1);
        scoreCol.setCellValueFactory(new PropertyValueFactory<BookData, Integer>("score"));
        TableColumn winRateCol = bookTable.getColumns().get(2);
        winRateCol.setCellValueFactory(new PropertyValueFactory<BookData, Double>("winRate"));
        TableColumn winNumCol = bookTable.getColumns().get(3);
        winNumCol.setCellValueFactory(new PropertyValueFactory<BookData, Integer>("winNum"));
        TableColumn drawNumCol = bookTable.getColumns().get(4);
        drawNumCol.setCellValueFactory(new PropertyValueFactory<BookData, Integer>("drawNum"));
        TableColumn loseNumCol = bookTable.getColumns().get(5);
        loseNumCol.setCellValueFactory(new PropertyValueFactory<BookData, Integer>("loseNum"));
        TableColumn noteCol = bookTable.getColumns().get(6);
        noteCol.setCellValueFactory(new PropertyValueFactory<BookData, String>("note"));
        TableColumn sourceCol = bookTable.getColumns().get(7);
        sourceCol.setCellValueFactory(new PropertyValueFactory<BookData, String>("source"));
    }

    public void initStage() {
        borderPane.setPrefWidth(prop.getStageWidth());
        borderPane.setPrefHeight(prop.getStageHeight());
        splitPane.setDividerPosition(0, prop.getSplitPos());
        splitPane2.setDividerPosition(0, prop.getSplitPos2());
        menuOfTopWindow.setSelected(prop.isTopWindow());
        App.topWindow(prop.isTopWindow());
    }

	private void setButtonTips() {
    newButton.setTooltip(new Tooltip(App.getBundleString("control.new")));
    copyButton.setTooltip(new Tooltip(App.getBundleString("control.copy")));
    pasteButton.setTooltip(new Tooltip(App.getBundleString("control.paste")));
    regretButton.setTooltip(new Tooltip(App.getBundleString("control.undo")));
    reverseButton.setTooltip(new Tooltip(App.getBundleString("control.flipboard")));
    redButton.setTooltip(new Tooltip(App.getBundleString("control.redrobot")));
    blackButton.setTooltip(new Tooltip(App.getBundleString("control.blackrobot")));
    analysisButton.setTooltip(new Tooltip(App.getBundleString("control.analysis")));
    immediateButton.setTooltip(new Tooltip(App.getBundleString("control.movenow")));
    changeTacticButton.setTooltip(new Tooltip(App.getBundleString("control.variation")));
    linkButton.setTooltip(new Tooltip(App.getBundleString("control.connect")));
    bookSwitchButton.setTooltip(new Tooltip(App.getBundleString("control.enablebook")));
	}	
    private void initChessBoard() {
        menuOfStepTip.setSelected(prop.isStepTip());
        menuOfStepSound.setSelected(prop.isStepSound());
        menuOfLinkBackMode.setSelected(prop.isLinkBackMode());
        menuOfLinkAnimation.setSelected(prop.isLinkAnimation());
        menuOfShowNumber.setSelected(prop.isShowNumber());
        menuOfShowStatus.setSelected(prop.isLinkShowInfo());
        if (prop.getBoardSize() == ChessBoard.BoardSize.LARGE_BOARD) {
            menuOfLargeBoard.setSelected(true);
        } else if (prop.getBoardSize() == ChessBoard.BoardSize.BIG_BOARD) {
            menuOfBigBoard.setSelected(true);
        } else if (prop.getBoardSize() == ChessBoard.BoardSize.MIDDLE_BOARD) {
            menuOfMiddleBoard.setSelected(true);
        } else if (prop.getBoardSize() == ChessBoard.BoardSize.AUTOFIT_BOARD) {
            menuOfAutoFitBoard.setSelected(true);
        } else {
            menuOfSmallBoard.setSelected(true);
        }

        if (prop.getBoardStyle() == ChessBoard.BoardStyle.DEFAULT) {
            menuOfDefaultBoard.setSelected(true);
        } else {
            menuOfCustomBoard.setSelected(true);
        }

        initBoardContextMenu();

        this.infoShowLabel.prefWidthProperty().bind(statusToolBar.widthProperty().subtract(120));
        this.timeShowLabel.setText(getTimeStrategyString());
        this.statusToolBar.setVisible(prop.isLinkShowInfo());
    }

	private void initBoardContextMenu() {
		BoardContextMenu contextMenu = BoardContextMenu.getInstance();
		// Gọi hàm tự cập nhật ngôn ngữ đã tích hợp sẵn bên trong lớp BoardContextMenu
		contextMenu.updateLocale();
		// Thiết lập hành động click xử lý sự kiện
		contextMenu.setOnAction(event -> {
			MenuItem item = (MenuItem) event.getTarget();
			String itemText = item.getText();
			if (App.getBundleString("control.copyfen").equals(itemText)) {
				copyButtonClick(null);
			} else if (App.getBundleString("control.pastefen").equals(itemText)) {
				pasteButtonClick(null);
			} else if (App.getBundleString("control.switchplayer").equals(itemText)) {
				switchPlayer(true);
			} else if (App.getBundleString("control.edit").equals(itemText)) {
				editChessBoardClick(null);
			} else if (App.getBundleString("control.copyimage").equals(itemText)) {
				copyImageMenuClick(null);
			} else if (App.getBundleString("control.pasteimage").equals(itemText)) {
				pasteImageMenuClick(null);
			} else if (App.getBundleString("control.copygame").equals(itemText)) {
				chessManualHandle.copyChessManual();
			} else if (App.getBundleString("control.pastegame").equals(itemText)) {
				chessManualHandle.pasteChessManual();
			}
		});
	}

    @FXML
    public void copyImageMenuClick(ActionEvent event) {
        WritableImage writableImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
        canvas.snapshot(null, writableImage);
        BufferedImage bi =SwingFXUtils.fromFXImage(writableImage, null);
        ClipboardUtils.setImage(bi);
    }

    @FXML
    public void pasteImageMenuClick(ActionEvent event) {
        Image img = ClipboardUtils.getImage();
        if (img != null) {
            importFromBufferImage((BufferedImage) img);
        }
    }

    @FXML
    public void editChessBoardClick(ActionEvent e) {
        String fenCode = App.openEditChessBoard(board.getBoard(), redGo, isReverse.getValue());
        newFromOriginFen(fenCode);
    }

    private void newFromOriginFen(String fenCode) {
        if (StringUtils.isNotEmpty(fenCode)) {
            if (linkMode.getValue()) {
                stopGraphLink();
            }

            newChessBoard(fenCode);
            if (XiangqiUtils.isReverse(fenCode)) {
                reverseButtonClick(null);
            }
        }
    }

    private void newChessBoard(String fenCode) {
        newChessBoard(fenCode, false);
    }

    private void newChessBoard(String fenCode, boolean fromManual) {
        robotRed.setValue(false);
        redButton.setDisable(false);
        robotBlack.setValue(false);
        blackButton.setDisable(false);
        robotAnalysis.setValue(false);
        immediateButton.setDisable(false);
        isReverse.setValue(false);
        engineStop();
        board = new ChessBoard(this.canvas, prop.getBoardSize(), prop.getBoardStyle(), prop.isStepTip(), prop.isManualTip(),
                engine != null && engine.getMultiPV() > 1, prop.isStepSound(), prop.isShowNumber(), fenCode);
        redGo = StringUtils.isEmpty(fenCode) ? true : fenCode.contains("w");
        fenCode = board.fenCode(redGo);
        if (!fromManual)
            chessManualHandle.newChessManual(fenCode);
        refreshLineChart();
        listView.getItems().clear();
        this.infoShowLabel.setText("");

        doOpenBook();
        System.gc();
    }

    private void initEngineView() {
        refreshEngineComboBox();
        for (int i = 1; i <= Runtime.getRuntime().availableProcessors(); i++) {
            threadComboBox.getItems().add(String.valueOf(i));
        }
        hashComboBox.getItems().addAll("16", "32", "64", "128", "256", "512", "1024", "2048", "4096");
        threadComboBox.setValue(String.valueOf(prop.getThreadNum()));
        hashComboBox.setValue(String.valueOf(prop.getHashSize()));
    }

    private void initGraphLinker() {
        try {
            this.graphLinker = com.sun.jna.Platform.isWindows() ?
                    new WindowsGraphLinker(this) : (com.sun.jna.Platform.isLinux() ?
                    new LinuxGraphLinker(this) : new MacosGraphLinker(this));
        } catch (Exception e) {
            e.printStackTrace();
        }

		linkComboBox.getItems().addAll(
			App.getBundleString("control.autoplay"), 
			App.getBundleString("control.background")
		);
		linkComboBox.setValue(App.getBundleString("control.autoplay"));
    }

    private void refreshEngineComboBox() {
        engineComboBox.getItems().clear();
        for (EngineConfig ec : prop.getEngineConfigList()) {
            engineComboBox.getItems().add(ec.getName());
        }
        engineComboBox.setValue(prop.getEngineName());
    }

    private void initButtonListener() {
        addListener(redButton, robotRed);
        addListener(blackButton, robotBlack);
        addListener(analysisButton, robotAnalysis);
        addListener(reverseButton, isReverse);
        addListener(linkButton, linkMode);
        addListener(bookSwitchButton, useOpenBook);

        threadComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                int num = Integer.parseInt(t1);
                if (num != prop.getThreadNum()) {
                    prop.setThreadNum(num);
                }
            }
        });
        hashComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                int size = Integer.parseInt(t1);
                if (size != prop.getHashSize()) {
                    prop.setHashSize(size);
                }
            }
        });
        engineComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                if (StringUtils.isNotEmpty(t1) && !t1.equals(prop.getEngineName())) {
                    prop.setEngineName(t1);
                    robotRed.setValue(false);
                    redButton.setDisable(false);
                    robotBlack.setValue(false);
                    blackButton.setDisable(false);
                    robotAnalysis.setValue(false);
                    immediateButton.setDisable(false);
                    if (linkMode.getValue()) {
                        stopGraphLink();
                    }
                    loadEngine(t1);
                }
            }
        });
        linkComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                setLinkMode(t1);
            }
        });
    }

    private void setLinkMode(String t1) {
        if (linkMode.getValue()) {
			if (App.getBundleString("control.autoplay").equals(t1)) {			
                engineStop();
                if (isReverse.getValue()) {
                    blackButton.setDisable(false);
                    robotBlack.setValue(true);

                    redButton.setDisable(true);
                    robotRed.setValue(false);

                    analysisButton.setDisable(true);
                    robotAnalysis.setValue(false);

                    if (!redGo) {
                        engineGo();
                    }
                } else {
                    redButton.setDisable(false);
                    robotRed.setValue(true);

                    blackButton.setDisable(true);
                    robotBlack.setValue(false);

                    analysisButton.setDisable(true);
                    robotAnalysis.setValue(false);

                    if (redGo) {
                        engineGo();
                    }
                }
            } else {
                analysisButton.setDisable(false);
                robotAnalysis.setValue(true);

                blackButton.setDisable(true);
                robotBlack.setValue(false);

                redButton.setDisable(true);
                robotRed.setValue(false);

                immediateButton.setDisable(true);

                engineGo();
            }
        }
    }

    private void addListener(Button button, ObjectProperty property) {
        property.addListener((ChangeListener<Boolean>) (observableValue, aBoolean, t1) -> {
            setButtonSelected(button, t1);
        });
        setButtonSelected(button, Boolean.TRUE.equals(property.getValue()));
    }

    private void setButtonSelected(Button button, boolean selected) {
        String selectedStylesheet = this.getClass().getResource("/style/selected-button.css").toString();
        if (selected) {
            if (!button.getStylesheets().contains(selectedStylesheet)) {
                button.getStylesheets().add(selectedStylesheet);
            }
            if (!button.getStyleClass().contains("selected-state")) {
                button.getStyleClass().add("selected-state");
            }
        } else {
            button.getStylesheets().remove(selectedStylesheet);
            button.getStyleClass().remove("selected-state");
        }
    }

    private void loadEngine(String name) {
        try {
            if (StringUtils.isNotEmpty(name)) {
                for (EngineConfig ec : prop.getEngineConfigList()) {
                    if (name.equals(ec.getName())) {
                        if (engine != null) {
                            engine.close();
                        }
                        engine = new Engine(ec, this);
                        board.showMultiPV(engine.getMultiPV() > 1);
                        return;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void trickAutoClick(ChessBoard.Step step) {
        if (step != null) {
            int x1 = step.getStart().getX(), y1 = step.getStart().getY();
            int x2 = step.getEnd().getX(), y2 = step.getEnd().getY();
            if (robotBlack.getValue()) {
                y1 = 9 - y1;
                y2 = 9 - y2;
                x1 = 8 - x1;
                x2 = 8 - x2;
            }
            graphLinker.autoClick(x1, y1, x2, y2);
        }
        this.isThinking = false;
    }

    @Override
    public void bestMove(String first, String second) {
        if (redGo && robotRed.getValue() || !redGo && robotBlack.getValue()) {
            ChessBoard.Step s = board.stepForBoard(first);

            Platform.runLater(() -> {
                board.move(s.getStart().getX(), s.getStart().getY(), s.getEnd().getX(), s.getEnd().getY());
                board.setTip(second, null, 1);

                goCallBack(first);
            });

            if (linkMode.getValue()) {
                trickAutoClick(s);
            }
        }
    }

    @Override
    public void thinkDetail(ThinkData td) {
        if (redGo && robotRed.getValue() || !redGo && robotBlack.getValue() || robotAnalysis.getValue()) {
            td.generate(redGo, isReverse.getValue(), board);
            if (td.getValid()) {
                Platform.runLater(() -> {
                    listView.getItems().addFirst(td);
                    if (listView.getItems().size() > 128) {
                        listView.getItems().removeLast();
                    }

                    if (prop.isLinkShowInfo()) {
                        infoShowLabel.setText(td.getTitle() + " | " + td.getBody());
                        setScoreStyle(infoShowLabel, td.getScore());
                        timeShowLabel.setText(getTimeStrategyString());
                    }

                    board.setTip(td.getDetail().get(0), td.getDetail().size() > 1 ? td.getDetail().get(1) : null, td.getPv());

                    if (td.getPv() == 1) {
                        chessManualHandle.setScore(td.getScore(), td.getMate());
                    }
                });
            }
        }
    }

    private String getTimeStrategyString() {
        switch (prop.getAnalysisModel()) {
            case Engine.AnalysisModel.FIXED_TIME:	
				return App.getBundleString("control.fixtime") + " " + (prop.getAnalysisValue() / 1000d) + " " + App.getBundleString("control.ms");
            case Engine.AnalysisModel.FIXED_STEPS:
				return App.getBundleString("control.fixdepth") + " " + prop.getAnalysisValue() + App.getBundleString("control.layer");
            case Engine.AnalysisModel.FIXED_NODES:
                long nodes = prop.getAnalysisValue();
                if (nodes > 1000) {
                    nodes /= 1000;
					return App.getBundleString("control.fixnode") + " " + nodes + App.getBundleString("control.knode");
                } else {
					return App.getBundleString("control.fixnode") + " " + nodes + App.getBundleString("control.units");
                }
            default:
                return "";
        }
    }

    private void setScoreStyle(Label label, double score) {
        label.getStyleClass().removeAll("positive-score", "negative-score");
        label.getStyleClass().add(score >= 0 ? "positive-score" : "negative-score");
    }

    @Override
    public void showBookResults(List<BookData> list) {
        this.bookTable.getItems().clear();
        for (BookData bd : list) {
            String move = bd.getMove();
            bd.setWord(board.translate(move, false));
            this.bookTable.getItems().add(bd);
        }
    }

    @FXML
    public void bookTableClick(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
            if (redGo && !robotRed.getValue() || !redGo && !robotBlack.getValue() ||robotAnalysis.getValue()) {
                BookData bd = bookTable.getSelectionModel().getSelectedItem();
                if (bd == null) {
                    return;
                }
                Platform.runLater(() -> {
                    board.move(bd.getMove());
                    goCallBack(bd.getMove());
                });
            }
        }
    }

    @FXML
    public void exit() {
        if (engine != null) {
            engine.close();
        }

        OpenBookManager.getInstance().close();
        graphLinker.stop();
        prop.setStageWidth(borderPane.getWidth());
        prop.setStageHeight(borderPane.getHeight());
        prop.setSplitPos(splitPane.getDividerPositions()[0]);
        prop.setSplitPos2(splitPane2.getDividerPositions()[0]);
        prop.save();
        Platform.exit();
    }

    @Override
    public void linkerInitChessBoard(String fenCode, boolean isReverse) {
        Platform.runLater(() -> {
            newChessBoard(fenCode);
            if (isReverse) {
                reverseButtonClick(null);
            }
            setLinkMode(linkComboBox.getValue());
        });
    }

    @Override
    public char[][] getEngineBoard() {
        return board.getBoard();
    }

    @Override
    public boolean isThinking() {
        return this.isThinking;
    }

    @Override
    public boolean isWatchMode() {
        return "观战模式".equals(linkComboBox.getValue());
    }

    @Override
    public void linkerMove(int x1, int y1, int x2, int y2) {
        Platform.runLater(() -> {
            String move = board.move(x1, y1, x2, y2);
            if (move != null) {
                boolean red = XiangqiUtils.isRed(board.getBoard()[y2][x2]);
                if (isWatchMode() && (!redGo && red || redGo && !red)) {
                    System.out.println(move + "," + red + ", " + redGo);
                    switchPlayer(false);
                } else {
                    goCallBack(move);
                }
            }
        });
    }

    private void switchPlayer(boolean f) {
        engineStop();
        graphLinker.pause();
        boolean tmpRed = robotRed.getValue(), tmpBlack = robotBlack.getValue(), tmpAnalysis = robotAnalysis.getValue(), tmpLink = linkMode.getValue(), tmpReverse = isReverse.getValue();
        String fenCode = board.fenCode(f ? !redGo : redGo);
        newChessBoard(fenCode);
        isReverse.setValue(tmpReverse);
        board.reverse(tmpReverse);
        robotRed.setValue(tmpRed);
        robotBlack.setValue(tmpBlack);
        robotAnalysis.setValue(tmpAnalysis);
        linkMode.setValue(tmpLink);
        graphLinker.resume();
        if (robotRed.getValue() && redGo || robotBlack.getValue() && !redGo || robotAnalysis.getValue()) {
            engineGo();
        }
    }


    private ChessManualHandle chessManualHandle;
    @FXML
    private BorderPane chessManualPane;
    @FXML
    private CheckMenuItem menuOfChessNotation;
    @FXML
    private CheckMenuItem menuOfShowTactic;
    @FXML
    private TreeView notationTree;
    @FXML
    private Label manualTitleLabel;
    @FXML
    private ListView subRecordTable;
    @FXML
    private TextArea remarkText;
    @FXML
    private Button manualBackButton;
    @FXML
    private Button manualDeleteButton;
    @FXML
    private Button manualDownButton;
    @FXML
    private Button manualFinalButton;
    @FXML
    private Button manualForwardButton;
    @FXML
    private Button manualFrontButton;
    @FXML
    private Button manualPlayButton;
    @FXML
    private Button manualUpButton;
    @FXML
    private Button openManualButton;
    @FXML
    private Button saveManualButton;
    @FXML
    private Button manualScoreButton;
    @FXML
    private TextField competitionNameText;
    @FXML
    private TextField competitionCityText;
    @FXML
    private TextField competitionDateText;
    @FXML
    private TextField competitionRedText;
    @FXML
    private TextField competitionBlackText;

    @FXML
    void menuOfShowTacticClick(ActionEvent event) {
        CheckMenuItem item = (CheckMenuItem) event.getTarget();
        prop.setManualTip(item.isSelected());
        board.setManualTip(item.isSelected());
    }
    @FXML
    void openChessManualFolder(ActionEvent event) {
        chessManualHandle.openChessNotationFolder(event);
    }
    @FXML
    void deleteButtonClick(ActionEvent event) {
        checkLinkMode();
        chessManualHandle.deleteButtonClick(event);
    }
    @FXML
    void scoreButtonClick(ActionEvent event) {
		if (isEngineNotLoaded()) return;//Tiêu đề và văn bản báo chưa nạp engine		
        checkLinkMode();
        chessManualHandle.scoreButtonClick(event);
    }
    @FXML
    void playButtonClick(ActionEvent event) {
        checkLinkMode();
        chessManualHandle.playButtonClick(event);
    }
    @FXML
    void downwardButtonClick(ActionEvent event) {
        checkLinkMode();
        chessManualHandle.manualButtonClick(8);
    }
    @FXML
    void upwardButtonClick(ActionEvent event) {
        checkLinkMode();
        chessManualHandle.manualButtonClick(7);
    }

    @Override
    public void turnOnAnalysisMode() {
        if (!robotAnalysis.getValue()) {
            analysisButtonClick(null);
        }
    }

    @Override
    public void turnOffAnalysisMode() {
        if (robotAnalysis.getValue()) {
            analysisButtonClick(null);
        }
    }

    @Override
    public void newChessBoardFromManual(String fenCode) {
        newChessBoard(fenCode, true);
    }

    @Override
    public void browseChessRecord(String fenCode, List<String> moveList, boolean redGo, List<String> nextList) {
        checkLinkMode();
        board.browseChessRecord(fenCode, moveList);
        board.setManualList(nextList);
        this.redGo = redGo;
        refreshLineChart();
        if (robotRed.getValue() && robotBlack.getValue()) {
            robotRed.setValue(false);
            robotBlack.setValue(false);
            engineStop();
        } else if (redGo && robotRed.getValue() || !redGo && robotBlack.getValue() || robotAnalysis.getValue()) {
            engineGo();
        } else {
            engineStop();
            doOpenBook();
        }
    }

    @Override
    public void setNextList(List<String> nextList) {
        board.setManualList(nextList);
    }

    private void checkLinkMode() {
        if (linkMode.getValue()) {
            stopGraphLink();
        }
    }

    @FXML
    void recordTableClick(MouseEvent event) {
        checkLinkMode();
        chessManualHandle.manualButtonClick(5);
    }

    @FXML
    public void backButtonClick(ActionEvent event) {
        checkLinkMode();
        chessManualHandle.manualButtonClick(2);
    }

    @FXML
    public void regretButtonClick(ActionEvent event) {
        checkLinkMode();
        if (redGo && robotRed.getValue() || !redGo && robotBlack.getValue()) {
            chessManualHandle.manualButtonClick(2);
        } else {
            chessManualHandle.manualButtonClick(6);
        }
    }

    @FXML
    void forwardButtonClick(ActionEvent event) {
        checkLinkMode();
        chessManualHandle.manualButtonClick(3);
    }

    @FXML
    void finalButtonClick(ActionEvent event) {
        checkLinkMode();
        chessManualHandle.manualButtonClick(4);
    }

    @FXML
    void frontButtonClick(ActionEvent event) {
        checkLinkMode();
        chessManualHandle.manualButtonClick(1);
    }

    @FXML
    void openChessManualFile(ActionEvent event) {
        chessManualHandle.openChessManualFile(event);
    }

    @FXML
    void saveAsChessManualFile(ActionEvent event) {
        chessManualHandle.saveAsChessManualFile(event);
    }

    @FXML
    void saveChessManualFile(ActionEvent event) {
        chessManualHandle.saveChessManualFile(event);
    }
	private javafx.scene.control.TabPane timTabPaneCha(javafx.scene.Node node) {
    javafx.scene.Parent parent = node.getParent();
    while (parent != null) {
        if (parent instanceof javafx.scene.control.TabPane tabPane) {
            return tabPane;
        }
        parent = parent.getParent();
		}
    return null;
	}


}
