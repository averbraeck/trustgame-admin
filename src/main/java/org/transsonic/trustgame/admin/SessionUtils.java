package org.transsonic.trustgame.admin;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.transsonic.trustgame.data.trustgame.Tables;
import org.transsonic.trustgame.data.trustgame.tables.records.GameRecord;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public final class SessionUtils {

    private SessionUtils() {
        // utility class
    }

    public static AdminData getData(final HttpSession session) {
        return (AdminData) session.getAttribute("adminData");
    }

    public static boolean checkLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (request.getSession().getAttribute("userId") == null) {
            response.sendRedirect("jsp/admin/login.jsp");
            return false;
        }
        @SuppressWarnings("unchecked")
        Map<Integer, String> idSessionMap = (Map<Integer, String>) request.getServletContext()
                .getAttribute("idSessionMap");
        String storedSessionId = idSessionMap.get(request.getSession().getAttribute("userId"));
        if (!request.getSession().getId().equals(storedSessionId)) {
            response.sendRedirect("jsp/admin/login-session.jsp"); // TODO: session management
            return false;
        }
        return true;
    }

    public static void showGames(HttpSession session, AdminData data, int selectedGameRecordNr, String showText,
            String showMethod) {
        StringBuilder s = new StringBuilder();
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        List<GameRecord> gameRecords = dslContext.selectFrom(Tables.GAME).fetch();

        s.append(AdminTable.startTable());
        for (GameRecord game : gameRecords) {
            TableRow tableRow = new TableRow(game.getId(), selectedGameRecordNr,
                    game.getCode() + " : " + game.getName(), showMethod);
            tableRow.addButton(showText, showMethod);
            s.append(tableRow.process());
        }
        s.append(AdminTable.endTable());

        data.getColumn(0).setSelectedRecordNr(selectedGameRecordNr);
        data.getColumn(0).setContent(s.toString());
    }

}
