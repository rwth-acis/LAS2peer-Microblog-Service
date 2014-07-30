# CoffeeScript

# requires moduleHelper.js
# requires JQuery 2.1.1
@module "i5", ->
  @module "las2peer", ->
    @module "microblog", ->
    
      ###
        Formatting helper
      ###
      class @Encoder
        ###
          Escapes text so it does not get executed (transforms to plain text).
        ###
        @escapeUnsafe: (unsafe)->
            $('<span></span>').text(unsafe).html()
        
        ###
          Converts a string to a compatible id, by replacing spaces with _ and removing special characters.
        ###
        @formatAsId: (text)->
          rep = text.replace(/\ /g, '_').replace(/\W/g, '')
      
        ###
          Helps to create various elements of the microblog pages.
        ###
      class @ElementGenerator
        
        @generateEntryElements: (xmlInnerArr, clickevent, parent) ->
          for k,i in xmlInnerArr      
            div = document.createElement 'div'
            div.className = "blogContainer"
            div.id = k.attr "id"
      
            author = document.createElement 'div'
            author.className = "authorItem"
            $(author).text(k.attr("owner"))
            time = document.createElement 'div'
            time.className = "dateItem"
            $(time).text(k.attr("time"))
            $(author).append time 
        
            text = document.createElement 'div'
            text.className = "textItem"
            text.id = div.id
            author.id = div.id
            $(text).text(k.html());
            $(div).append author
            $(div).append text
            $(div).click (event) -> 
              clickevent event.target.id if clickevent?
            $(parent).append(div)
            
        @generateMultilineTextfield: (parent) ->
          textfieldDiv = document.createElement "div"
          textfieldDiv.id = "textfieldBox"
          textfieldDiv.className = "selectItem multilineText"
          $(textfieldDiv).html('<textarea name="blogEntry" id="enterBlogEntry"'+
          'value=""/><input type="button" id="sendEntryButton" name="no" value="Send"></input>')
          $(parent).append textfieldDiv