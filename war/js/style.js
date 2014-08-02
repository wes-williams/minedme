var mobile = navigator.userAgent.match(/iphone|android/i);  
if (mobile) 
{
  $("#game-css").attr("href", "css/mobile_game.css");
  $("#main-css").attr("href", "css/mobile_mm.css");
} 
else 
{
   $("#game-css").attr("href", "css/game.css");
   $("#main-css").attr("href", "css/mm.css");
}
