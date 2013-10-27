(function() {

    var App = function() {
        this.orderId = window.location.hash.replace('#','');
        this.getOrderUrl = '../mocks/getAllOrders.json';
    };

    App.prototype.initMap = function() {
        var mapOptions = {
            center: new google.maps.LatLng(-34.397, 150.644),
            zoom: 8,
            mapTypeId: google.maps.MapTypeId.ROADMAP
        };
        var map = new google.maps.Map(document.getElementById("map-canvas"),
        mapOptions);

    };

    App.prototype.getOrderData = function() {
        $.ajax({
            url: this.getOrderUrl + this.orderId

        }).then(_.bind(this.updateOrderPosition, this));
    };
    
    App.prototype.updateOrderPosition = function () {
        
    };
    
    
    $(document).ready(function () {
        var app = new App();
        app.initMap();
    });

}());