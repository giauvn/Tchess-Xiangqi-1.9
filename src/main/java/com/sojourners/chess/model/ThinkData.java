package com.sojourners.chess.model;

import com.sojourners.chess.board.ChessBoard;
import com.sojourners.chess.App;
import java.util.List;

/**
 * 思考细节数据显示
 */
public class ThinkData {

    private Integer depth;

    private Integer score;

    private Integer mate;

    private Integer pv;

    private Long nps;

    private Long time;

    private List<String> detail;

    private String title;

    private String body;

    private Boolean isValid;

    public ThinkData() {

    }

    public void generate(boolean redGo, boolean isReverse, ChessBoard board) {
        // Import lớp com.sojourners.chess.App nếu chưa import ở đầu file
        StringBuilder sb = new StringBuilder();        
        // Đa ngôn ngữ cho "深度: "
        sb.append(com.sojourners.chess.App.getBundleString("think.depth")).append(depth).append("  ");        
        if (pv == null) pv = 1;
        sb.append("PV: ").append(pv).append("  ");        
        boolean f = false;
        if (score == null) {
            // Đa ngôn ngữ cho "绝杀: "
            sb.append(com.sojourners.chess.App.getBundleString("think.checkmate"));
            score = mate;
            f = true;
        } else {
            // Đa ngôn ngữ cho "分数: "
            sb.append(com.sojourners.chess.App.getBundleString("think.score"));
            score = score;
        }        
        if (redGo && isReverse || !redGo && !isReverse) {
            score = -score;
        }        
        // Nếu là tuyệt sát (f == true) thì thêm chữ "步  " (moves), ngược lại chỉ thêm khoảng trắng
        sb.append(score).append(f ? com.sojourners.chess.App.getBundleString("think.steps") : "  ");        
        if (nps == null) nps = 0L;
        sb.append("NPS: ").append(nps / 1000).append("K  ");        
        if (time == null) time = 0L;
        // Đa ngôn ngữ cho "时间: "
        sb.append(com.sojourners.chess.App.getBundleString("think.time")).append(String.format("%.1fs", time / 1000D));        
        title = sb.toString();
        // 生成body
        body = board.translate(detail);
        // 是否有效（处理分析模式下null数据）
        isValid = !body.contains("null");
    }

    public Boolean getValid() {
        return isValid;
    }

    public void setValid(Boolean valid) {
        isValid = valid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Integer getMate() {
        return mate;
    }

    public void setMate(Integer mate) {
        this.mate = mate;
    }

    public Integer getDepth() {
        return depth;
    }

    public void setDepth(Integer depth) {
        this.depth = depth;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Long getNps() {
        return nps;
    }

    public void setNps(Long nps) {
        this.nps = nps;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public List<String> getDetail() {
        return detail;
    }

    public void setDetail(List<String> detail) {
        this.detail = detail;
    }

    public Integer getPv() {
        return pv;
    }

    public void setPv(Integer pv) {
        this.pv = pv;
    }
}
