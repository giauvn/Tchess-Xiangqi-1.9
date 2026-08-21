package com.sojourners.chess.board;

import com.sojourners.chess.config.Properties;
import com.sojourners.chess.util.MathUtils;
import com.sojourners.chess.util.XiangqiUtils;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Rotate;

import java.util.List;


public abstract class BaseBoardRender implements BoardRender {

    private Canvas canvas;

    GraphicsContext gc;

    private static int autoPieceSize;

    public BaseBoardRender(Canvas canvas) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
    }

    public void paint(ChessBoard.BoardSize boardSize, char[][] board, ChessBoard.Step prevStep, ChessBoard.Point remark,
                      boolean stepTip, boolean showMultiPV, List<ChessBoard.MoveTip> moveTips, boolean isReverse, boolean showNumber,
                      boolean manualTip, List<ChessBoard.Step> manualList) {
        Properties prop = Properties.getInstance();
        int padding = getPadding(boardSize);
        int piece = getPieceSize(boardSize);
        int pos = padding + piece / 2;

        canvas.setWidth(2 * padding + piece * 9);
        canvas.setHeight(2 * padding + piece * 10);

        // Draw the background image
        drawBackgroundImage(canvas.getWidth(), canvas.getHeight());
        // Draw checkerboard lines
        drawBoardLine(pos, padding, piece, isReverse, boardSize);
        // Draw line sequence numbers
        if (showNumber) {
            drawBoardNum(pos, piece, isReverse, boardSize);
        }
        // Drawing the Chu-Han Boundary
        drawCenterText(pos, piece, boardSize);
        // Marker for the previous move
        if (prevStep != null) {
            drawStepRemark(pos, piece, prevStep.getStart().x, prevStep.getStart().y, true, isReverse, boardSize);
            drawStepRemark(pos, piece, prevStep.getEnd().x, prevStep.getEnd().y, true, isReverse, boardSize);
        }
        // Piece notation selected
        if (remark != null) {
            drawStepRemark(pos, piece, remark.x, remark.y, false, isReverse, boardSize);
        }
        // Draw the game pieces
        drawPieces(pos, piece, board, isReverse, boardSize);
        // Variations in a game record
        if (manualTip && manualList != null && manualList.size() > 1) {
            for (int i = manualList.size() - 1; i >= 0; i--) {
                ChessBoard.Step manual = manualList.get(i);
                drawStepTips(pos, piece, manual.getStart().x, manual.getStart().y, manual.getEnd().x, manual.getEnd().y,
                        true, i + 1, isReverse, Color.web(prop.getBranchStepColor()), prop.getBranchStepOpacity(),
                        Color.web(prop.getBranchStepNumberColor()));
            }
        }
        // Draw move hints
        if (stepTip && moveTips != null) {
            for (int i = moveTips.size() - 1; i >= 0; i--) {
                ChessBoard.MoveTip tip = moveTips.get(i);
                ChessBoard.Step second = tip.getSecond();
                if (second != null) {
                    drawStepTips(pos, piece, second.getStart().x, second.getStart().y, second.getEnd().x, second.getEnd().y,
                            showMultiPV, i + 1, isReverse, Color.web(prop.getSecondStepColor()), prop.getSecondStepOpacity(),
                            Color.web(prop.getSecondStepNumberColor()));
                }
                ChessBoard.Step first = tip.getFirst();
                if (first != null) {
                    drawStepTips(pos, piece, first.getStart().x, first.getStart().y, first.getEnd().x, first.getEnd().y,
                            showMultiPV, i + 1, isReverse, Color.web(prop.getFirstStepColor()), prop.getFirstStepOpacity(),
                            Color.web(prop.getFirstStepNumberColor()));
                }
            }
        }
    }

    // paint edit chess board demo piece
    public void paintDemoBoard(ChessBoard.BoardSize boardSize, char[][] board, ChessBoard.Point remark) {
        int piece = getPieceSize(boardSize);
        int padding = getPadding(boardSize);
        int pos = padding + piece / 2;

        canvas.setWidth(2 * padding + piece * 2);
        canvas.setHeight(2 * padding + piece * 10);

        // Draw background
        gc.setFill(getBackgroundColor());
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        // Piece notation selected
        if (remark != null) {
            drawStepRemark(pos, piece, remark.x, remark.y, true, false, boardSize);
        }
        // Draw the game pieces
        drawPieces(pos, piece, board, false, boardSize);

    }

    @Override
    public void drawCenterText(int pos, int piece, ChessBoard.BoardSize style) {
        // Drawing the Chu-Han Boundary
        double centerTextSize = getCenterTextSize(style);
        gc.setFont(Font.font(centerTextSize));
        gc.setFill(Color.BLACK);
        gc.setGlobalAlpha(0.55);
        gc.fillText("楚", pos + 2 * piece - centerTextSize, pos + 4.5 * piece + centerTextSize / 3.6);
        gc.fillText("河", pos + 3 * piece - centerTextSize, pos + 4.5 * piece + centerTextSize / 3.6);
        gc.fillText("汉", pos + 5 * piece, pos + 4.5 * piece + centerTextSize / 3.6);
        gc.fillText("界", pos + 6 * piece, pos + 4.5 * piece + centerTextSize / 3.6);
        gc.setGlobalAlpha(1);
    }

    /**
     * Get the font size for "Chu River and Han Border"
     * @return
     */
    private double getCenterTextSize(ChessBoard.BoardSize style) {
        return getPieceSize(style) / 2.5d;
    }

    @Override
    public void drawStepTips(int pos, int piece, int x1, int y1, int x2, int y2, boolean showNumber, int pv,
                             boolean isReverse, Color color, double opacity, Color numberColor) {
        x1 = pos + piece * getReverseX(x1, isReverse);
        y1 = pos + piece * getReverseY(y1, isReverse);
        x2 = pos + piece * getReverseX(x2, isReverse);
        y2 = pos + piece * getReverseY(y2, isReverse);

        gc.save();

        gc.setGlobalAlpha(opacity);
        gc.setFill(color);

        double angle = MathUtils.calculateAngle(x1, y1, x2, y2);
        Rotate r = new Rotate(angle, x1, y1);
        gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());

        int len = (int) MathUtils.calculateDistance(x1, y1, x2, y2);
        int tmpX2 = x1 - len;

        int tmpX1 = x1 - piece / 4;
        double offY = piece / 12.5, offX = piece / 4.5, h = piece / 6.5;

        // The arrow + circle are merged into one path, and only filled once to avoid overlapping and deepening the color of the overlapping area.
        gc.beginPath();
        gc.moveTo(tmpX1, y1 - offY);
        gc.lineTo(tmpX2 + offX, y1 - offY);
        gc.lineTo(tmpX2 + offX + h / 2, y1 - offY - h);
        gc.lineTo(tmpX2, y1);
        gc.lineTo(tmpX2 + offX + h / 2, y1 + offY + h);
        gc.lineTo(tmpX2 + offX, y1 + offY);
        gc.lineTo(tmpX1, y1 + offY);
        gc.closePath();

        if (showNumber) {
            double cx = x1 - piece / 8.0 - len / 2.0;
            double cy = y1;
            double rr = piece / 6d;
            gc.moveTo(cx + rr, cy);
            gc.arc(cx, cy, rr, rr, 0, 360);
        }

        gc.fill();

        gc.restore();

        // Draw the PV number inside the circle (drawn after restore; does not rotate with the arrow)
        if (showNumber) {
            gc.save();
            double rad = Math.toRadians(angle);
            double centerX = x1 - (piece / 8.0 + len / 2.0) * Math.cos(rad);
            double centerY = y1 - (piece / 8.0 + len / 2.0) * Math.sin(rad);
            double fontSize = pv < 10 ? piece / 3.6d : piece / 5d;
            gc.setFont(Font.font(fontSize));
            gc.setTextAlign(TextAlignment.CENTER);
            gc.setTextBaseline(VPos.CENTER);
            gc.setFill(numberColor);
            gc.setGlobalAlpha(1);
            gc.fillText(String.valueOf(pv), centerX, centerY);
            gc.restore();
        }
    }

    int getReverseY(int y, boolean isReverse) {
        return isReverse ? (9 - y) : y;
    }

    int getReverseX(int x, boolean isReverse) {
        return isReverse ? (8 - x) : x;
    }

    /**
     * Line width of the rectangle marking the chess move
     * @return
     */
    private double getStepRectWitdh(ChessBoard.BoardSize style) {
        return getPieceSize(style) / 25d;
    }

    @Override
    public void drawStepRemark(int pos, int piece, int x, int y, boolean isPrevStep, boolean isReverse, ChessBoard.BoardSize style) {
        x = pos + piece * getReverseX(x, isReverse);
        y = pos + piece * getReverseY(y, isReverse);

        double len = piece / 1.08;
        gc.setLineWidth(getStepRectWitdh(style));
        Color color = isPrevStep ? Color.web("#bf242a") : Color.web("#0000FF");
        gc.setStroke(color);
        gc.strokePolyline(new double[]{x - len / 2 + len / 6, x - len / 2, x - len / 2},
                new double[]{y - len / 2, y - len / 2, y - len / 2 + len / 6},
                3);
        gc.strokePolyline(new double[]{x - len / 2 + len / 6, x - len / 2, x - len / 2},
                new double[]{y + len / 2, y + len / 2, y + len / 2 - len / 6},
                3);
        gc.strokePolyline(new double[]{x + len / 2 - len / 6, x + len / 2, x + len / 2},
                new double[]{y - len / 2, y - len / 2, y - len / 2 + len / 6},
                3);
        gc.strokePolyline(new double[]{x + len / 2 - len / 6, x + len / 2, x + len / 2},
                new double[]{y + len / 2, y + len / 2, y + len / 2 - len / 6},
                3);
    }

    @Override
    public void drawBoardLine(int pos, int padding, int piece, boolean isReverse, ChessBoard.BoardSize style) {
        // Vertical and horizontal lines of the chessboard
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(getOutRectWidth(style));
        gc.setGlobalAlpha(0.75);
        gc.strokeRect(pos - padding / 2, pos - padding / 2, piece * 8 + padding, piece * 9 + padding);
        gc.setGlobalAlpha(1);
        gc.setLineWidth(getInnerRectWidth(style));
        gc.strokeRect(pos, pos, piece * 8, piece * 9);
        for (int i = 1; i < 9; i++) {
            gc.strokeLine(pos, pos + piece * i, pos + piece * 8, pos + piece * i);
        }
        for (int i = 1; i < 8; i++) {
            gc.strokeLine(pos + piece * i, pos, pos + piece * i, pos + piece * 4);
            gc.strokeLine(pos + piece * i, pos + piece * 5, pos + piece * i, pos + piece * 9);
        }
        // Diagonal of the 3x3 grid
        gc.strokeLine(pos + piece * 3, pos, pos + piece * 5, pos + piece * 2);
        gc.strokeLine(pos + piece * 3, pos + piece * 2, pos + piece * 5, pos);
        gc.strokeLine(pos + piece * 3, pos + piece * 9, pos + piece * 5, pos + piece * 7);
        gc.strokeLine(pos + piece * 3, pos + piece * 7, pos + piece * 5, pos + piece * 9);
        // Artillery position marker
        for (int i = 0; i < 9; i += 2) {
            String style1 = i == 0 ? "r" : (i == 8 ? "l" : "lr");
            drawStarPos(pos + piece * i, pos + piece * 3, piece, style1);
            drawStarPos(pos + piece * i, pos + piece * 6, piece, style1);
        }
        for (int i = 1; i < 9; i += 6) {
            drawStarPos(pos + piece * i, pos + piece * 2, piece, "lr");
            drawStarPos(pos + piece * i, pos + piece * 7, piece, "lr");
        }
    }

    public void drawBoardNum(int pos, int piece, boolean isReverse, ChessBoard.BoardSize style) {
        // Draw line sequence numbers
        double numberSize = getNumberSize(style);
        gc.setFont(Font.font(numberSize));
        gc.setFill(Color.BLACK);
        for (int i = 0; i < 9; i++) {
            // Black
            char number = (char) ('１' + i);
            double xTop = pos + i * piece - numberSize / 2, xBottom = pos + (8 - i) * piece - numberSize / 2;
            double yTop = pos - piece / 4, yBottom = pos + 9 * piece + piece / 2.3;
            gc.fillText(String.valueOf(number), isReverse ? xBottom : xTop, isReverse ? yBottom : yTop);
            // Red Team
            gc.fillText(XiangqiUtils.map.get(number), isReverse ? xTop : xBottom, isReverse ? yTop : yBottom);
        }
    }

    private void drawStarPos(int x, int y, int w, String style) {
        int offset = w / 16;
        int len = w / 6;
        if (style.contains("l")) {
            gc.strokePolyline(new double[]{x - offset - len, x - offset, x - offset},
                    new double[]{y - offset, y - offset, y - offset - len}, 3);

            gc.strokePolyline(new double[]{x - offset - len, x - offset, x - offset},
                    new double[]{y + offset, y + offset, y + offset + len}, 3);


        }
        if (style.contains("r")) {
            gc.strokePolyline(new double[]{x + offset + len, x + offset, x + offset},
                    new double[]{y - offset, y - offset, y - offset - len}, 3);

            gc.strokePolyline(new double[]{x + offset + len, x + offset, x + offset},
                    new double[]{y + offset, y + offset, y + offset + len}, 3);

        }
    }

    /**
     * Get line sequence number font size
     * @return
     */
    private double getNumberSize(ChessBoard.BoardSize style) {
        return getPieceSize(style) / 4d;
    }

    /**
     * Width of rectangular lines within the game board
     * @return
     */
    private double getInnerRectWidth(ChessBoard.BoardSize style) {
        return getOutRectWidth(style) / 2d;
    }

    /**
     * Width of the rectangular lines outside the chessboard
     * @return
     */
    private double getOutRectWidth(ChessBoard.BoardSize style) {
        return getPieceSize(style) / 40d;
    }

    /**
     *Chess piece size
     * @return
     */
    public int getPieceSize(ChessBoard.BoardSize style) {
        switch (style) {
            case LARGE_BOARD: {
                return 120;
            }
            case BIG_BOARD: {
                return 72;
            }
            case MIDDLE_BOARD: {
                return 64;
            }
            case SMALL_BOARD: {
                return 48;
            }
            case AUTOFIT_BOARD: {
                return autoPieceSize;
            }
            default: {
                return 64;
            }
        }
    }

    /**
     * Board margin
     * @return
     */
    public int getPadding(ChessBoard.BoardSize style) {
        return getPieceSize(style) / 6;
    }

    public void setAutoPieceSize(int size) {
        this.autoPieceSize = size;
    }
}
