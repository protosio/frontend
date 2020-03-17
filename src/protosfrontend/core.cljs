(ns protosfrontend.core
  (:require [reagent.core :as reagent]
            [reagent.dom :as rdom]
            [re-frame.core :as rf]
            [goog.events]
            [goog.history.EventType]
            [protosfrontend.routes :as routes]
            [protosfrontend.events]
            [protosfrontend.subs]
            [protosfrontend.views :as v]
            [protosfrontend.ws]
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
  (rdom/render [v/current-page] (.getElementById js/document "protos")))

(defn stop []
  (println "Stopping Protos frontend ..."))

(defn init! []
  (println "Starting Protos frontend ...")
  (routes/start!)
  (rf/dispatch-sync [:initialize])
  (mount-root))
