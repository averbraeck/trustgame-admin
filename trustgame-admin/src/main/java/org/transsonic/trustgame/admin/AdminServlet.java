package org.transsonic.trustgame.admin;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/admin")
@MultipartConfig
public class AdminServlet extends HttpServlet {

    /** */
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();

        AdminData data = SessionUtils.getData(session);
        if (data == null) {
            response.sendRedirect("/trustgame-admin/login");
            return;
        }

        String click = "";
        if (request.getParameter("click") != null)
            click = request.getParameter("click").toString();
        else if (request.getParameter("editClick") != null)
            click = request.getParameter("editClick").toString();

        int recordNr = 0;
        if (request.getParameter("recordNr") != null)
            recordNr = Integer.parseInt(request.getParameter("recordNr"));
        else if (request.getParameter("editRecordNr") != null)
            recordNr = Integer.parseInt(request.getParameter("editRecordNr"));

        data.setShowModalWindow(0);
        data.setModalWindowHtml("");

        switch (click) {

        case "user":
        case "viewUserGroup":
        case "editUserGroup":
        case "saveUserGroup":
        case "deleteUserGroup":
        case "deleteUserGroupOk":
        case "newUserGroup":
        case "showUsers":
        case "saveUser":
        case "deleteUser":
        case "deleteUserOk":
        case "viewUser":
        case "editUser":
        case "newUser":
        case "readUsers":
        case "generateUsers":
        case "generateUserParameters":
            data.setMenuChoice("user");
            MaintainUser.handleMenu(request, click, recordNr);
            break;

        case "carrier":
        case "showCarrier":
        case "viewCarrier":
        case "editCarrier":
        case "saveCarrier":
        case "deleteCarrier":
        case "deleteCarrierOk":
        case "newCarrier":
            data.setMenuChoice("carrier");
            MaintainCarrier.handleMenu(request, click, recordNr);
            break;

        case "fbreport":
        case "showFBReport":
        case "viewFBReport":
        case "editFBReport":
        case "saveFBReport":
        case "deleteFBReport":
        case "deleteFBReportOk":
        case "newFBReport":
            data.setMenuChoice("fbreport");
            MaintainFBReport.handleMenu(request, click, recordNr);
            break;

        case "client":
        case "showClient":
        case "viewClient":
        case "editClient":
        case "saveClient":
        case "deleteClient":
        case "deleteClientOk":
        case "newClient":
            data.setMenuChoice("client");
            MaintainClient.handleMenu(request, click, recordNr);
            break;

        case "mission":
        case "showMission":
        case "viewMission":
        case "editMission":
        case "saveMission":
        case "deleteMission":
        case "deleteMissionOk":
        case "newMission":
            data.setMenuChoice("mission");
            MaintainMission.handleMenu(request, click, recordNr);
            break;

        case "game":
        case "viewGame":
        case "editGame":
        case "saveGame":
        case "deleteGame":
        case "deleteGameOk":
        case "newGame":
        case "cloneGame":
        case "showRounds":
        case "viewRound":
        case "editRound":
        case "saveRound":
        case "deleteRound":
        case "deleteRoundOk":
        case "newRound":
        case "showOrders":
        case "viewOrder":
        case "editOrder":
        case "saveOrder":
        case "deleteOrder":
        case "deleteOrderOk":
        case "newOrder":
        case "showOrderCarriers":
        case "viewOrderCarrier":
        case "editOrderCarrier":
        case "saveOrderCarrier":
        case "deleteOrderCarrier":
        case "deleteOrderCarrierOk":
        case "newOrderCarrier":
            data.setMenuChoice("game");
            MaintainGame.handleMenu(request, click, recordNr);
            break;

        case "review":
        case "showReviewRounds":
        case "showReviews":
        case "viewReview":
        case "editReview":
        case "saveReview":
        case "deleteReview":
        case "deleteReviewOk":
        case "newReview":
        case "viewCarrierReview":
        case "editCarrierReview":
        case "saveCarrierReview":
        case "deleteCarrierReview":
        case "deleteCarrierReviewOk":
        case "newCarrierReview":
            data.setMenuChoice("review");
            MaintainReview.handleMenu(request, click, recordNr);
            break;

        case "briefing":
        case "viewBriefing":
        case "editBriefing":
        case "saveBriefing":
        case "deleteBriefing":
        case "deleteBriefingOk":
        case "newBriefing":
            data.setMenuChoice("briefing");
            MaintainBriefing.handleMenu(request, click, recordNr);
            break;

        case "gameplay":
        case "showGamePlay":
        case "viewGamePlay":
        case "editGamePlay":
        case "saveGamePlay":
        case "deleteGamePlay":
        case "deleteGamePlayOk":
        case "newGamePlay":
        case "showGameUsers":
        case "viewGameUser":
        case "editGameUser":
        case "saveGameUser":
        case "addGameUser":
        case "removeGameUser":
        case "selectGameUserGroup":
        case "confirmGameUserGroup":
        case "addGameUserGroup":
            data.setMenuChoice("gameplay");
            MaintainGamePlay.handleMenu(request, click, recordNr);
            break;

        case "result":
        case "resultGamePlay":
        case "resultGameUsers":
        case "resultGameUserDetail":
            data.setMenuChoice("result");
            MaintainResult.handleMenu(request, response, click, recordNr);
            break;

        case "csvResultGamePlay":
        case "tsvResultGamePlay":
            data.setMenuChoice("result");
            MaintainResult.handleMenu(request, response, click, recordNr);
            return;
            
        case "logging":
        case "loggingGamePlay":
        case "loggingGameUsers":
        case "loggingGameUserLogs":
            data.setMenuChoice("logging");
            MaintainLogging.handleMenu(request, response, click, recordNr); 
            break;

        case "csvGamePlayLogs":
        case "csvGameUserLogs":
        case "tsvGamePlayLogs":
        case "tsvGameUserLogs":
            data.setMenuChoice("logging");
            MaintainLogging.handleMenu(request, response, click, recordNr); 
            return;

        default:
            break;
        }

        response.sendRedirect("jsp/admin/admin.jsp");
    }

    public static void makeColumnContent(AdminData data) {
        StringBuilder s = new StringBuilder();
        s.append("<table width=\"100%\">\n");
        s.append("  <tr>");
        for (int i = 0; i < data.getNrColumns(); i++) {
            s.append("    <td width=\"");
            s.append(data.getColumn(i).getWidth());
            s.append("\">\n");
            s.append("      <div class=\"tg-admin-line-header\">");
            s.append(data.getColumn(i).getHeader());
            s.append("</div>\n");
            s.append(data.getColumn(i).getContent());
            s.append("    </td>\n");
        }
        if (data.getFormColumn() != null) {
            s.append("    <td width=\"");
            s.append(data.getFormColumn().getWidth());
            s.append("\">\n");
            s.append("      <div class=\"tg-admin-line-header\">");
            s.append(data.getFormColumn().getHeader());
            s.append("</div>\n");
            s.append(data.getFormColumn().getContent());
            s.append("    </td>\n");
        }
        s.append("  </tr>");
        s.append("</table>\n");
        data.setContentHtml(s.toString());
    }

    public static String getTopMenu(AdminData data) {
        StringBuilder s = new StringBuilder();
        topmenu(data, s, "user", "User");
        topmenu(data, s, "carrier", "Carrier");
        topmenu(data, s, "fbreport", "FB Report");
        topmenu(data, s, "client", "Client");
        topmenu(data, s, "mission", "Mission");
        topmenu(data, s, "game", "Game");
        topmenu(data, s, "review", "Review");
        topmenu(data, s, "briefing", "Briefing");
        topmenu(data, s, "gameplay", "GamePlay");
        topmenu(data, s, "result", "Result");
        topmenu(data, s, "logging", "Logging");
        return s.toString();
    }

    private static final String bn = "          <div class=\"tg-admin-menu-button\"";
    private static final String br = "          <div class=\"tg-admin-menu-button-red\"";

    private static void topmenu(AdminData data, StringBuilder s, String key, String text) {
        s.append(key.equals(data.getMenuChoice()) ? br : bn);
        s.append(" onclick=\"clickMenu('");
        s.append(key);
        s.append("')\">");
        s.append(text);
        s.append("</div>\n");
    }
}
