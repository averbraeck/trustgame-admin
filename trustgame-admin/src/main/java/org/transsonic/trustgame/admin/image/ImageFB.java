package org.transsonic.trustgame.admin.image;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.transsonic.trustgame.admin.AdminData;
import org.transsonic.trustgame.admin.SessionUtils;
import org.transsonic.trustgame.admin.SqlUtils;
import org.transsonic.trustgame.data.trustgame.tables.records.FbreportRecord;

@WebServlet("/imageFB")
public class ImageFB extends HttpServlet {

    /** */
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer carrierId = Integer.valueOf(request.getParameter("id").toString());
        Integer imageId = Integer.valueOf(request.getParameter("image").toString());
        AdminData data = SessionUtils.getData(request.getSession());
        FbreportRecord fbReport = SqlUtils.readFBReportForCarrierId(data, carrierId);

        String classPath = getClass().getClassLoader().getResource(".").getPath().toString();
        String root = new File(classPath).getParentFile().getParentFile().getAbsolutePath();
        String imageFolder = root + "/jsp/admin/images";

        if (imageId == 1) {
            if (fbReport == null || fbReport.getServiceontime() == null) {
                byte[] image = Files.readAllBytes(new File(imageFolder + "/fb_ontime_none.png").toPath());
                ImageUtil.makeResponse(response, image);
            } else
                ImageUtil.makeResponse(response, fbReport.getServiceontime());
        } else if (imageId == 2) {
            if (fbReport == null || fbReport.getServicesatisfaction() == null) {
                byte[] image = Files.readAllBytes(new File(imageFolder + "/fb_satisfaction_none.png").toPath());
                ImageUtil.makeResponse(response, image);
            } else
                ImageUtil.makeResponse(response, fbReport.getServicesatisfaction());
        } else if (imageId == 3) {
            if (fbReport == null || fbReport.getTechnicalfleet() == null) {
                byte[] image = Files.readAllBytes(new File(imageFolder + "/fb_fleet_none.png").toPath());
                ImageUtil.makeResponse(response, image);
            } else
                ImageUtil.makeResponse(response, fbReport.getTechnicalfleet());
        } else if (imageId == 4) {
            if (fbReport == null || fbReport.getTechnicalgreen() == null) {
                byte[] image = Files.readAllBytes(new File(imageFolder + "/fb_green_none.png").toPath());
                ImageUtil.makeResponse(response, image);
            } else
                ImageUtil.makeResponse(response, fbReport.getTechnicalgreen());
        }
    }

}
