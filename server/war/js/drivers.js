(function() {

	var App = function() {
		// this.orderId = window.location.hash.replace('#', '');
		this.getDriversUrl = './resources/drivers/';
		//this.getDriversUrl = './drivers.json';
		this.markersArray = [];

		this.$statusBar = $('#status-bar');
		this.clientMarker;
		this.initMap();
		this.initPositionUpdate();
	};

	App.prototype.initMap = function() {
		var mapOptions = {
			center : new google.maps.LatLng(47.659339, 9.166083),
			zoom : 14,
			mapTypeId : google.maps.MapTypeId.ROADMAP
		};
		this.map = new google.maps.Map(document.getElementById("map-canvas"),
				mapOptions);
		// this.updateDriverPosition(drivers[i]);
		this.geocoder = new google.maps.Geocoder();

	};

	App.prototype.initPositionUpdate = function() {
		this.getDriversData();
		setInterval(_.bind(this.getDriversData, this), 10000);
	};

	App.prototype.getDriversData = function() {
		this.showStatusBar();
		$.ajax({
			url : this.getDriversUrl

		}).then(_.bind(this.updateMap, this));
	};

	App.prototype.updateMap = function(data) {
		this.clearOverlays();
		if (data) {
			var drivers = data['driver'];
			if (drivers instanceof Array) {
				for (var i = 0; i < drivers.length; i++) {
					this.updateDriverPosition(drivers[i]);
				}
			} else {
				var driver = drivers;
				this.updateDriverPosition(driver);
			}
		}

	};

	App.prototype.updateDriverPosition = function(driver) {
		var pos = new google.maps.LatLng(driver.position.latitude,
				driver.position.longitude);
		console.log("updated Driver Position " + pos);
		var marker = new google.maps.Marker({
			map : this.map,
			title : driver.name,
			position : pos
		});
		this.markersArray.push(marker);
		
		this.hideStatusBar();
	};

	App.prototype.setClientPosition = function(address) {
		this.geocoder.geocode({
			'address' : address
		}, _.bind(function(results, status) {
			if (status == google.maps.GeocoderStatus.OK) {
				this.map.setCenter(results[0].geometry.location);
				this.clientMarker = new google.maps.Marker({
					map : this.map,
					position : results[0].geometry.location
				});
			}
		}, this));
	};

	App.prototype.showStatusBar = function() {
		console.log('showing bar');
		this.$statusBar.show();
	};

	App.prototype.hideStatusBar = function() {
		console.log('hiding bar');
		_.delay(_.bind(this.$statusBar.hide, this.$statusBar), 2000);
	};

	App.prototype.zoomToMarkers = function(orderPosition) {
		var LatLngList = [ this.clientMarker.getPosition(), orderPosition ];
		var bounds = new google.maps.LatLngBounds();
		for (var i = 0, LtLgLen = LatLngList.length; i < LtLgLen; i++) {
			bounds.extend(LatLngList[i]);
		}
		this.map.fitBounds(bounds);
	};

	App.prototype.clearOverlays = function() {
		console.log("Removing all Markers");
		for (var i = 0; i < this.markersArray.length; i++) {
			this.markersArray[i].setMap(null);
		}
		this.markersArray.length = 0;
	};

	$(document).ready(function() {
		var app = new App();
	});

}());