
$(function() {
	$( "#tabs" ).tabs();
});

$(function() {
	$( "#accordion" ).accordion();
    
    for(var legendIndex=0;legendIndex<chartColors.length;legendIndex++)
    {
	  $(('#legend_' + (legendIndex+1))).css('background-color', ('#' + chartColors[legendIndex]));
	  $(('#legend_' + (legendIndex+1))).css('color', 'white');
	  $(('#operator_' + (legendIndex+1))).css('background-color', ('#' + chartColors[legendIndex]));
    }
});

$(function () {
    $('a[title]').qtip({ style: { name: 'light', width:200, tip: 'topLeft', border: { radius:5 } } });
});
	    