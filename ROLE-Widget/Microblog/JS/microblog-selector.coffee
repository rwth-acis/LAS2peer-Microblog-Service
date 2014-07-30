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
  iwcManager = new api.IWCManager(iwcCallback);

login = new api.Login(api.LoginTypes.HTTP_BASIC)
login.setUserAndPassword(clientDefaults.userName, clientDefaults.userPassword)
###
  Init RequestSender object with uri and login data.
###
requestSender = new api.RequestSender(clientDefaults.address, login)
isEnteringInput = false;

$(document).ready -> initMicroblogSelector()



initMicroblogSelector = ->
  initEvents()
  fetchEntries()


initEvents = ->
  $("#addEntry").click(() -> addBlogInput())



###
  Inserts input field to create a new microblog.
###
addBlogInput = ->
  
  if isEnteringInput
    return 0
  
  textfieldDiv = document.createElement "div"
  textfieldDiv.id = "textfieldBox"
  textfieldDiv.className = "selectItem"
  $(textfieldDiv).html('<label>Blog Name: </label>'+
  '<input type="text" name="blogName" id="enterBlogName" value="" >')
  $("#content").append textfieldDiv
  ###
    Accept on ENTER
  ###
  $("#enterBlogName").keypress (e) ->
    if e.which is 13
      commitBlogInput()
      return false;  
  isEnteringInput = true
  $("#enterBlogName").focus()

###
  Remove input field and notify service.
  Fetch blog entries after that (update microblog list).
###
commitBlogInput = ->
  name=$("#enterBlogName").val().trim()
  isEnteringInput = false
  $("#textfieldBox").remove()
  
  id = microblogLib.Encoder.formatAsId name
  
  if id.length > 0
    requestSender.sendRequest "put", "blogs/" + id + "?name=" + name, "", () ->
      fetchEntries()

###
  Get all available microblogs from the service
###
fetchEntries = ->  
  $("#content").html ""
  requestSender.sendRequest "get", "blogs", "", (data) ->
    
    xml= $.parseXML data
    $xml = $(xml)
    $xml.find("child").each (index) ->
      
      div = document.createElement "div"
      div.className = "selectItem"
      div.id=$(this).attr "id" 
      $(div).text($(this).attr("name"))
      $(div).append("<span style='font-size:small;' id=" + div.id + ">" + " by " +
      microblogLib.Encoder.escapeUnsafe($(this).attr("owner")) + "</span>");
      ###
        Send intent if a microblog is selected, so other widgets can update their contents.
      ###
      $(div).click (event) -> 
        iwcManager.sendIntent("MICROBLOG_SELECTED", event.target.id) if gadgets?
      $("#content").append div
  , (error) -> alert error