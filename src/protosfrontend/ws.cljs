(ns protosfrontend.ws
  (:require-macros
    [cljs.core.async.macros :as asyncm :refer (go go-loop)])
  (:require [cljs.core.async :as async :refer (<! chan)]
            [chord.client :refer [ws-ch]]
            [protosfrontend.util :as util]
            [cemerick.url :refer (url)]
            [re-frame.core :as rf]
            [re-frame.db :refer [app-db]]
            [clojure.walk :refer [keywordize-keys]]))


(defn- receive-msgs!
  [server-ch]
  ;; every time we get a message from the server, trigger the appropriate re-frame event
  (rf/dispatch [:ws-connected true])
  (go-loop []
    (let [{:keys [message error] :as msg} (<! server-ch)]
      (cond
        error (rf/dispatch [:ws-error error])
        (nil? message) (rf/dispatch [:ws-connected false])
        message (rf/dispatch [:ws-message message]))
      (when message
        (recur)))))

(defn- websocket-url
  []
  (str (if (= "https:" (-> js/document .-location .-protocol))
         "wss://"
         "ws://")
       (-> js/document .-location .-host)
       (util/createurl ["e" "ws"])))

(rf/reg-fx
  :init-ws
  (fn init-ws-handler
    []
    (if (:ws-connected @app-db)
      (println "Websocket connection already established")
      (go
        (println "Initiating websocket connection")
        (let [{:keys [ws-channel error]} (<! (ws-ch (str (url (websocket-url)))
                                              {:format :json
                                               :read-ch (chan 10)}))]
          (if error
            ;; if error just print it
            (rf/dispatch [:ws-error (str "Could not establish WS connection: " error)])
            ;; start the receive loop
            (receive-msgs! ws-channel)))))
    {}))


; (trace-forms {:tracer (tracer :color "pink")}

(rf/reg-event-fx
  :ws-message
  (fn ws-message-handler
    [_ [_ msg]]
    (let [message (keywordize-keys msg)]
      (if (= (get message :MsgType) "update")
          {:dispatch [:ws-update (get message :PayloadType) (get message :PayloadValue)]}
          {:dispatch [:ws-error (str "Unknown WS message type: " (get message :MsgType))]}))))

(rf/reg-event-fx
  :ws-update
  (fn ws-update-handler
    [{db :db} [_ type value]]
    (let [id (keyword (get value :id))]
      (condp = type
        "task" {:dispatch [:save-task id value]}
        "app" {:dispatch [:save-app id value]}
        "resource" {:db (assoc-in db [:resources id] value)}
        {:dispatch [:ws-error (str "Unknown WS payload type: " type)]}))))

(rf/reg-event-db
  :ws-error
  (fn ws-error-handler
    [db [_ error]]
    (assoc-in db [:errors] (str "WS error: " error))))

(rf/reg-event-db
  :ws-connected
  (fn ws-connected-handler
    [db [_ connected?]]
    (assoc db :ws-connected connected?)))
