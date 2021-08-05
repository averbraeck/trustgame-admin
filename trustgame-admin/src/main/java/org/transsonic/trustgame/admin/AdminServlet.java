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
            UserUtils.handleMenu(request, click, recordNr);
            break;

        case "carrier":
        case "viewCarrier":
        case "editCarrier":
        case "saveCarrier":
        case "newCarrier":
            CarrierUtils.handleMenu(request, click, recordNr);
            break;

        case "fbreport":
        case "viewFBReport":
        case "editFBReport":
        case "saveFBReport":
        case "newFBReport":
            FBReportUtils.handleMenu(request, click, recordNr);
            break;

        case "client":
        case "viewClient":
        case "editClient":
        case "saveClient":
        case "newClient":
            ClientUtils.handleMenu(request, click, recordNr);
            break;
            
        case "organization":
        case "viewOrganization":
        case "editOrganization":
        case "saveOrganization":
        case "newOrganization":
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
            GameUtils.handleMenu(request, click, recordNr);
            break;

        case "gameplay":
            break;

        case "logging":
            break;

        default:
            break;
        }

        response.sendRedirect("jsp/admin/admin.jsp");
    }

    public static void makeColumnContent(int nrColumns, AdminData data) {
        StringBuffer s = new StringBuffer();
        s.append("<table width=\"100%\">\n");
        s.append("  <tr>");
        for (int i = 0; i < nrColumns - 1; i++) {
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

}
