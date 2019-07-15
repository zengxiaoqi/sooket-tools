// inyector.js// Get the ipcRenderer of electron
const electron = require('electron');
let ipcRenderer = electron.ipcRenderer;
//receive msg from render
ipcRenderer.on('request', function(event,data){
    console.log("recive request")
    receiveEventFromBrowser(event,data);
});


/**
 * Simple function to return the source path of all the scripts in the document
 * of the <webview>
 *
 *@returns {String}
 **/

global.receiveEventFromBrowser = (event,data) => {
    console.log("receiveEventFromBrowser in window_sdk.js")
}

global.sendEventToBrowser = (data) => {
    console.log("sendEventToBrowser in window_sdk.js")
    ipcRenderer.sendToHost(data)
}

