
$(document).ready(function () {    
    initBlogViewer();
});

var iwcCallback = function (intent) {
    // process received ROLE IWCintent
    if (intent.action === constants.MICROBLOG_SELECTED) {
        addEntries(intent.data);
        
    }
};


iwcClient = new iwc.Client();

var isEnteringInput = false;
var currentBlog = "";
function initBlogViewer() {
    
    addEvents();
    iwcClient.connect(iwcCallback);   
}

function addEvents() {
    $("#addEntry").click(function (event) { addEntryInput(); });

}
function addEntryInput() {
    if (isEnteringInput) return;
    if (currentBlog.length == 0) return;


    var textfieldDiv = document.createElement('div');
    textfieldDiv.id = "textfieldBox";
    textfieldDiv.className = "selectItem multilineText";
    $(textfieldDiv).html('<textarea name="blogEntry" id="enterBlogEntry" value=""/><input type="button" id="sendEntryButton" name="no" value="Send"></input>');
    $("#content").append(textfieldDiv);

    $("#sendEntryButton").click(function (e) {
       
            addEntry();
                  
    });

    isEnteringInput = true;
    $("#enterBlogEntry").focus();
}

function addEntry() {
    var entry = $("#enterBlogEntry").val().trim();
    isEnteringInput = false;
    $("#textfieldBox").remove();
    if (entry.length > 0) {
        sendRequest("post", "blogs/" + currentBlog ,entry, function (data) {
            addEntries(currentBlog);
        });
    }
}
function addEntries(blog) {   

    currentBlog = blog;
    $("#content").html("");
    
    
    sendRequest("get", "blogs/"+blog, "", function (data) {
        if (data) {

            var xml = $.parseXML(data);
            $xml = $(xml);
            $("#header .title").text($xml.find("resource").attr("name"));
            
            $xml.find("child").each(function (index) {

                sendRequest("get", "entries/" + $(this).attr("id"), "", function (data2) {

                    var xmlInner = $.parseXML(data2);
                    $xmlInner = $(xmlInner).find("resource");

                    var div = document.createElement('div');
                    div.className = "blogContainer";
                    div.id = $xmlInner.attr("id");
                    $(div).attr("timestamp",$xmlInner.attr("timestamp"));
                    var currentTimestamp=parseFloat($xmlInner.attr("timestamp"));
                    
                    var author = document.createElement('div');
                    author.className = "authorItem";
                    
                    $(author).text($xmlInner.attr("owner"));

                    var time = document.createElement('div');
                    time.className = "dateItem";
                    
                    $(time).text($xmlInner.attr("time"));
                    $(author).append(time);

                    var text = document.createElement('div');
                    text.className = "textItem";
                    

                    text.id = div.id;
                    author.id = div.id;
                    
                    $(text).text($xmlInner.html());
                    $(div).append(author);
                    $(div).append(text);
                    $(div).click(function (event) { loadComments(event.target.id); });

                    
                    var added=false;
                    $("#content").children().each(function (index2) {
                        var stamp = parseFloat($(this).attr("timestamp"));
                        
                        if(added==false && currentTimestamp<stamp)
                        {
                            added = true;
                            $(this).before(div);
                        }
                    });

                    if(!added)
                    {
                        
                        $("#content").append(div);
                    }

                    
                    
                });       
               
            });            
        }
    });
}

function loadComments(id)
{
    
    // formulate ROLE IWC intent as JSON object
    var intent = {
        "component": "",
        "data": id,
        "dataType": "text/url-list",
        "action": constants.MICROBLOG_ENTRY_SELECTED,
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


