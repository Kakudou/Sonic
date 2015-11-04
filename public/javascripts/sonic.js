function OpenPopupCenter(pageURL, id, width, height) {
	var left = (screen.width - width) / 2;
	var top = (screen.height - height) / 4;
	var popup = window.open(pageURL, id,
			'toolbar=no, menubar=no, scrollbars=yes,location=yes,resizable=yes, width='
					+ width + ', height=' + height + ', top=' + top + ', left='
					+ left)
}

function CheckIfInPopup() {
	window.opener.location.href = window.opener.location.href;
	if (window.opener.progressWindow) {
		window.opener.progressWindow.close()
	}
	window.close();
}

$(document).ready(function() {
	if (window.opener != null) {
		CheckIfInPopup();
	}

	$('.sonic_popup').click(function(event) {
		if ($(window).width() > 600) {
			event.preventDefault();
			OpenPopupCenter(this.href, "sonic_popup", 500, 600);
		}
	});

});