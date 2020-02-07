(ns protosfrontend.core
  (:require [reagent.core :as reagent]
            [reagent.session :as session]
            [re-frame.core :as rf]
            [ajax.core :as ajax]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [protosfrontend.routes :as routes]
            [protosfrontend.events :as e]
            [protosfrontend.subs]
            [protosfrontend.views :as v]
            [protosfrontend.ws :as ws]
            [protosfrontend.auth.subs]
            [protosfrontend.auth.events]
            [protosfrontend.init.subs]
            [protosfrontend.init.events]
            [protosfrontend.components.header]
            [protosfrontend.components.cards]
            [protosfrontend.dashboard.subs]
            [protosfrontend.dashboard.events])
  (:import goog.History))

(enable-console-print!)

(defn mount-root []
  (reagent/render [v/current-page] (.getElementById js/document "protos")))

(defn stop []
  (js/console.log "Stopping Protos frontend ..."))

(defn init! []
  (js/console.log "Starting Protos frontend ...")
  (routes/start!)
  (rf/dispatch-sync [:initialize])
  (mount-root))
