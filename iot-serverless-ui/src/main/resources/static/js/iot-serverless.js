var circle;
var selectedMarker;

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

                var icon = getIcon(asset.alert, false);

                marker = new google.maps.Marker({
                    map: map,
                    animation: google.maps.Animation.DROP,
                    position: {lat: parseFloat(asset.latitude), lng: parseFloat(asset.longitude)},
                    icon: icon,
                    title: asset.name
                  });

                marker['customInfo'] = asset;

                google.maps.event.addListener(marker, "click", function () {
                    displayAsset(this.customInfo);
                    drawCircle(this.customInfo, map);
                    selectMarker(this, map);
                });
            });
        },
        error: function(jqXHR, textStatus, errorThrown) {
            console.log("Error Retrieving Assets: " + textStatus);
        }

    });
}

function displayAsset(asset) {
    $(".asset-location-latitude").html("Latitude: " + parseFloat(asset.latitude).toFixed(7));
    $(".asset-location-longitude").html("Longitude: " + parseFloat(asset.longitude).toFixed(7));
    if(asset.alert == 0){
        $(".asset-within-fence").html("Within Fence: Yes");
    }
    else {
        $(".asset-within-fence").html("Within Fence: No");
    }
    $(".asset-name").html(asset.name);
    $(".asset-location").html(asset.location);

    var img = $('<img style="height: 150px; width 300px" class="navbar-brand-icon asset-image" />');
    img.attr("src", "img/"+asset.picture);
    $(".asset-image").replaceWith(img)
}

function drawCircle(asset, map) {

    if(circle) {
        circle.setMap(null);
    }
    circle = new google.maps.Circle({
        strokeColor: '#FF0000',
        strokeOpacity: 0.8,
        strokeWeight: 2,
        fillColor: '#FF0000',
        fillOpacity: 0.35,
        map: map,
        center: {lat: parseFloat(asset.centerLatitude), lng: parseFloat(asset.centerLongitude)},
        radius: parseInt(asset.geofenceRadius)
      });

      circle.setMap(map);

}

function selectMarker(marker, map) {

    if(selectedMarker != null) {
        selectedMarker.setIcon(getIcon(selectedMarker.customInfo.alert, false));
    }

    marker.setIcon(getIcon(marker.customInfo.alert, true));

    selectedMarker = marker;

}

function getIcon(alert, selected) {

    var iconUrl;
    var size;

    if(alert == 1) {
        iconUrl = "/img/icon-alert.svg";
    }
    else {
        iconUrl = "/img/icon-normal.svg";
    }

    if(selected) {
        size = new google.maps.Size(100, 100);
    }
    else {
        size = new google.maps.Size(35, 35);
    }

    return {
        url: iconUrl,
        scaledSize: size
    };
}