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
        case "newUserGroup":
        case "showUsers":
        case "saveUser":
        case "viewUser":
        case "editUser":
        case "newUser":
        case "readUsers":
        case "generateUsers":
            data.setMenuChoice("user") ;
            UserUtils.handleMenu(request, click, recordNr);
            break;

        case "carrier":
        case "viewCarrier":
        case "editCarrier":
        case "saveCarrier":
        case "newCarrier":
            data.setMenuChoice("carrier") ;
            CarrierUtils.handleMenu(request, click, recordNr);
            break;

        case "fbreport":
        case "viewFBReport":
        case "editFBReport":
        case "saveFBReport":
        case "newFBReport":
            data.setMenuChoice("fbreport") ;
            FBReportUtils.handleMenu(request, click, recordNr);
            break;

        case "client":
        case "viewClient":
        case "editClient":
        case "saveClient":
        case "newClient":
            data.setMenuChoice("client") ;
            ClientUtils.handleMenu(request, click, recordNr);
            break;

        case "organization":
        case "viewOrganization":
        case "editOrganization":
        case "saveOrganization":
        case "newOrganization":
            data.setMenuChoice("organization") ;
            OrganizationUtils.handleMenu(request, click, recordNr);
            break;

        case "game":
        case "viewGame":
        case "editGame":
        case "saveGame":
        case "newGame":
        case "showRounds":
        case "viewRound":
        case "editRound":
        case "saveRound":
        case "newRound":
        case "showOrders":
        case "viewOrder":
        case "editOrder":
        case "saveOrder":
        case "newOrder":
        case "showOrderCarriers":
        case "viewOrderCarrier":
        case "editOrderCarrier":
        case "saveOrderCarrier":
        case "newOrderCarrier":
            data.setMenuChoice("game") ;
            GameUtils.handleMenu(request, click, recordNr);
            break;

        case "review":
        case "showReviewRounds":
        case "showReviews":
        case "viewReview":
        case "editReview":
        case "saveReview":
        case "newReview":
        case "viewCarrierReview":
        case "editCarrierReview":
        case "saveCarrierReview":
        case "newCarrierReview":
            data.setMenuChoice("review") ;
            ReviewUtils.handleMenu(request, click, recordNr);
            break;

        case "gameplay":
        case "showGamePlay":
        case "viewGamePlay":
        case "editGamePlay":
        case "saveGamePlay":
        case "newGamePlay":
        case "showGameUsers":
        case "viewGameUser":
        case "editGameUser":
        case "saveGameUser":
        case "newGameUser":
            data.setMenuChoice("gameplay") ;
            GamePlayUtils.handleMenu(request, click, recordNr);
            break;

        case "result":
            data.setMenuChoice("result") ;
            break;

        case "logging":
            data.setMenuChoice("logging") ;
            break;

        default:
            break;
        }

        response.sendRedirect("jsp/admin/admin.jsp");
    }

    public static void makeColumnContent(AdminData data) {
        StringBuffer s = new StringBuffer();
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
        s.append("    <td width=\"");
        s.append(data.getFormColumn().getWidth());
        s.append("\">\n");
        s.append("      <div class=\"tg-admin-line-header\">");
        s.append(data.getFormColumn().getHeader());
        s.append("</div>\n");
        s.append(data.getFormColumn().getContent());
        s.append("    </td>\n");
        s.append("  </tr>");
        s.append("</table>\n");
        data.setContentHtml(s.toString());
    }

    public static String getTopMenu(AdminData data) {
        StringBuffer s = new StringBuffer();
        topmenu(data, s, "user", "Users");
        topmenu(data, s, "carrier", "Carriers");
        topmenu(data, s, "fbreport", "FB Report");
        topmenu(data, s, "client", "Clients");
        topmenu(data, s, "organization", "Organizations");
        topmenu(data, s, "game", "Games");
        topmenu(data, s, "review", "Reviews");
        topmenu(data, s, "gameplay", "GamePlay");
        topmenu(data, s, "result", "Results");
        topmenu(data, s, "logging", "Logging");
        return s.toString();
    }

    private static final String bn = "          <div class=\"tg-admin-menu-button\"";
    private static final String br = "          <div class=\"tg-admin-menu-button-red\"";

    private static void topmenu(AdminData data, StringBuffer s, String key, String text) {
        s.append(key.equals(data.getMenuChoice()) ? br : bn);
        s.append(" onclick=\"clickMenu('");
        s.append(key);
        s.append("')\">");
        s.append(text);
        s.append("</div>\n");
    }
}
