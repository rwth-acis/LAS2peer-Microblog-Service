
$(document).ready(function () {    
    initBlogSelector();
});

var iwcCallback = function (intent) {
    // process received ROLE IWCintent
    if (intent.action === constants.MICROBLOG_SELECTED) {
        // alert(JSON.stringify(intent));
        // alert(intent.data);
    }
};


iwcClient = new iwc.Client();

var isEnteringInput = false;

function initBlogSelector() {
    addEvents();
    addEntries();
    
    iwcClient.connect(iwcCallback);
    

}

function addEvents() {
    $("#addEntry").click(function (event) { addBlogInput(); });

}

function addBlogInput() {
    if (isEnteringInput) return;

    var textfieldDiv = document.createElement('div');
    textfieldDiv.id = "textfieldBox";
    textfieldDiv.className="selectItem";
    $(textfieldDiv).html('<label>Blog Name: </label> <input type="text" name="blogName" id="enterBlogName" value="" >');
    $("#content").append(textfieldDiv);
   
    $("#enterBlogName").keypress(function (e) {
        if (e.which == 13) {
            addBlog();
            return false;  
        }
    });

    isEnteringInput = true;
    $("#enterBlogName").focus();

}
function addBlog() {
    
    var name = $("#enterBlogName").val().trim();
    isEnteringInput = false;
    $("#textfieldBox").remove();
    
    var id = name.replace(/ /g, '_');
    id = id.replace(/\W/g, '');
    if (id.length > 0) {
        sendRequest("put", "blogs/" + id + "?name=" + name, "", function (data) {
            addEntries();
        });
    }


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
                $(div).text($(this).attr("name"));
                $(div).append("<span style='font-size:small;'>" + " by " + escapeUnsafe($(this).attr("owner")) + "</span>");
                $(div).click(function (event) { loadBlog(event.target.id); });
                $("#content").append(div);
            });            
        }
    });
}

function loadBlog(id)
{
   
    // formulate ROLE IWC intent as JSON object
    var intent = {
        "component": "",
        "data": id,
        "dataType": "text/url-list",
        "action": constants.MICROBLOG_SELECTED,
        "categories": ["", "cat2"],
        "flags": ["PUBLISH_GLOBAL"],
        "extras": {
            
        }
       
        
    };
    // first validate formulated intent
    //console.log(iwc.util);
    if (iwc.util.validateIntent(intent)) {
        // publish ROLE IWC intent, if valid.
        iwcClient.publish(intent);
    }
}

