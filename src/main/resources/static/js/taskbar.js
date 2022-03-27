/*
Made by Tiago Costa
https://github.com/tdcosta100
*/

(function($)
{
	var wallpaperStyle;
	
	$("#start-menu").on("mouseleave", function()
	{
		$(this).css("display", "none");
		$("#start-button").removeClass("pressed");
		$("#files").load("fetch.php");
	});

	$("#start-button").on("mouseenter", function()
	{
		$(this).addClass("hover");
	});
	
	$("#start-button").on("mouseleave", function()
	{
		$(this).removeClass("hover");
	});
	
	$("#start-button").on("click", function()
	{
		const element = document.querySelector('#start-menu');
		$(this).toggleClass("pressed");
		(getComputedStyle(element).display == "none" ? $("#start-menu").css("display", "block") : $("#start-menu").css("display", "none"));
	});

	function changeWallpaperStyle()
	{
		switch(wallpaperStyle)
		{
			case "Center":
				wallpaperStyle = "Tile";
				
				$("#desktop").css("background-size", "auto");
				$("#desktop").css("background-position", "left top");
				$("#desktop").css("background-repeat", "repeat");
				
				break;
			case "Tile":
				wallpaperStyle = "Stretch";
				
				$("#desktop").css("background-size", "100% 100%");
				$("#desktop").css("background-position", "left top");
				$("#desktop").css("background-repeat", "no-repeat");
				
				break;
			case "Stretch":
			default:
				wallpaperStyle = "Center";
				
				$("#desktop").css("background-size", "auto");
				$("#desktop").css("background-position", "center center");
				$("#desktop").css("background-repeat", "no-repeat");
				break;
		}
		
		console.log(wallpaperStyle);
	}

	function updateClock() {
		var now = new Date() // current date
        time = now.toLocaleString([], { hour: '2-digit', minute: '2-digit' });
		document.getElementById('time').innerHTML = capitalize(time);
	
		// call this function again in 1000ms
		setTimeout(updateClock, 1000);
	}
	updateClock(); // initial call

	  function capitalize(word) {
		return word.toUpperCase();
	  }
	  
	
	
})(jQuery);

