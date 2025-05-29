package org.transsonic.trustgame.admin.image;

import java.io.IOException;

import org.transsonic.trustgame.admin.AdminData;
import org.transsonic.trustgame.admin.SessionUtils;
import org.transsonic.trustgame.admin.SqlUtils;
import org.transsonic.trustgame.data.trustgame.tables.records.ClientRecord;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/imageClient")
public class ImageClient extends HttpServlet {

    /** */
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer clientId = Integer.valueOf(request.getParameter("id").toString());
        AdminData data = SessionUtils.getData(request.getSession());
        ClientRecord client = SqlUtils.readClientFromClientId(data, clientId);
        if (client == null || client.getLogo() == null)
            ImageUtil.makeResponse(response, ImageUtil.getNoImage());
        else
            ImageUtil.makeResponse(response, client.getLogo());
    }

    
}
