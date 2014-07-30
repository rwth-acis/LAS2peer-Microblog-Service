# CoffeeScript
# CoffeeScript
# using
api=i5.las2peer.jsAPI
microblogLib=i5.las2peer.microblog

###
  See microblog-viewer for comments.
###
if gadgets?
  iwcCallback = (intent)->    
    if intent.action is "MICROBLOG_ENTRY_SELECTED"
      fetchEntries intent.data
    if intent.action is "MICROBLOG_SELECTED"
      $("#content").html("")
  iwcManager = new api.IWCManager(iwcCallback);

login = new api.Login(api.LoginTypes.HTTP_BASIC)

login.setUserAndPassword(clientDefaults.userName, clientDefaults.userPassword)

requestSender = new api.RequestSender(clientDefaults.address, login)
isEnteringInput = false
currentEntry = ""



$(document).ready -> initMicroblogCommentViewer()

initMicroblogCommentViewer = ->
  initEvents()
  

initEvents = ->
  $("#addEntry").click(() -> addEntryInput())
  
addEntryInput = ->
  
  if isEnteringInput or currentEntry.length is 0
    return 0
  
  microblogLib.ElementGenerator.generateMultilineTextfield($("#content"))
  $("#sendEntryButton").click (e) ->
    addEntry()
    
  isEnteringInput = true
  $("#enterBlogEntry").focus()

addEntry = ->
  entry = $("#enterBlogEntry").val().trim()
  isEnteringInput = false
  $("#textfieldBox").remove()
  if entry.length > 0 
    requestSender.sendRequest "post", "comments/" + currentEntry, entry, (data) ->
      fetchEntries(currentEntry)

fetchEntries = (entry) ->
  currentEntry = entry
  $("#content").html("")
  requestSender.sendRequest "get", "entries/"+entry, "", (data) ->
    
    xml = $.parseXML data
    $xml = $(xml)
    $("#header .title").text($xml.find("resource").attr("name"))
    
    requests = []
    $xmlInner = []    
    
    xmlCallback = (childData) ->
      xmlInner = $.parseXML childData        
      $xmlInner[0] = $(xmlInner).find "resource"      
    
    $xml.find("child").each (index) ->
      requests[index] = new api.Request "get","comments/" + $(this).attr("id"), "", (childData) ->
        xmlInner = $.parseXML childData        
        $xmlInner[index] = $(xmlInner).find "resource" 
        
    requestSender.sendRequestsAsync requests , ->
      microblogLib.ElementGenerator.generateEntryElements($xmlInner, null,  $("#content"))
          


  