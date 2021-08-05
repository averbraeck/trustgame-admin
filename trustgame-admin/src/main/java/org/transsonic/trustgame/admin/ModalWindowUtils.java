package org.transsonic.trustgame.admin;

public class ModalWindowUtils {

    public static void popup(AdminData data, String title, String message, String okMethod) {
        // make popup
        StringBuffer s = new StringBuffer();
        s.append("<p>");
        s.append(message);
        s.append("</p>\n");
        data.setModalWindowHtml(makeOkModalWindow(title, s.toString(), okMethod));
        data.setShowModalWindow(1);
    }

    public static String makeModalWindow(String title, String content, String onClickClose) {
        StringBuffer s = new StringBuffer();
        s.append("    <div class=\"tg-modal\">\n");
        s.append("      <div class=\"tg-modal-window\" id=\"tg-modal-window\">\n");
        s.append("        <div class=\"tg-modal-window-header\">");
        s.append("          <span class=\"tg-modal-close\" onclick=\"");
        s.append(onClickClose);
        s.append("\">");
        s.append("&times;</span>\n");
        s.append("          <p>");
        s.append(title);
        s.append("</p>\n");
        s.append("        </div>\n");
        s.append(content);
        s.append("      </div>\n");
        s.append("    </div>\n");
        s.append("    <script>");
        s.append("      dragElement(document.getElementById(\"tg-modal-window\"));");
        s.append("    </script>");
        return s.toString();
    }

    public static String makeOkModalWindow(String title, String htmlText, String okMethod) {
        StringBuffer s = new StringBuffer();
        s.append("        <div class=\"tg-modal-body\">");
        s.append("          <div class=\"tg-modal-text\">\n");
        s.append("            <p>\n");
        s.append(htmlText);
        s.append("            </p>\n");
        s.append("          <div class=\"tg-modal-button-row\">\n");
        s.append("            <div class=\"tg-button-small\" onclick=\"" + okMethod + "\">OK</div>\n");
        s.append("          </div>\n");
        s.append("        </div>\n");
        return makeModalWindow(title, s.toString(), okMethod);
    }

    public static String makeOkModalWindow(String title, String htmlText) {
        return makeOkModalWindow(title, htmlText, "clickModalWindowOk()");
    }

}
