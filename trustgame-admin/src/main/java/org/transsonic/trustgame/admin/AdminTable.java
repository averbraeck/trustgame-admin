package org.transsonic.trustgame.admin;

public class AdminTable {

    public static String startTable() {
        return "      <div class=\"tg-admin-line-table\">";
    }

    public static String endTable() {
        return "      </div>\n"; // tg-admin-line-table
    }

    public static String finalButton(String text, String method) {
        StringBuffer s = new StringBuffer();
        s.append("      <div class=\"tg-admin-table-button\">");
        s.append("<a href=\"#\" onClick=\"clickRecordId('");
        s.append(method);
        s.append("',0); return false;\">");
        s.append(text);
        s.append("</a>");
        s.append("</div>\n");
        return s.toString();
    }
}
