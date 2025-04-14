import React, { useEffect, useState, useRef } from "react";
import { Client, IMessage } from "@stomp/stompjs";

interface WebSocketMessage {
  content: string;
  timestamp: Date;
}

export default function WebSocketComponent() {
  const [messages, setMessages] = useState<WebSocketMessage[]>([]);
  const [inputMessage, setInputMessage] = useState("");
  const [isConnected, setIsConnected] = useState(false);
  const stompClientRef = useRef<Client | null>(null);

  // Função para enviar mensagem
  function sendMessage() {
    if (stompClientRef.current && isConnected && inputMessage.trim()) {
      stompClientRef.current.publish({
        destination: "/sender/polls",
        body: inputMessage,
        headers: { "content-type": "text/plain" },
      });
      setInputMessage("");
    }
  }

  // Efeito para gerenciar a conexão WebSocket
  useEffect(() => {
    const client = new Client({
      brokerURL: "ws://localhost:8081/ws",
      debug: (str) => console.log("[STOMP]", str),
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      webSocketFactory: () => new WebSocket("ws://localhost:8081/ws") as any,
    });

    client.onConnect = (frame) => {
      console.log("Conectado:", frame);
      setIsConnected(true);

      client.subscribe("/receiver/polls", (message: IMessage) => {
        setMessages((prev) => [
          ...prev,
          {
            content: message.body,
            timestamp: new Date(),
          },
        ]);
      });
    };

    client.onDisconnect = () => {
      setIsConnected(false);
    };

    client.activate();
    stompClientRef.current = client;

    return () => {
      client.deactivate();
    };
  }, []);

  return (
    <div style={styles.container}>
      <h2 style={styles.title}>WebSocket STOMP</h2>

      <div
        style={{
          ...styles.status,
          background: isConnected ? "#d4edda" : "#f8d7da",
        }}
      >
        Status: {isConnected ? "✅ Conectado" : "❌ Desconectado"}
      </div>

      <div style={styles.inputContainer}>
        <input
          type="text"
          value={inputMessage}
          onChange={(e) => setInputMessage(e.target.value)}
          onKeyPress={(e) => e.key === "Enter" && sendMessage()}
          style={styles.input}
          placeholder="Digite sua mensagem"
        />
        <button
          onClick={sendMessage}
          disabled={!isConnected}
          style={styles.button}
        >
          Enviar
        </button>
      </div>

      <div style={styles.messagesContainer}>
        <h3>Mensagens:</h3>
        {messages.length === 0 ? (
          <p>Nenhuma mensagem recebida</p>
        ) : (
          <ul style={styles.messagesList}>
            {messages.map((msg, index) => (
              <li key={index} style={styles.messageItem}>
                <span style={styles.timestamp}>
                  [{msg.timestamp.toLocaleTimeString()}]
                </span>
                {msg.content}
              </li>
            ))}
          </ul>
        )}
      </div>
    </div>
  );
}

// Estilos separados para melhor organização
const styles = {
  container: {
    padding: "20px",
    maxWidth: "600px",
    margin: "0 auto",
    fontFamily: "Arial, sans-serif",
  },
  title: {
    color: "#333",
    textAlign: "center" as const,
  },
  status: {
    padding: "10px",
    borderRadius: "4px",
    margin: "10px 0",
    textAlign: "center" as const,
  },
  inputContainer: {
    margin: "20px 0",
    display: "flex",
    gap: "10px",
  },
  input: {
    padding: "8px",
    flex: 1,
    borderRadius: "4px",
    border: "1px solid #ddd",
  },
  button: {
    padding: "8px 15px",
    background: "#007bff",
    color: "white",
    border: "none",
    borderRadius: "4px",
    cursor: "pointer",
  },
  messagesContainer: {
    border: "1px solid #ddd",
    padding: "15px",
    borderRadius: "4px",
  },
  messagesList: {
    listStyle: "none",
    padding: 0,
    margin: 0,
  },
  messageItem: {
    padding: "8px 0",
    borderBottom: "1px solid #eee",
    display: "flex",
    gap: "10px",
  },
  timestamp: {
    color: "#666",
    fontSize: "0.8em",
  },
};
