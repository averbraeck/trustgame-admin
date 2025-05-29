package org.transsonic.trustgame.admin;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.xml.bind.DatatypeConverter;

import org.transsonic.trustgame.data.trustgame.tables.records.UserRecord;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/login")
public class UserLoginServlet extends HttpServlet {

    /** */
    private static final long serialVersionUID = 1L;

    @Override
    public void init() throws ServletException {
        super.init();
        System.getProperties().setProperty("org.jooq.no-logo", "true");

        // determine the connection pool, and create one if it does not yet exist (first use after server restart)
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new ServletException(e);
        }

        try {
            Context ctx = new InitialContext();
            try {
                ctx.lookup("/trustgame-admin_datasource");
            } catch (NamingException ne) {
                final HikariConfig config = new HikariConfig();
                config.setJdbcUrl("jdbc:mysql://localhost:3306/trustgame");
                config.setUsername("trustgame");
                config.setPassword("TG%s1naval%2105");
                config.setMaximumPoolSize(2);
                config.setDriverClassName("com.mysql.cj.jdbc.Driver");
                DataSource dataSource = new HikariDataSource(config);
                ctx.bind("/trustgame-admin_datasource", dataSource);
            }
        } catch (NamingException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        MessageDigest md;
        String hashedPassword;
        try {
            // https://www.baeldung.com/java-md5
            md = MessageDigest.getInstance("MD5");
            md.update(password.getBytes());
            byte[] digest = md.digest();
            hashedPassword = DatatypeConverter.printHexBinary(digest).toLowerCase();
        } catch (NoSuchAlgorithmException e1) {
            throw new ServletException(e1);
        }
        HttpSession session = request.getSession();

        AdminData data = new AdminData();
        session.setAttribute("adminData", data);
        try {
            data.setDataSource((DataSource) new InitialContext().lookup("/trustgame-admin_datasource"));
        } catch (NamingException e) {
            throw new ServletException(e);
        }

        UserRecord user = SqlUtils.readUserFromUsername(data, username);
        String userPassword = user == null ? "" : user.getPassword() == null ? "" : user.getPassword();
        if (user != null && userPassword.equals(hashedPassword) && user.getAdministrator().intValue() == 1 ) {
            data.setUsername(user.getName());
            data.setUserId(user.getId());
            data.setUser(user);
            response.sendRedirect("jsp/admin/admin.jsp");
        } else {
            session.removeAttribute("adminData");
            response.sendRedirect("jsp/admin/login.jsp");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect("jsp/admin/login.jsp");
    }

}
