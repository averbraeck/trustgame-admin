<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
 pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>TrustGame Admin Login</title>

<style>
html, body {
  font-family: Arial, Helvetica, sans-serif;
}

.tg-login-page {
  display: flex;
  flex-direction: column;
  align-items: stretch;
  position: absolute;
  top: 20px;
  bottom: 20px;
  left: 20px;
  right: 20px;
  min-height: 0px;
}

.tg-header, .tg-body, .tg-footer {
  flex-shrink: 0;
}

.tg-login-header {
  background: navy;
  padding: 5px;
  height: 50px;
  text-align: left;
  line-height: 50px;
  color: white;
  font-weight: bold;
  font-size: 2em;
  border-radius: 10px;
  margin-bottom: 20px;
}

.tg-login-header-right {
  position: absolute;
  background: white;
  margin: 2px;
  height: 46px;
  width: 300px;
  right: 20px;
  border-radius: 20px;
  flex-direction: row;
}

.tg-login-header-right > img {
  margin-left: 20px;
  margin-top: 3px;
  height: 40px;
  width: auto;
}

.tg-login-body {
  display: flex;
  flex-direction: column;
  align-items: stretch;
  min-height: 0;
  height: calc(100vh - 200px);
  width: 600px;
  margin-left: calc(50vw - 300px);
  padding-right: 20px;
  overflow-y: auto;
  overflow-x: hidden;
}

.tg-login-footer {
  background: navy;
  padding: 5px;
  height: 50px;
  line-height: 50px;
  color: white;
  font-weight: bold;
  font-size: 1em;
  text-align: right;
  border-radius: 10px;
  margin-top: 20px;
}

.tg-login-top-message {
  font-style: normal;
  font-size: 1em;
  margin-bottom: 20px;
}

.tg-login-top-message > h1 {
  font-style: normal;
  font-weight: bold;
  font-size: 1.8em;
  text-align: center;
  color: orange;
}

.tg-login-top-message > p {
  text-align: justify;
  line-height: 1.2;
}

.tg-login-bottom-message {
  font-style: normal;
  font-size: 0.8em;
  margin-top: 20px;
}

.tg-login-bottom-message > p {
  text-align: justify;
  line-height: 1.2;
}

.tg-login {
  width: 595px;
  border: 3px solid orange;
  border-radius: 10px;
  padding-top: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.tg-login-button {
  display: block;
  width: 595px;
  border: none;
  background-color: orange;
  padding: 14px 28px;
  cursor: pointer;
  text-align: center;
  font-size: 1.3em;
  font-weight: bold;
}

</style>
</head>

<body>
  <div class="tg-login-page">
    <div class="tg-login-header">
      <div class="tg-login-header-right">
        <img src="images/nwo.png" />
        <img src="images/tudelft.png" />
        <span style="font-size: 12px; padding-left: 20px; position:relative; top:-4px; color:black;">v2.4.3</span>
      </div>
    </div>
  
    <div class="tg-login-body">
    
      <div class="tg-login-top-message">
        <h1>FreightBooking Game Administration</h1> 
          <p>The following functions are available:</p>
          <ul>
            <li>Creating and maintaining carrier and client data</li>
            <li>Creating and maintaining games, game rounds and reviews</li>
            <li>Creating and maintaining game-play instances</li>
            <li>Creating and maintaining users (also in bulk)</li>
            <li>Allocating users to game-play instances</li>
            <li>Viewing and exporting results and log data</li>
          </ul>
      </div>

      <div class="tg-login">
        <form action="/trustgame-admin/login" method="post">
         <table>
           <tr>
             <td width="60px">&nbsp;</td>
             <td>UserName &nbsp; </td>
             <td><input type="text" name="username" /></td>
           </tr>
           <tr>
             <td width="150px">&nbsp;</td>
             <td>Password &nbsp; </td>
             <td><input type="password" name="password" /></td>
           </tr>
         </table>
         <br/>
         <span>
           <input type="submit" value="ADMINISTRATOR LOGIN" class="tg-login-button" />
         </span>
        </form>
      </div>
  
      <div class="tg-login-bottom-message">
        <p>This game is part of the research project Trans-SONIC 
          (<a href="https://transsonic.nl" target="_blank">https://transsonic.nl</a>) in which we 
          aim to explore the influence of trust on technology-mediated collaborations. 
        </p>
        <p> 
          This administration function has been tested with the latest versions of <b>Chrome</b>, <b>Firefox</b> and <b>Edge</b>. 
          The admin panel works best on a HD screen with a resolution of 1920 x 1080 pixels, and has <b>not</b> been
          designed for a mobile phone.
        </p>
        <p> 
          If you have any questions about the game or the research, feel free to contact 
          Anique Kuijpers at TU Delft (<a href="mailto:a.g.j.kuijpers@tudelft.nl">a.g.j.kuijpers@tudelft.nl</a>).
        </p>
      </div>
  
    </div>
    
    <div class="tg-login-footer">
      <!-- logo's at top right -->
    </div>
    
  </div>      
</body>
</html>