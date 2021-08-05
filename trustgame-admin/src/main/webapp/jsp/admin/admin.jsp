<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
  pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
  <head>
    <meta charset="ISO-8859-1">
    <title>TrustGame Administration</title>

    <link rel="stylesheet" type="text/css" href="/trustgame-admin/css/admin.css" />
    <script src="/trustgame-admin/js/admin.js"></script>

    <style>
    table, th, td {
    	border: 1px solid gray;
    	border-spacing: 0px;
    	border-collapse: collapse;
    	padding: 5px;
    	vertical-align: top;
    }
    
    body {
    	line-height: 1.2;
    }
    </style>

  </head>

  <body onload="initPage()">
    <div class="tg-page">
      <div class="tg-header">
        <span class="tg-freightbooking">TransSonic Trust Game</span>
        <span class="tg-slogan">Game Administration</span>
      </div>
      <div class="tg-header-right">
        <img src="images/nwo.png" />
        <img src="images/tudelft.png" />
        <p><a href="/trustgame-admin">LOGOUT</a></p>
      </div>
      <div class="tg-header-game-user">
        <p>&nbsp;</p>
        <p>User:&nbsp;&nbsp;&nbsp; ${adminData.getUser().getName()}</p>
      </div>

      <div class="tg-body">
      
        <div class="tg-admin-menu">
          <div class="tg-button-large tg-admin-menu-button" onclick="clickMenu('user')">Users</div>
          <div class="tg-button-large tg-admin-menu-button" onclick="clickMenu('carrier')">Carriers</div>
          <div class="tg-button-large tg-admin-menu-button" onclick="clickMenu('fbreport')">FB Report</div>
          <div class="tg-button-large tg-admin-menu-button" onclick="clickMenu('client')">Clients</div>
          <div class="tg-button-large tg-admin-menu-button" onclick="clickMenu('organization')">Organizations</div>
          <div class="tg-button-large tg-admin-menu-button" onclick="clickMenu('game')">Games</div>
          <div class="tg-button-large tg-admin-menu-button" onclick="clickMenu('gameplay')">GamePlay</div>
          <div class="tg-button-large tg-admin-menu-button" onclick="clickMenu('logging')">Logging</div>
        </div>
        <div class="tg-admin" id="tg-admin">
          ${adminData.getContentHtml()}
        </div>
        
      </div> <!-- tg-body -->
      
    </div> <!-- tg-page -->
    
    <!-- modal window for the client information within an order -->
    
    ${adminData.getModalWindowHtml()}

    <form id="clickForm" action="/trustgame-admin/admin" method="POST" style="display:none;">
      <input id="click" type="hidden" name="click" value="tobefilled" />
      <input id="recordNr" type="hidden" name="recordNr" value="0" />
    </form>

  </body>

</html>