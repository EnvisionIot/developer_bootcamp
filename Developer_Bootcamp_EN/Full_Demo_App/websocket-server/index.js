/* © 2020 Envision Digital. All Rights Reserved. */

const WebSocket = require("ws");

const wss = new WebSocket.Server({
  host: "127.0.0.1",
  port: "8765"
});

wss.on("connection", (ws) => {
  console.log("websocket server is connected");
  ws.on("message", (message) => {
    wss.clients.forEach((client) => {
      client.send(message);
    });
    console.log(message);
  });
});