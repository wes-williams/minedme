<%@ page import="com.minedme.social.app.data.MineOverviewData" %>
<!doctype html>
<%
	MineOverviewData mineData = (MineOverviewData) request.getAttribute("overview");
%>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
  <title>MINED!Me</title>
  <link id="reset-css" href="css/reset.css" rel="stylesheet" type="text/css"  />
  <link id="main-css" href="css/mm.css" rel="stylesheet" type="text/css"  />
    <link id="main-css" href="css/mine.css" rel="stylesheet" type="text/css"  />
  <link href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8/themes/base/jquery-ui.css" rel="stylesheet" type="text/css"/>
  <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.6.2/jquery.min.js"></script>
  <script type="text/javascript" src="js/plugins/jquery.qtip-1.0.0-rc3.min.js"></script>
  <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.15/jquery-ui.min.js"></script>
  <script type="text/javascript" src="https://www.google.com/jsapi"></script>
  <script type="text/javascript">
      var yearRowData = [ <%= mineData.getYearData() %> ];
      var yearTotalsRowData = [ <%= mineData.getYearTotalsData() %> ];
      var monthRowData = [ <%= mineData.getMonthData() %> ];
      var monthTotalsRowData = [ <%= mineData.getMonthTotalsData() %> ];
      var weekRowData = [ <%= mineData.getWeekData() %> ];
      var weekTotalsRowData = [ <%= mineData.getWeekTotalsData() %> ];
      var dayRowData = [ <%= mineData.getDayData() %> ];
      var dayTotalsRowData = [ <%= mineData.getDayTotalsData() %> ];

	  var legendEntries = yearRowData[0].slice(1);
      var chartColors = [];
  </script>
  <script type="text/javascript" src="js/mineCharts.js"></script>
  <script type="text/javascript">
        $(function() {
	      $( "#tabs" ).tabs();
	      $( "#pieCharts" ).tabs();
	      $( "#stackCharts" ).tabs();
	      $( "#barCharts" ).tabs();
	      $( "#lineCharts" ).tabs();
	      $( "#areaCharts" ).tabs();
       });
              
       $(function () {
    		$('a[title]').qtip({ style: { name: 'light', width:200, tip: 'topLeft', border: { radius:5 } } });
		});
		
        $(function() {
		    for(var legendIndex=0;legendIndex<chartColors.length;legendIndex++)
		    {
			  $(('#legend_' + (legendIndex+1))).css('background-color', ('#' + chartColors[legendIndex]));
			  $(('#legend_' + (legendIndex+1))).css('color', 'white');
		    }
        });
  </script>
</head>
<body>
  <div class="innerPage">
   <div class="header"><a href="/index"><img src="media/minedme-banner.jpg" /></a><br />- Report for <a href="/prospect?m=<%= mineData.getPublicId() %>"><%= mineData.getFormattedDate() %></a></div>

   <div class="scoreboard">
	<div class="score" style="margin-top:10px">
	  <div class="scoreContent">
        <span class="scorePoints"><%= mineData.getProspectorCount() %></span>
        <span class="scoreLabel">Prospectors To Date</span>
      </div>
      <div class="scoreNotes"><a href="#" title="Prospectors are anyone who visits a mine and plays the game. Each game play increments this score."">&nbsp;?&nbsp;</a></div>
	 </div>
     <div class="score">
       <div class="scoreContent">
        <span class="scorePoints"><%=  mineData.getNuggetCount() %></span>
        <span class="scoreLabel">Nuggets To Date</span>
       </div>
        <div class="scoreNotes"><a href="#" title="Nuggets are the activities conducted on the social networks. Each original network activity increments this score.">&nbsp;?&nbsp;</a></div>
     </div>
     <div class="score" style="margin-bottom:10px">
      <div class="scoreContent">
        <span class="scorePoints"><%= mineData.getAppraisalValue() %></span>
        <span class="scoreLabel">Appraisal Value</span>
      </div>
      <div class="scoreNotes"><a href="#" title="The mine's appraisal represents a score from nuggets, prospectors and winning game play.">&nbsp;?&nbsp;</a></div>
     </div>
   </div>
   <div class="legend">
<%  
    int networkCount = 0;
	for(MineOverviewData.NetworkDetail networkDetail : mineData.getNetworkDetails())
	{
		networkCount++;
%>
	<div align="center" class="legendEntry">
  		  <div class="legendEntryIcon"><a target="_blank" href="<%= networkDetail.getNetworkUrl() %>"><img src="media/<%= networkDetail.getNetworkName() %>.png" /></a></div>
  		  <div id="legend_<%= networkCount %>" class="legendEntryColor"><%= networkDetail.getNetworkName() %></div>
	</div>
<%
	}
%>   
   </div>
   <div class="charts">
			<div id="tabs">
				<ul class="nestedchartnav">
					  <li><a href="#pieCharts"><span>Pie</span></a></li>
					  <li><a href="#stackCharts"><span>Stack</span></a></li>
					  <li><a href="#barCharts"><span>Bar</span></a></li>
					  <li><a href="#lineCharts"><span>Line</span></a></li>
					  <li><a href="#areaCharts"><span>Area</span></a></li>
				</ul>
				<div id="pieCharts" class="nestedchart">
					<ul class="nestedchartnav">
						  <li id="dayPieChartTab"><a href="#dayPieChart"><span>Day</span></a></li>
						  <li id="weekPieChartTab"><a href="#weekPieChart"><span>Week</span></a></li>
						  <li id="monthPieChartTab"><a href="#monthPieChart"><span>Month</span></a></li>
						  <li id="yearPieChartTab"><a href="#yearPieChart"><span>Year</span></a></li>
					</ul>
					<div id="dayPieChart" class="chart">&nbsp;</div>
					<div id="weekPieChart" class="chart">&nbsp;</div>
					<div id="monthPieChart" class="chart">&nbsp;</div>
					<div id="yearPieChart" class="chart">&nbsp;</div>
				</div>
				<div id="stackCharts" class="nestedchart">
					<ul class="nestedchartnav">
						  <li id="dayStackChartTab"><a href="#dayStackChart"><span>Day</span></a></li>
						  <li id="weekStackChartTab"><a href="#weekStackChart"><span>Week</span></a></li>
						  <li id="monthStackChartTab"><a href="#monthStackChart"><span>Month</span></a></li>
						  <li id="yearStackChartTab"><a href="#yearStackChart"><span>Year</span></a></li>
					</ul>
					<div id="dayStackChart" class="chart">&nbsp;</div>
					<div id="weekStackChart" class="chart">&nbsp;</div>
					<div id="monthStackChart" class="chart">&nbsp;</div>
					<div id="yearStackChart" class="chart">&nbsp;</div>
				</div>
				<div id="barCharts" class="nestedchart">
					<ul class="nestedchartnav">
						  <li id="dayBarChartTab"><a href="#dayBarChart"><span>Day</span></a></li>
						  <li id="weekBarChartTab"><a href="#weekBarChart"><span>Week</span></a></li>
						  <li id="monthBarChartTab"><a href="#monthBarChart"><span>Month</span></a></li>
						  <li id="yearBarChartTab"><a href="#yearBarChart"><span>Year</span></a></li>
					</ul>
					<div id="dayBarChart" class="chart">&nbsp;</div>
					<div id="weekBarChart" class="chart">&nbsp;</div>
					<div id="monthBarChart" class="chart">&nbsp;</div>
					<div id="yearBarChart" class="chart">&nbsp;</div>
				</div>
				<div id="lineCharts" class="nestedchart">
					<ul class="nestedchartnav">
						  <li id="weekLineChartTab"><a href="#weekLineChart"><span>Week</span></a></li>
						  <li id="monthLineChartTab"><a href="#monthLineChart"><span>Month</span></a></li>
						  <li id="yearLineChartTab"><a href="#yearLineChart"><span>Year</span></a></li>
					</ul>
					<div id="weekLineChart" class="chart">&nbsp;</div>
					<div id="monthLineChart" class="chart">&nbsp;</div>
					<div id="yearLineChart" class="chart">&nbsp;</div>
				</div>
				<div id="areaCharts" class="nestedchart">
					<ul class="nestedchartnav">
						  <li id="weekAreaChartTab"><a href="#weekAreaChart"><span>Week</span></a></li>
						  <li id="monthAreaChartTab"><a href="#monthAreaChart"><span>Month</span></a></li>
						  <li id="yearAreaChartTab"><a href="#yearAreaChart"><span>Year</span></a></li>
					</ul>
					<div id="weekAreaChart" class="chart">&nbsp;</div>
					<div id="monthAreaChart" class="chart">&nbsp;</div>
					<div id="yearAreaChart" class="chart">&nbsp;</div>
				</div>
			</div>
   </div>
 </div>
 <div class="footer">&copy; 2011 MINED!Me</div>
</body>
</html>