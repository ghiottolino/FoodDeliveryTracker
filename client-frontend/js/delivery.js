(function() {

    var App = function() {
        this.orderId = window.location.hash.replace('#', '');
        this.getOrderUrl = '../mocks/getSingleOrder.json';
        this.clientMarker;
    };

    App.prototype.initMap = function() {
        var mapOptions = {
            center: new google.maps.LatLng(-34.397, 150.644),
            zoom: 16,
            mapTypeId: google.maps.MapTypeId.ROADMAP
        };
        this.map = new google.maps.Map(document.getElementById("map-canvas"),
        mapOptions);
        this.geocoder = new google.maps.Geocoder();

    };

    App.prototype.getOrderData = function() {
        $.ajax({
            url: this.getOrderUrl + this.orderId

        }).then(_.bind(this.updateOrderPosition, this));
    };

    App.prototype.updateOrderPosition = function(data) {
        if (!this.clientMarker) {
            this.setClientPosition(data.deliveryAddress);
        }

    };

    App.prototype.setClientPosition = function(address) {
        this.geocoder.geocode({
            'address': address
        }, _.bind(function(results, status) {
            if (status == google.maps.GeocoderStatus.OK) {
                this.map.setCenter(results[0].geometry.location);
                this.clientMarker = new google.maps.Marker({
                    map: this.map,
                    position: results[0].geometry.location
                });
            }
            else {
                alert('Geocode was not successful for the following reason: ' + status);
            }
        }, this));
    };


    $(document).ready(function() {
        var app = new App();
        app.initMap();
        app.getOrderData();
    });

}());