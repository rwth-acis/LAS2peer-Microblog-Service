# CoffeeScript
# using
api=i5.las2peer.jsAPI
microblogLib=i5.las2peer.microblog

###
  Check explicitly if gadgets is known, i.e. script is executed in widget environment.
  Allows compatibility as a Web page.
###
if gadgets?
  iwcCallback = (intent)-> 
    ###
      Get the selected blog from intend and fetch the contents.
    ###
    if intent.action is "MICROBLOG_SELECTED"
      fetchEntries intent.data      
  iwcManager = new api.IWCManager(iwcCallback);

login = new api.Login(api.LoginTypes.HTTP_BASIC)

login.setUserAndPassword(clientDefaults.userName, clientDefaults.userPassword)

requestSender = new api.RequestSender(clientDefaults.address, login)
isEnteringInput = false
currentBlog = ""



$(document).ready -> initMicroblogViewer()

initMicroblogViewer = ->
  initEvents()
  

initEvents = ->
  $("#addEntry").click(() -> addEntryInput())
  
###
  Creates multiline input field to create new entries.
###
addEntryInput = ->
  
  if isEnteringInput or currentBlog.length is 0
    return 0
  
  microblogLib.ElementGenerator.generateMultilineTextfield($("#content"))
  $("#sendEntryButton").click (e) ->
    addEntry()
    
  isEnteringInput = true
  $("#enterBlogEntry").focus()
  
###
  Removes input field and notifies service about new entry.
###
addEntry = ->
  entry = $("#enterBlogEntry").val().trim()
  isEnteringInput = false
  $("#textfieldBox").remove()
  if entry.length > 0 
    requestSender.sendRequest "post", "blogs/" + currentBlog, entry, (data) ->
      fetchEntries(currentBlog)
###
  Fetches all entries from a selected blog.
###
fetchEntries = (blog) ->
  currentBlog = blog
  $("#content").html("")
  requestSender.sendRequest "get", "blogs/"+blog, "", (data) ->
    xml = $.parseXML data
    $xml = $(xml)
    $("#header .title").text($xml.find("resource").attr("name"))
    
    requests = []
    $xmlInner = []
    
    
    xmlCallback = (childData) ->
      xmlInner = $.parseXML childData        
      $xmlInner[0] = $(xmlInner).find "resource"      
    
    
    ###
      For each child (i.e.) entry get the respective ressource.
      Create a bunch of requests and use a global variable to store the results.
    ###
    $xml.find("child").each (index) ->
      requests[index] = new api.Request "get","entries/" + $(this).attr("id"), "", (childData) ->
        xmlInner = $.parseXML childData
        $xmlInner[index] = $(xmlInner).find "resource"
        
    ###
      Send all requests asynchroniusly and when they are done, create new elements in the html.
    ###
    requestSender.sendRequestsAsync requests , ->
      microblogLib.ElementGenerator.generateEntryElements($xmlInner, loadComments, $("#content"))

###
  Notify the comment-viewer, when a microblog entry is selected.
###
loadComments = (id) ->
  iwcManager.sendIntent("MICROBLOG_ENTRY_SELECTED", id) if gadgets?
  