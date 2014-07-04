
$(document).ready(function () {    
    initBlogSelector();
});
var iwcClient;
function initBlogSelector() {
    addEntries();
    window.parent.gadgets.util.registerOnLoadHandler(initIWC);

}
function initIWC() {

    // instantiate new instance of ROLE IWC client and 
    // bind to global variable
    iwcClient = new window.parent.iwc.Client();

    // define callback processing incoming ROLE IWC intents
    var iwcCallback = function (intent) {
        // process received ROLE IWCintent
        console.log("Your Widget: received ROLE IWC intent:");
        console.log(intent);
        alert(intent.action);
    };

    // connect callback to ROLE IWC client
    iwcClient.connect(iwcCallback);

}

function addEntries() {   

    $("#content").html("");
    sendRequest("get", "blogs", "", function (data) {
        if (data) {

            var xml = $.parseXML(data);
            $xml = $(xml);

            $xml.find("child").each(function (index) {

                var div = document.createElement('div');
                div.className = "selectItem";
                div.id=$(this).attr("id");
                $(div).text($(this).attr("id"));
                $(div).append("<span style='font-size:small;'>" + " by " + escapeUnsafe($(this).attr("owner")) + "</span>");
                $(div).click(function (event) { loadBlog(event.target.id); });
                $("#content").append(div);
            });            
        }
    });
}
function loadBlog(id) {
    // formulate ROLE IWC intent as JSON object
    var intent = {
        "component": "",
        "data": "http://example.org/some/data",
        "dataType": "text/url-list",
        "action": "ACTION_UPDATE",
        "categories": ["", "cat2"],
        "flags": ["PUBLISH_GLOBAL"],
        "extras": {
            "mykey1": "myvalue1",
            "mykey2": 20
        }
    }
    // first validate formulated intent
    console.log(window.parent.iwc.util);
    if (window.parent.iwc.util.validateIntent(intent)) {
        // publish ROLE IWC intent, if valid.
        iwcClient.publish(intent);
    }
    alert("!");

}



