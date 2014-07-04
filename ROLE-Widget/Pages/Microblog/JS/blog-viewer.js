
$(document).ready(function () {    
    initBlogSelector();
});

function initBlogSelector() {
    addEntries("JavaBlog");
   
    
}

function addEntries(blog) {   

    $("#content").html("");
    sendRequest("get", "blogs/"+blog, "", function (data) {
        if (data) {

            var xml = $.parseXML(data);
            $xml = $(xml);

            $xml.find("child").each(function (index) {

                sendRequest("get", "entries/" + $(this).attr("id"), "", function (data2) {

                    var xmlInner = $.parseXML(data2);
                    $xmlInner = $(xmlInner).find("resource");

                    var div = document.createElement('div');
                    div.className = "blogContainer";
                    div.id = $xmlInner.attr("id");
                    
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


                    $("#content").append(div);

                });       
               
            });            
        }
    });
}


