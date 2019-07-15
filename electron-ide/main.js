// Modules to control application life and create native browser window
// Set environment for development
const {app, BrowserWindow, ipcMain, Menu, MenuItem} = require('electron')
// loading .env
var envjs = require('loadenvjs')

var config = envjs();
console.log(config)

process.env.NODE_ENV = config.APP_ENV
process.env.APP_NAME = config.APP_NAME = config.APP_NAME || 'Master Tools'

if (config.DEBUG_FLAG == "true") {
    // Install `electron-debug` with `devtron`
    require('electron-debug');
}


// Keep a global reference of the window object, if you don't, the window will
// be closed automatically when the JavaScript object is garbage collected.
let mainWindow
const winURL = "index.html"

function createWindow() {
    console.log("createWindow..")
    // Create the browser window.
    mainWindow = new BrowserWindow({
        title: config.APP_NAME,
        autoHideMenuBar: false,
        show:false,
        focusable:true,
        webPreferences: {
            nodeIntegration: true
        }
    })

    mainWindow.loadFile(winURL)

    mainWindow.on('closed', () => {
        mainWindow = null
    })

    mainWindow.on('ready-to-show', function() {
        mainWindow.show();
    });
}

//app.commandLine.appendSwitch("--disable-http-cache")

// This method will be called when Electron has finished
// initialization and is ready to create browser windows.
// Some APIs can only be used after this event occurs.
app.on('ready', function () {
    console.log("app ready.....")
    createWindow();

    initMenu();

    mainWindow.maximize();

    if (config.APP_ENV == "production") {
        startIde();
        sleep(3000);
    }
})

// Quit when all windows are closed.
app.on('window-all-closed', function () {
    console.log("close.....")
    // On macOS it is common for applications and their menu bar
    // to stay active until the user quits explicitly with Cmd + Q
    if (process.platform !== 'darwin') {
        app.quit()
    }
    if (config.APP_ENV == "production") {
        stopIde()
        sleep(2000)
    }
})

app.on('activate', function () {
    if (mainWindow === null) {
        createWindow()
    }
})

ipcMain.on('close', () => {
    console.log('close')
    console.log(mainWindow.isMaximized())
})

ipcMain.on('reload', (opts) => {
    mainWindow.close()
    if (typeof opts === 'object') {
        Object.assign(winOptions, opts)
    }
    createWindow()
})

ipcMain.on('relaunch', () => {
    app.relaunch({args: process.argv.slice(1).concat(['--relaunch'])})
    // relaunch不会退出当前应用，需要调用exit或者quit
    app.exit(0)
})


// 设置配置
ipcMain.on('get-config', (event) => {
    console.log("receive get-config")
    // 同步返回
    event.returnValue = config
})

ipcMain.on('openwebview', (event, data) => {
    mainWindow.loadFile("webview.html");
})

let childProcess = require('child_process')

function startIde() {
    console.log('startIde')
    childProcess.execFile('start_ide.bat', null, {cwd: config.BAT_DIR}, function (error, stdout, stderr) {
        if (error !== null) {
            console.log('exec error' + error)
        } else console.log('成功')
        console.log('stdout: ' + stdout)
        console.log('stderr: ' + stderr)
    })
}

function stopIde() {
    console.log('stopIde')
    childProcess.execFile('stop_ide.bat', null, {cwd: config.BAT_DIR}, function (error, stdout, stderr) {
        if (error !== null) {
            console.log('exec error' + error)
        } else console.log('成功')
        console.log('stdout: ' + stdout)
        console.log('stderr: ' + stderr)
    })
}

function sleep(milliSeconds) {
    var StartTime = new Date().getTime()
    let i = 0
    while (new Date().getTime() < StartTime + milliSeconds) ;
}

function initMenu() {
    let template = [
        {
            label: '应用',
            submenu: [{
                label: '刷新页面',
                accelerator: (function () {
                    if (process.platform === 'darwin') {
                        return 'Ctrl+Command+F'
                    } else {
                        return 'F5'
                    }
                })(),
                click: function (item, focusedWindow) {
                    mainWindow.webContents.send('sendMsgToRender', {"cmd": "refreshPage"});
                }
            }, {
                label: '切换全屏',
                accelerator: (function () {
                    if (process.platform === 'darwin') {
                        return 'Ctrl+Command+F'
                    } else {
                        return 'F11'
                    }
                })(),
                click: function (item, focusedWindow) {
                    if (focusedWindow) {
                        focusedWindow.setFullScreen(!focusedWindow.isFullScreen())
                    }
                }
            }, {
                label: '调试',
                accelerator: (function () {
                    if (process.platform === 'darwin') {
                        return 'Ctrl+Command+F'
                    } else {
                        return 'F12'
                    }
                })(),
                click: function (item, focusedWindow) {
                    if(mainWindow.webContents.isDevToolsOpened()){
                        mainWindow.webContents.send('sendMsgToRender', {"cmd": "closeDevtooler"});
                        mainWindow.webContents.closeDevTools();
                    }else{
                        mainWindow.webContents.send('sendMsgToRender', {"cmd": "openDevtooler"});
                        mainWindow.webContents.openDevTools();
                    }

                }
            }, {
                label: '清空缓存',
                click: function (item, focusedWindow) {
                    mainWindow.webContents.send('sendMsgToRender', {"cmd": "clearCache"});
                }
            }, {
                label: '重载浏览器',
                accelerator: 'CmdOrCtrl+L',
                click: function (item, focusedWindow) {
                    mainWindow.webContents.send('sendMsgToRender', {"cmd": "clearCache"});
                    app.relaunch({args: process.argv.slice(1).concat(['--relaunch'])})
                    // relaunch不会退出当前应用，需要调用exit或者quit
                    app.exit(0)
                }
            }]
        },

        {
            label: '窗口',
            role: 'window',
            submenu: [{
                label: '最大化',
                accelerator: 'CmdOrCtrl+I',
                click: function (item, focusedWindow) {
                    mainWindow.maximize();
                }
            }, {
                label: '最小化',
                accelerator: 'CmdOrCtrl+M',
                role: 'minimize'
            }, {
                label: '关闭',
                accelerator: 'CmdOrCtrl+W',
                role: 'close'
            }, {
                type: 'separator'
            }]
        }

    ]
    let menu = Menu.buildFromTemplate(template)
    Menu.setApplicationMenu(menu)
}
