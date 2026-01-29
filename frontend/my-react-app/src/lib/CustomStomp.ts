import { useEffect, useRef, useState, useCallback } from "react";
import { Client, type IMessage, type StompSubscription } from "@stomp/stompjs";

interface ChannelOptions<TReceive, TSend = TReceive> {
  url: string;
  subscribeDestination: string;
  sendDestination: string;
  parse?: (raw: any) => TReceive;
}

export function createStompChannelHook<TReceive, TSend = TReceive>(
  options: ChannelOptions<TReceive, TSend>
) {
  const { url, subscribeDestination, sendDestination, parse } = options;

  return function useStompChannel() {
    const clientRef = useRef<Client | null>(null);
    const [connected, setConnected] = useState(false);
    const [messages, setMessages] = useState<TReceive[]>([]);

    

    useEffect(() => {

      setMessages([]);

      const client = new Client({
        brokerURL: url,
        reconnectDelay: 5000,
        onConnect: () => {
          setConnected(true);

          const sub: StompSubscription = client.subscribe(
            subscribeDestination,
            (msg: IMessage) => {
              const raw = JSON.parse(msg.body);
              const parsed = parse ? parse(raw) : (raw as TReceive);
              setMessages([parsed]);
            }
          );

          (client as any)._sub = sub;
        },
        onDisconnect: () => setConnected(false),
        onStompError: (frame) => {
          console.error("STOMP error:", frame.headers["message"], frame.body);
        },
      });

      client.activate();
      clientRef.current = client;

      return () => {
    const sub: StompSubscription | undefined = (client as any)._sub;
    sub?.unsubscribe();
    client.deactivate();
    setMessages([]);
      };
    }, [url, subscribeDestination]);

    const send = useCallback(
      (msg: TSend) => {
        if (!clientRef.current || !connected) return;
        clientRef.current.publish({
          destination: sendDestination,
          body: JSON.stringify(msg),
        });
      },
      [connected, sendDestination]
    );

    return { connected, messages, send };
  };
}