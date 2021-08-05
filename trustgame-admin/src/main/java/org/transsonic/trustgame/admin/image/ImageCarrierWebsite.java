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
import org.transsonic.trustgame.data.trustgame.tables.records.CarrierRecord;

@WebServlet("/imageCarrierWebsite")
public class ImageCarrierWebsite extends HttpServlet {

    /** */
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer carrierId = Integer.valueOf(request.getParameter("id").toString());
        AdminData data = SessionUtils.getData(request.getSession());
        CarrierRecord carrier = SqlUtils.readCarrierFromCarrierId(data, carrierId);
        byte[] website = carrier.getCarrierwebimage();
        ImageUtil.makeResponse(response, website);
    }

    
}
