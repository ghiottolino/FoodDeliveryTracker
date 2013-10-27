(function() {

    var App = function() {
        this.orderId = window.location.hash.replace('#', '');
        this.getOrderUrl = '../mocks/getSingleOrder.json';
        this.clientMarker;
        this.initMap();
        this.initPositionUpdate();
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

    App.prototype.initPositionUpdate = function() {
        this.getOrderData();
        setInterval(_.bind(this.getOrderData, this), 20000);
    };

    App.prototype.getOrderData = function() {
        console.log('Updating the order position');
        $.ajax({
            url: this.getOrderUrl + this.orderId

        }).then(_.bind(this.updateMap, this));
    };

    App.prototype.updateMap = function(data) {
        if (!this.clientMarker) {
            this.setClientPosition(data.deliveryAddress);
        }
        this.updateOrderPosition(data.position);

    };

    App.prototype.updateOrderPosition = function(position) {
        var pos = new google.maps.LatLng(position.latitude, position.longitude);
        var marker = new google.maps.Marker({
            map: this.map,
            position: pos
        });
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
        }, this));
    };


    $(document).ready(function() {
        var app = new App();
    });

}());