$( document ).ready(function() {
    var script = document.createElement("script");
    script.type = "text/javascript";
    script.src = "https://maps.googleapis.com/maps/api/js?key=AIzaSyCW9cyOLKoVfgHdWkpYARyeEDDfbNxYJfg&callback=initMap";
    document.body.appendChild(script);

});

function loadAssets(map) {
    $.ajax({ url: "/assets/latest",
        success: function(data) {
            $.each(data, function(key, asset) {

                marker = new google.maps.Marker({
                    map: map,
                    animation: google.maps.Animation.DROP,
                    position: {lat: parseFloat(asset.latitude), lng: parseFloat(asset.longitude)},
                    title: asset.name
                  });
            });
        },
        error: function(jqXHR, textStatus, errorThrown) {
            console.log("Error Retrieving Assets: " + textStatus);
        }

    });
}