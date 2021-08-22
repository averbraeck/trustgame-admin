package org.transsonic.trustgame.admin.image;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.transsonic.trustgame.admin.AdminData;
import org.transsonic.trustgame.admin.SessionUtils;
import org.transsonic.trustgame.admin.SqlUtils;
import org.transsonic.trustgame.data.trustgame.tables.records.BriefingRecord;

@WebServlet("/imageBriefing")
public class ImageBriefing extends HttpServlet {

    /** */
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer briefingId = Integer.valueOf(request.getParameter("id").toString());
        AdminData data = SessionUtils.getData(request.getSession());
        BriefingRecord briefing = SqlUtils.readBriefingFromBriefingId(data, briefingId);
        if (briefing == null || briefing.getBriefingimage() == null)
            ImageUtil.makeResponse(response, ImageUtil.getNoImage());
        else
            ImageUtil.makeResponse(response, briefing.getBriefingimage());
    }

}
