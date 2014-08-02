<%@ page import="com.minedme.social.app.data.VisitorData, 
		 		 com.minedme.social.util.NetworkUtil" %>
<!doctype html>
<%
	VisitorData visitorData = (VisitorData) request.getAttribute("visitorData");
%>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
  <title>MINED!Me Game</title>
  <link id="reset-css" href="css/reset.css" rel="stylesheet" type="text/css"  />
  <link id="main-css" href="css/mm.css" rel="stylesheet" type="text/css"  />
  <link id="game-css" href="css/game.css" rel="stylesheet" type="text/css" />
  <link href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8/themes/base/jquery-ui.css" rel="stylesheet" type="text/css"/>
  <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.6.2/jquery.min.js"></script>
  <script type="text/javascript" src="js/plugins/jquery.qtip-1.0.0-rc3.min.js"></script>
  <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.15/jquery-ui.min.js"></script>
  <script type="text/javascript" src="https://www.google.com/jsapi"></script>
  <script type="text/javascript"src="js/game.js"></script>
  <script type="text/javascript">
      var last7RowData = [ <%= visitorData.getLastWeekChartData() %> ];
      var last7TotalsRowData = [ <%= visitorData.getLastWeekTotalsChartData() %> ];
      var chartColors = [];
  </script>
  <script type="text/javascript" src="js/gameCharts.js"></script>
  <script type="text/javascript" src="js/gameSetup.js"></script>
</head>
<body onLoad="startGame()">
  <div class="innerPage">
   <div class="header"><a href="/index"><img src="media/minedme-banner.jpg" /></a><br />- Report for <a href="/mine?m=<%= visitorData.getUserId() %>&d=<%= visitorData.getPublicId() %>"><%= visitorData.getFormattedActivityDate() %></a></div>
   <div class="scoreboard">
	<div class="score" style="margin-top:10px">
	  <div class="scoreContent">
        <span class="scorePoints"><%= visitorData.getVisitorCount() %></span>
        <span class="scoreLabel">Visiting Prospectors</span>
      </div>
      <div class="scoreNotes"><a href="#" title="Prospectors are anyone who visits a mine and plays the game below. Each game play increments this score."">&nbsp;?&nbsp;</a></div>
	 </div>
     <div class="score">
       <div class="scoreContent">
        <span class="scorePoints"><%= visitorData.getNetworkCount() %></span>
        <span class="scoreLabel">New Nuggets Found</span>
       </div>
        <div class="scoreNotes"><a href="#" title="Nuggets are the activities conducted on the social networks. Each original network activity increments this score.">&nbsp;?&nbsp;</a></div>
     </div>
     <div class="score" style="margin-bottom:10px">
      <div class="scoreContent">
        <span class="scorePoints"><%= visitorData.getTotalScore() %></span>
        <span class="scoreLabel">Appraisal Adjustment</span>
      </div>
      <div class="scoreNotes"><a href="#" title="The mine's appraisal adjustment represents a rise in score from nuggets, prospectors and winning game play.">&nbsp;?&nbsp;</a></div>
     </div>
   </div>
	 <div class="description">Use the charts below to your advantage while guessing yesterday's usage. Contribute to this mine's value by selecting one of the revolving choices for comparing against the previous day. The actual usage will be revealed after your selection. <span class="goodLuck">Good Luck!</span></div>

   <div class="charts">
   			<div class="chartsTitle">Explore the previous 7 days of usage trends.</div>
			<div id="tabs">
					<ul>
						  <li><a href="#piechart"><span>Pie</span></a></li>
						  <li><a href="#stackchart"><span>Stack</span></a></li>
						  <li><a href="#barchart"><span>Bar</span></a></li>
						  <li><a href="#linechart"><span>Line</span></a></li>
						  <li><a href="#areachart"><span>Area</span></a></li>
					</ul>
					<div id="piechart" class="chart">&nbsp;</div>
					<div id="stackchart" class="chart">&nbsp;</div>
					<div id="barchart" class="chart">&nbsp;</div>
					<div id="linechart" class="chart">&nbsp;</div>
					<div id="areachart" class="chart">&nbsp;</div>
			</div>
   </div>
   <div class="gamesWrapper">
    <div class="gamesTitle">How does yesterday compare to the day before?</div>
     <div id="accordion" class="games">
<%  
    int gameCount = 0;
	for(VisitorData.NetworkUsage networkUsage : visitorData.getNetworkUsage())
	{
		gameCount++;
%>
	<h3><a id="legend_<%= gameCount %>" href="#" class="legendEntry"><%= networkUsage.getNetworkName() %></a></h3>
	
     <div class="game" id="game_<%= gameCount %>">
        <div class="gameSubject"><a target="_blank" href="<%= networkUsage.getNetworkUrl() %>"><img src="media/<%= networkUsage.getNetworkName() %>.png" /></a></div>
        <div id="answer_<%= gameCount %>" class="gameResult newGameResult">?</div>
        <div id="operator_<%= gameCount %>" class="gameAnswer">
            <form id="gameAnswerForm_<%= gameCount %>" class="gameAnswerForm" method="POST" action="/game">
              <a id="gameAnswerFormLink_<%= gameCount %>" href="#" class="gameSubmit">&gt;</a>
              <input type="hidden" name="a" value="" />
              <input type="hidden" name="m" value="<%= visitorData.getPublicId() %>" />
              <input type="hidden" name="n" value="<%= networkUsage.getNetworkId() %>" />
            </form>
        </div>
        <div class="gameResult oldGameResult"><%= networkUsage.getPreviousCount() %></div>
       </div>
     
<%
	}
%>     
    </div>
   </div> 
 </div>
 <div class="footer">&copy; 2011 MINED!Me</div>
</body>
</html>