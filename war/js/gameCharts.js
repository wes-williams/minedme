      function supportsSVG()
      {
		return !!document.createElementNS && !!document.createElementNS('http://www.w3.org/2000/svg', "svg").createSVGRect;
      }
      
      function supportsVml()
      {
    	  //return navigator.appName == 'Microsoft Internet Explorer';
    	  return false;
      }
      
      chartColors = ['1B50E0', // blue
                     '990066', // purple
                     '077814', // green
                     'FF8800', // orange
                     '000000'  // black
                     ].slice(0,last7TotalsRowData.length-1);
      
      var useNormalCharts = supportsSVG() || supportsVml();
      
      // clean data - should not reference networks
      for(var networkIndex=1;networkIndex<last7TotalsRowData.length;networkIndex++)
      {
    	  last7TotalsRowData[networkIndex][0] = '#' + networkIndex; 
      }
      for(var networkIndex=1;networkIndex<last7RowData[0].length;networkIndex++)
      {
    	  last7RowData[0][networkIndex] = '#' + networkIndex; 
      }
        
      var last7DataExist = false;
      for(var last7Index=0;last7Index < last7TotalsRowData.length;last7Index++)
      {
    	  if(last7TotalsRowData[last7Index][1] > 0)
    	  {
    		  last7DataExist=true;
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
    	
  	  // Create and populate the data table.
  	  var last7Data = google.visualization.arrayToDataTable(last7RowData);
  	  var last7TotalsData = google.visualization.arrayToDataTable(last7TotalsRowData);

      // Instantiate and draw our chart, passing in some options.
  	  //var chartAreaSize = {width:"80%",height:"80%"}; 
  	  var chartAreaSize = {width:"80%",height:"75%"}; // 4 digit date needs more space
  	  var chartWidth = 370;
  	  var chartHeight = 300;
  	  var fontPxSize = 14;
  	  
  	  if(last7DataExist)
  	  {
	      if(useNormalCharts)
	      {
	    	  var pieChart = new google.visualization.PieChart(document.getElementById('piechart'));
	          pieChart.draw(last7TotalsData, {enableInteractivity:false, colors:chartColors, title:"", width:chartWidth, height:chartHeight, legend:'none', chartArea: chartAreaSize, fontSize:fontPxSize });
	      }
	      else
	      {
	    	  var pieChart = new google.visualization.ImagePieChart(document.getElementById('piechart'));
	          pieChart.draw(last7TotalsData, {colors:chartColors, title:"", width:chartWidth, height:chartHeight, legend:'none', chartArea: chartAreaSize, fontSize:fontPxSize });
	      }
	 
	      if(useNormalCharts)
	      {
	    	  var columnChart = new google.visualization.ColumnChart(document.getElementById('stackchart'));
	    	  columnChart.draw(last7Data, {enableInteractivity:false, colors:chartColors, title:"", isStacked:true, width:chartWidth, height:chartHeight, legend:'none', chartArea: chartAreaSize, fontSize:fontPxSize });
	      }
	      else
	      {
	    	  var columnChart = new google.visualization.ImageBarChart(document.getElementById('stackchart'));
	    	  columnChart.draw(last7Data, {colors:chartColors, title:"", isStacked:true, isVertical:true, width:chartWidth, height:chartHeight, legend:'none', chartArea: chartAreaSize, fontSize:fontPxSize });
	      }
	      
	      if(useNormalCharts)
	      {
	    	  var columnChart = new google.visualization.ColumnChart(document.getElementById('barchart'));
	    	  columnChart.draw(last7Data, {enableInteractivity:false, colors:chartColors, title:"", isStacked:false, width:chartWidth, height:chartHeight, legend:'none', chartArea: chartAreaSize, fontSize:fontPxSize });
	      }
	      else
	      {
	    	  var columnChart = new google.visualization.ImageBarChart(document.getElementById('barchart'));
	    	  columnChart.draw(last7Data, {colors:chartColors, title:"", isStacked:false, isVertical:true, width:chartWidth, height:chartHeight, legend:'none', chartArea: chartAreaSize, fontSize:fontPxSize });
	      }
		 
	      if(useNormalCharts)
	      {
	    	  var lineChart = new google.visualization.LineChart(document.getElementById('linechart'));
	          lineChart.draw(last7Data, {enableInteractivity:false, colors:chartColors, title:"", width:chartWidth, height:chartHeight, legend:'none',  chartArea: chartAreaSize, fontSize:fontPxSize });
	      }
	      else
	      {
	    	  var lineChart = new google.visualization.ImageLineChart(document.getElementById('linechart'));
	          lineChart.draw(last7Data, {colors:chartColors, title:"", width:chartWidth, height:chartHeight, legend:'none',  chartArea: chartAreaSize, fontSize:fontPxSize });
	      }
	
	      if(useNormalCharts)
	      {
	    	  var areaChart = new google.visualization.AreaChart(document.getElementById('areachart'));
	    	  areaChart.draw(last7Data, {enableInteractivity:false, colors:chartColors, title:"", width:chartWidth, height:chartHeight, legend:'none',  chartArea: chartAreaSize, fontSize:fontPxSize });   
	      }
	      else
	      {
	    	  var areaChart = new google.visualization.ImageAreaChart(document.getElementById('areachart'));
	    	  areaChart.draw(last7Data, {colors:chartColors, title:"", width:chartWidth, height:chartHeight, legend:'none',  chartArea: chartAreaSize, fontSize:fontPxSize });   
	      }
  	  }
  	  else
  	  {
  		 $("#piechart").html('<div class="missingChart">Chart Not Available</div>');
  		 $("#stackchart").html('<div class="missingChart">Chart Not Available</div>');
    	 $("#barchart").html('<div class="missingChart">Chart Not Available</div>');
    	 $("#linechart").html('<div class="missingChart">Chart Not Available</div>');
    	 $("#areachart").html('<div class="missingChart">Chart Not Available</div>');
  	  }
    }

    // Set a callback to run when the Google Visualization API is loaded.
    google.setOnLoadCallback(drawChart);