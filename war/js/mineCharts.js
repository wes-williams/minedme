      function supportsSVG()
      {
		return !!document.createElementNS && !!document.createElementNS('http://www.w3.org/2000/svg', "svg").createSVGRect;
      }
      
      function supportsVml()
      {
    	 // return navigator.appName == 'Microsoft Internet Explorer';
    	 return false;
      }
      
      chartColors = ['1B50E0', // blue
                     '990066', // purple
                     '077814', // green
                     'FF8800', // orange
                     '000000'  // black
                     ].slice(0,yearTotalsRowData.length-1);
      
      var useNormalCharts = supportsSVG() || supportsVml();
      
      // clean data - should not reference networks
      for(var networkIndex=1;networkIndex<yearTotalsRowData.length;networkIndex++)
      {
    	  yearTotalsRowData[networkIndex][0] = '#' + networkIndex; 
    	  monthTotalsRowData[networkIndex][0] = '#' + networkIndex; 
    	  weekTotalsRowData[networkIndex][0] = '#' + networkIndex;
    	  dayTotalsRowData[networkIndex][0] = '#' + networkIndex; 
      }
      for(var networkIndex=1;networkIndex<yearRowData[0].length;networkIndex++)
      {
    	  yearRowData[0][networkIndex] = '#' + networkIndex; 
      	  monthRowData[0][networkIndex] = '#' + networkIndex; 
    	  weekRowData[0][networkIndex] = '#' + networkIndex; 
    	  dayRowData[0][networkIndex] = '#' + networkIndex;
      }

      var dayDataExist = false;
      for(var dayIndex=0;dayIndex < dayTotalsRowData.length;dayIndex++)
      {
    	  if(dayTotalsRowData[dayIndex][1] > 0)
    	  {
    		  dayDataExist=true;
    		  break;
    	  }
      }
      var weekDataExist = false;
      for(var weekIndex=0;weekIndex < weekTotalsRowData.length;weekIndex++)
      {
    	  if(weekTotalsRowData[weekIndex][1] > 0)
    	  {
    		  weekDataExist=true;
    		  break;
    	  }
      }
      var monthDataExist = false;
      for(var monthIndex=0;monthIndex < monthTotalsRowData.length;monthIndex++)
      {
    	  if(monthTotalsRowData[monthIndex][1] > 0)
    	  {
    		  monthDataExist=true;
    		  break;
    	  }
      }
      var yearDataExist = false;
      for(var yearIndex=0;yearIndex < yearTotalsRowData.length;yearIndex++)
      {
    	  if(yearTotalsRowData[yearIndex][1] > 0)
    	  {
    		  yearDataExist=true;
    		  break;
    	  }
      }
      
      
      // Load the Visualization API and the piechart package.
      if(useNormalCharts)
      {
    	  google.load('visualization', '1', {'packages':['corechart']});
      }
      else
      {
          google.load('visualization', '1', {'packages':['imagechart']});
      }
                  
    // Callback that creates and populates a data table, 
    // instantiates the pie chart, passes in the data and
    // draws it.
    function drawChart() 
    {

      // Create our data table.
      var data = new google.visualization.DataTable();

  	  // Create and populate the data table.
  	  var yearData = google.visualization.arrayToDataTable(yearRowData);
  	  var yearTotalsData = google.visualization.arrayToDataTable(yearTotalsRowData);
  	  var monthData = google.visualization.arrayToDataTable(monthRowData);
  	  var monthTotalsData = google.visualization.arrayToDataTable(monthTotalsRowData);
  	  var weekData = google.visualization.arrayToDataTable(weekRowData);
  	  var weekTotalsData = google.visualization.arrayToDataTable(weekTotalsRowData);
  	  var dayData = google.visualization.arrayToDataTable(dayRowData);
  	  var dayTotalsData = google.visualization.arrayToDataTable(dayTotalsRowData);
 
      // Instantiate and draw our chart, passing in some options.
  	  var chartAreaSize = {width:"90%",height:"80%"};
  	  var dayChartAreaSize = {width:"80%",height:"80%"};
  	  var chartHeight = 300;
  	  var chartWidth = 710;
  	  var pieWidth = 370;
  	  var dayWidth = 400;
  	  var fontPxSize = 14;
  	  
  	  
      // day charts
      if(dayDataExist)
      {
	      if(useNormalCharts)
	      {
	    	  var pieChart = new google.visualization.PieChart(document.getElementById('dayPieChart'));
	          pieChart.draw(dayTotalsData, {enableInteractivity:false, colors:chartColors, title:"", width:pieWidth, height:chartHeight, legend:'none', chartArea: dayChartAreaSize, fontSize:fontPxSize });
	      }
	      else
	      {
	    	  var pieChart = new google.visualization.ImagePieChart(document.getElementById('dayPieChart'));
	          pieChart.draw(dayTotalsData, {colors:chartColors, title:"", width:pieWidth, height:chartHeight, legend:'none', chartArea: dayChartAreaSize, fontSize:fontPxSize });
	      }
	 
	      if(useNormalCharts)
	      {
	    	  var columnChart = new google.visualization.ColumnChart(document.getElementById('dayStackChart'));
	    	  columnChart.draw(dayData, {enableInteractivity:false, colors:chartColors, title:"", isStacked:true, width:dayWidth, height:chartHeight, legend:'none', chartArea: dayChartAreaSize, fontSize:fontPxSize });
	      }
	      else
	      {
	    	  var columnChart = new google.visualization.ImageBarChart(document.getElementById('dayStackChart'));
	    	  columnChart.draw(dayData, {colors:chartColors, title:"", isStacked:true, isVertical:true, width:dayWidth, height:chartHeight, legend:'none', chartArea: dayChartAreaSize, fontSize:fontPxSize });
	      }
	      
	      if(useNormalCharts)
	      {
	    	  var columnChart = new google.visualization.ColumnChart(document.getElementById('dayBarChart'));
	    	  columnChart.draw(dayData, {enableInteractivity:false, colors:chartColors, title:"", isStacked:false, width:dayWidth, height:chartHeight, legend:'none', chartArea: dayChartAreaSize, fontSize:fontPxSize });
	      }
	      else
	      {
	    	  var columnChart = new google.visualization.ImageBarChart(document.getElementById('dayBarChart'));
	    	  columnChart.draw(dayData, {colors:chartColors, title:"", isStacked:false, isVertical:true, width:dayWidth, height:chartHeight, legend:'none', chartArea: dayChartAreaSize, fontSize:fontPxSize });
	      }
      }
      else
      {
    	  $('#dayPieChartTab').hide();
    	  $( '#pieCharts' ).tabs({ selected: 1 });
    	  $('#dayStackChartTab').hide();
    	  $( '#stackCharts' ).tabs({ selected: 1 });
    	  $('#dayBarChartTab').hide();
    	  $( '#barCharts' ).tabs({ selected: 1 });
      }
      
      if(weekDataExist)
      {
	      // weekCharts
	      if(useNormalCharts)
	      {
	    	  var pieChart = new google.visualization.PieChart(document.getElementById('weekPieChart'));
	          pieChart.draw(weekTotalsData, {enableInteractivity:false, colors:chartColors, title:"", width:pieWidth, height:chartHeight, legend:'none', chartArea: chartAreaSize, fontSize:fontPxSize });
	      }
	      else
	      {
	    	  var pieChart = new google.visualization.ImagePieChart(document.getElementById('weekPieChart'));
	          pieChart.draw(weekTotalsData, {colors:chartColors, title:"", width:pieWidth, height:chartHeight, legend:'none', chartArea: chartAreaSize, fontSize:fontPxSize });
	      }
	 
	      if(useNormalCharts)
	      {
	    	  var columnChart = new google.visualization.ColumnChart(document.getElementById('weekStackChart'));
	    	  columnChart.draw(weekData, {enableInteractivity:false, colors:chartColors, title:"", isStacked:true, width:chartWidth, height:chartHeight, legend:'none', chartArea: chartAreaSize, fontSize:fontPxSize });
	      }
	      else
	      {
	    	  var columnChart = new google.visualization.ImageBarChart(document.getElementById('weekStackChart'));
	    	  columnChart.draw(weekData, {colors:chartColors, title:"", isStacked:true, isVertical:true, width:chartWidth, height:chartHeight, legend:'none', chartArea: chartAreaSize, fontSize:fontPxSize });
	      }
	      
	      if(useNormalCharts)
	      {
	    	  var columnChart = new google.visualization.ColumnChart(document.getElementById('weekBarChart'));
	    	  columnChart.draw(weekData, {enableInteractivity:false, colors:chartColors, title:"", isStacked:false, width:chartWidth, height:chartHeight, legend:'none', chartArea: chartAreaSize, fontSize:fontPxSize });
	      }
	      else
	      {
	    	  var columnChart = new google.visualization.ImageBarChart(document.getElementById('weekBarChart'));
	    	  columnChart.draw(weekData, {colors:chartColors, title:"", isStacked:false, isVertical:true, width:chartWidth, height:chartHeight, legend:'none', chartArea: chartAreaSize, fontSize:fontPxSize });
	      }
		 
	      if(weekRowData.length > 2)
	      {
		      if(useNormalCharts)
		      {
		    	  var lineChart = new google.visualization.LineChart(document.getElementById('weekLineChart'));
		          lineChart.draw(weekData, {enableInteractivity:false, colors:chartColors, title:"", width:chartWidth, height:chartHeight, legend:'none',  chartArea: chartAreaSize, fontSize:fontPxSize });
		      }
		      else
		      {
		    	  var lineChart = new google.visualization.ImageLineChart(document.getElementById('weekLineChart'));
		          lineChart.draw(weekData, {colors:chartColors, title:"", width:chartWidth, height:chartHeight, legend:'none',  chartArea: chartAreaSize, fontSize:fontPxSize });
		      }
		
		      if(useNormalCharts)
		      {
		    	  var areaChart = new google.visualization.AreaChart(document.getElementById('weekAreaChart'));
		    	  areaChart.draw(weekData, {enableInteractivity:false, colors:chartColors, title:"", width:chartWidth, height:chartHeight, legend:'none',  chartArea: chartAreaSize, fontSize:fontPxSize });   
		      }
		      else
		      {
		    	  var areaChart = new google.visualization.ImageAreaChart(document.getElementById('weekAreaChart'));
		    	  areaChart.draw(weekData, {colors:chartColors, title:"", width:chartWidth, height:chartHeight, legend:'none',  chartArea: chartAreaSize, fontSize:fontPxSize });   
		      }  
	      }
	      else
	      {
	    	  //$("#weekLineChart").html('<div class="missingChart">Chart Not Available</div>');
	    	  //$("#weekAreaChart").html('<div class="missingChart">Chart Not Available</div>');
	    	  $('#weekLineChartTab').hide();
	    	  $( '#lineCharts' ).tabs({ selected: 1 });
	    	  $('#weekAreaChartTab').hide();
	    	  $( '#areaCharts' ).tabs({ selected: 1 });
	      }
      }
      else
      {
    	  $('#weekPieChartTab').hide();
    	  $( '#pieCharts' ).tabs({ selected: 2 });
    	  $('#weekStackChartTab').hide();
    	  $( '#stackCharts' ).tabs({ selected: 2 });
    	  $('#weekBarChartTab').hide();
    	  $( '#barCharts' ).tabs({ selected: 2 });
    	  $('#weekLineChartTab').hide();
    	  $( '#lineCharts' ).tabs({ selected: 1 });
    	  $('#weekAreaChartTab').hide();
    	  $( '#areaCharts' ).tabs({ selected: 1 });
      }
      
      if(monthDataExist)
      {
	      // month charts
	      if(useNormalCharts)
	      {
	    	  var pieChart = new google.visualization.PieChart(document.getElementById('monthPieChart'));
	          pieChart.draw(monthTotalsData, {enableInteractivity:false, colors:chartColors, title:"", width:pieWidth, height:chartHeight, legend:'none', chartArea: chartAreaSize, fontSize:fontPxSize });
	      }
	      else
	      {
	    	  var pieChart = new google.visualization.ImagePieChart(document.getElementById('monthPieChart'));
	          pieChart.draw(monthTotalsData, {colors:chartColors, title:"", width:pieWidth, height:chartHeight, legend:'none', chartArea: chartAreaSize, fontSize:fontPxSize });
	      }
	 
	      if(useNormalCharts)
	      {
	    	  var columnChart = new google.visualization.ColumnChart(document.getElementById('monthStackChart'));
	    	  columnChart.draw(monthData, {enableInteractivity:false, colors:chartColors, title:"", isStacked:true, width:chartWidth, height:chartHeight, legend:'none', chartArea: chartAreaSize, fontSize:fontPxSize });
	      }
	      else
	      {
	    	  var columnChart = new google.visualization.ImageBarChart(document.getElementById('monthStackChart'));
	    	  columnChart.draw(monthData, {colors:chartColors, title:"", isStacked:true, isVertical:true, width:chartWidth, height:chartHeight, legend:'none', chartArea: chartAreaSize, fontSize:fontPxSize });
	      }
	      
	      if(useNormalCharts)
	      {
	    	  var columnChart = new google.visualization.ColumnChart(document.getElementById('monthBarChart'));
	    	  columnChart.draw(monthData, {enableInteractivity:false, colors:chartColors, title:"", isStacked:false, width:chartWidth, height:chartHeight, legend:'none', chartArea: chartAreaSize, fontSize:fontPxSize });
	      }
	      else
	      {
	    	  var columnChart = new google.visualization.ImageBarChart(document.getElementById('monthBarChart'));
	    	  columnChart.draw(monthData, {colors:chartColors, title:"", isStacked:false, isVertical:true, width:chartWidth, height:chartHeight, legend:'none', chartArea: chartAreaSize, fontSize:fontPxSize });
	      }
		 
	      if(monthRowData.length > 2)
	      {
		      if(useNormalCharts)
		      {
		    	  var lineChart = new google.visualization.LineChart(document.getElementById('monthLineChart'));
		          lineChart.draw(monthData, {enableInteractivity:false, colors:chartColors, title:"", width:chartWidth, height:chartHeight, legend:'none',  chartArea: chartAreaSize, fontSize:fontPxSize });
		      }
		      else
		      {
		    	  var lineChart = new google.visualization.ImageLineChart(document.getElementById('monthLineChart'));
		          lineChart.draw(monthData, {colors:chartColors, title:"", width:chartWidth, height:chartHeight, legend:'none',  chartArea: chartAreaSize, fontSize:fontPxSize });
		      }
		
		      if(useNormalCharts)
		      {
		    	  var areaChart = new google.visualization.AreaChart(document.getElementById('monthAreaChart'));
		    	  areaChart.draw(monthData, {enableInteractivity:false, colors:chartColors, title:"", width:chartWidth, height:chartHeight, legend:'none',  chartArea: chartAreaSize, fontSize:fontPxSize });   
		      }
		      else
		      {
		    	  var areaChart = new google.visualization.ImageAreaChart(document.getElementById('monthAreaChart'));
		    	  areaChart.draw(monthData, {colors:chartColors, title:"", width:chartWidth, height:chartHeight, legend:'none',  chartArea: chartAreaSize, fontSize:fontPxSize });   
		      }
	      }
	      else
	      {
	    	  //$("#monthLineChart").html('<div class="missingChart">Chart Not Available</div>');
	    	  //$("#monthAreaChart").html('<div class="missingChart">Chart Not Available</div>');
	    	  $('#monthLineChartTab').hide();
	    	  $( '#lineCharts' ).tabs({ selected: 2 });
	    	  $('#monthAreaChartTab').hide();
	    	  $( '#areaCharts' ).tabs({ selected: 2 });
	      }
      }
      else
      {
    	  $('#monthPieChartTab').hide();
    	  $( '#pieCharts' ).tabs({ selected: 3 });
    	  $('#monthStackChartTab').hide();
    	  $( '#stackCharts' ).tabs({ selected: 3 });
    	  $('#monthBarChartTab').hide();
    	  $( '#barCharts' ).tabs({ selected: 3 });
    	  $('#monthLineChartTab').hide();
    	  $( '#lineCharts' ).tabs({ selected: 2 });
    	  $('#monthAreaChartTab').hide();
    	  $( '#areaCharts' ).tabs({ selected: 2 });
      }
      
      if(yearDataExist)
      {
	      if(useNormalCharts)
	      {
	    	  var pieChart = new google.visualization.PieChart(document.getElementById('yearPieChart'));
	          pieChart.draw(yearTotalsData, {enableInteractivity:false, colors:chartColors, title:"", width:pieWidth, height:chartHeight, legend:'none', chartArea: chartAreaSize, fontSize:fontPxSize });
	      }
	      else
	      {
	    	  var pieChart = new google.visualization.ImagePieChart(document.getElementById('yearPieChart'));
	          pieChart.draw(yearTotalsData, {colors:chartColors, title:"", width:pieWidth, height:chartHeight, legend:'none', chartArea: chartAreaSize, fontSize:fontPxSize });
	      }
	 
	      if(useNormalCharts)
	      {
	    	  var columnChart = new google.visualization.ColumnChart(document.getElementById('yearStackChart'));
	    	  columnChart.draw(yearData, {enableInteractivity:false, colors:chartColors, title:"", isStacked:true, width:chartWidth, height:chartHeight, legend:'none', chartArea: chartAreaSize, fontSize:fontPxSize });
	      }
	      else
	      {
	    	  var columnChart = new google.visualization.ImageBarChart(document.getElementById('yearStackChart'));
	    	  columnChart.draw(yearData, {colors:chartColors, title:"", isStacked:true, isVertical:true, width:chartWidth, height:chartHeight, legend:'none', chartArea: chartAreaSize, fontSize:fontPxSize });
	      }
	      
	      if(useNormalCharts)
	      {
	    	  var columnChart = new google.visualization.ColumnChart(document.getElementById('yearBarChart'));
	    	  columnChart.draw(yearData, {enableInteractivity:false, colors:chartColors, title:"", isStacked:false, width:chartWidth, height:chartHeight, legend:'none', chartArea: chartAreaSize, fontSize:fontPxSize });
	      }
	      else
	      {
	    	  var columnChart = new google.visualization.ImageBarChart(document.getElementById('yearBarChart'));
	    	  columnChart.draw(yearData, {colors:chartColors, title:"", isStacked:false, isVertical:true, width:chartWidth, height:chartHeight, legend:'none', chartArea: chartAreaSize, fontSize:fontPxSize });
	      }
		 
	      if(yearRowData.length > 2)
	      {
		      if(useNormalCharts)
		      {
		    	  var lineChart = new google.visualization.LineChart(document.getElementById('yearLineChart'));
		          lineChart.draw(yearData, {enableInteractivity:false, colors:chartColors, title:"", width:chartWidth, height:chartHeight, legend:'none',  chartArea: chartAreaSize, fontSize:fontPxSize });
		      }
		      else
		      {
		    	  var lineChart = new google.visualization.ImageLineChart(document.getElementById('yearLineChart'));
		          lineChart.draw(yearData, {colors:chartColors, title:"", width:chartWidth, height:chartHeight, legend:'none',  chartArea: chartAreaSize, fontSize:fontPxSize });
		      }
		
		      if(useNormalCharts)
		      {
		    	  var areaChart = new google.visualization.AreaChart(document.getElementById('yearAreaChart'));
		    	  areaChart.draw(yearData, {enableInteractivity:false, colors:chartColors, title:"", width:chartWidth, height:chartHeight, legend:'none',  chartArea: chartAreaSize, fontSize:fontPxSize });   
		      }
		      else
		      {
		    	  var areaChart = new google.visualization.ImageAreaChart(document.getElementById('yearAreaChart'));
		    	  areaChart.draw(yearData, {colors:chartColors, title:"", width:chartWidth, height:chartHeight, legend:'none',  chartArea: chartAreaSize, fontSize:fontPxSize });   
		      }
	      }
	      else
	      {
	    	  $("#yearLineChart").html('<div class="missingChart">Chart Not Available</div>');
	    	  $("#yearAreaChart").html('<div class="missingChart">Chart Not Available</div>');
	      }
      }
      else
      {
    	  $("#yearPieChart").html('<div class="missingChart">Chart Not Available</div>');
    	  $("#yearStackChart").html('<div class="missingChart">Chart Not Available</div>');
    	  $("#yearBarChart").html('<div class="missingChart">Chart Not Available</div>');
    	  $("#yearLineChart").html('<div class="missingChart">Chart Not Available</div>');
    	  $("#yearAreaChart").html('<div class="missingChart">Chart Not Available</div>');
      }
        
    }
    
    // Set a callback to run when the Google Visualization API is loaded.
    google.setOnLoadCallback(drawChart);

