var gameChoiceInterval;
var loadingImage;

function startGame()
{
  loadingImage = $('<img />').attr('src', 'media/loading.gif');
  gameChoiceInterval = setInterval("playGame()",2000);
}

function playGame()
{  
  var gamesLeft=0;
  var activeGameNumber=1;
  while( $("#game_" + activeGameNumber).length > 0 )
  {
	var $gameChoice = $("#gameAnswerFormLink_" + activeGameNumber);
    var $gameAnswer = $("#answer_" + activeGameNumber);
	
	if( $gameAnswer.text() == '?' )
	{		
	   gamesLeft++;
		
	   if($gameChoice.text() == '>')
	   {
	     $gameChoice.text('=');
	   }
	   else if($gameChoice.text() == '=')
	   {
	     $gameChoice.text('<');
	   }
	   else if($gameChoice.text() == '<')
	   {
	     $gameChoice.text('>');
	   }
	}

   activeGameNumber++;
  }

  if(gamesLeft == 0)
  {
    clearInterval(gameChoiceInterval);
    gameChoiceInterval=0;
  }
}


jQuery(function() 
{
  $('form a.gameSubmit').click(function(e) 
  {
	var $form = $(this).closest('form');
	var $formName = $form.attr('id');
	var $gameAnswer = $("#answer_" + $formName.substring($formName.indexOf('_')+1));
	var $gameOperator = $("#operator_" + $formName.substring($formName.indexOf('_')+1));
    var $gameChoice = $("#gameAnswerFormLink_" + $formName.substring($formName.indexOf('_')+1));

	if($gameAnswer.text() == '?')
	{
        var $hiddenAnswer = $form.children("[name=a]");

        $hiddenAnswer.val($gameChoice.text());
	    //$gameAnswer.html('<div style="text-align:center; font-size:12px; "><br /><br />Wait...</div>');
        $gameAnswer.html(loadingImage);
			
	    $.ajax( 
        {
				type : $form.attr("method"),
				url : $form.attr("action"),
				dataType : "json",
				data : $form.serialize(),
				complete : function(resp) 
				{ 			
				  var data = jQuery.parseJSON(resp.responseText);

				  $gameAnswer.text(data.answer);
				  $gameOperator.text($hiddenAnswer.val());
				  
				  if(data.correct == true)
				  {
					  $gameOperator.css('background-color','#00FF00');
				  }
				  else
				  {
					  $gameOperator.css('background-color','#FF0000');
				  }
				}
			});
		  }
	
	});
});

