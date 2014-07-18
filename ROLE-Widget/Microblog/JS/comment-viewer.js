
$(document).ready(function () {
    initCommentViewer();
});


var iwcCallback = function (intent) {
    // process received ROLE IWCintent
    if (intent.action === constants.MICROBLOG_ENTRY_SELECTED) {
        
        addEntries(intent.data);

    }
};

iwcClient = new iwc.Client();
var isEnteringInput = false;
var currentEntry = "";
function initCommentViewer() {
    addEvents();
    iwcClient.connect(iwcCallback);
}

function addEvents() {
    $("#addEntry").click(function (event) { addEntryInput(); });

}

function addEntryInput() {
    if (isEnteringInput) return;
    if (currentEntry.length == 0) return;


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
        sendRequest("post", "comments/" + currentEntry, entry, function (data) {
            addEntries(currentEntry);
        });
    }
}

function addEntries(entry) {
    currentEntry = entry;
    $("#content").html("");
    sendRequest("get", "entries/" + entry, "", function (data) {
        if (data) {

            var xml = $.parseXML(data);
            $xml = $(xml);

            $xml.find("child").each(function (index) {

                sendRequest("get", "comments/" + $(this).attr("id"), "", function (data2) {

                    var xmlInner = $.parseXML(data2);
                    $xmlInner = $(xmlInner).find("resource");

                    var div = document.createElement('div');
                    div.className = "blogContainer";
                    div.id = $xmlInner.attr("id");
                    $(div).attr("timestamp", $xmlInner.attr("timestamp"));
                    var currentTimestamp = parseFloat($xmlInner.attr("timestamp"));

                    var author = document.createElement('div');
                    author.className = "authorItem";
                    
                    $(author).text($xmlInner.attr("owner"));

                    var time = document.createElement('div');
                    time.className = "dateItem";

                    $(time).text($xmlInner.attr("time"));
                    $(author).append(time);

                    var text = document.createElement('div');
                    text.className = "textItem";
                    



                    $(text).text($xmlInner.html());
                    $(div).append(author);
                    $(div).append(text);


                    var added = false;
                    $("#content").children().each(function (index2) {
                        var stamp = parseFloat($(this).attr("timestamp"));

                        if (added == false && currentTimestamp < stamp) {
                            added = true;
                            $(this).before(div);
                        }
                    });

                    if (!added) {

                        $("#content").append(div);
                    }

                });

            });
        }
    });
}


