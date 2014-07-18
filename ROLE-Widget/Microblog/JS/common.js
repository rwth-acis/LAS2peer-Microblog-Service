var baseURI = "http://localhost:8080/microblog";
var constants = {
    "MICROBLOG_SELECTED": "MICROBLOG_SELECTED",
    "MICROBLOG_ENTRY_SELECTED": "MICROBLOG_ENTRY_SELECTED"
};


function getBaseURI() {
    return baseURI;
}
function sendRequest(method, URI, content, callback) {
    var requestURI = encodeURI(getBaseURI() + "/" + URI);
   $.ajax ({
        url: requestURI,

        type: method.toUpperCase(),
        data: content,
        contentType: "text/plain; charset=UTF-8",
        crossDomain: true,
        beforeSend: function (xhr) { xhr.setRequestHeader("Authorization", "Basic " + getBasicAuthLogin()); },

        error: function (xhr, errorType, error) {
            var errorText = error;
            if (xhr.responseText != null && xhr.responseText.trim().length > 0)
                errorText = xhr.responseText;
            if (xhr.status == 0) {

                errorText = "WebConnector does not respond";
            }

            outputError(xhr.status, method, requestURI, errorText);
           
        },
        success: function (data, status, xhr) {           
            callback(xhr.responseText);
        },
    });

   
    
}
function escapeUnsafe(unsafe) {
    return $('<span></span>').text(unsafe).html();
}

function getBasicAuthLogin() {
    return B64.encode("User A:userAPass");
}

function outputError(status, method, uri, errorText) {
    var output = status + " " + errorText + "\n" + method.toUpperCase() + " " + uri + "\n";
    alert(output);
}


