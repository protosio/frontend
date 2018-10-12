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
            [auth.subs]
            [auth.events]
            [init.subs]
            [init.events]
            [components.header]
            [components.cards]
            [dashboard.subs]
            [dashboard.events])
  (:import goog.History))

(enable-console-print!)

(defn mount-root []
  (reagent/render [v/current-page] (.getElementById js/document "protos")))

(defn init! []
  (rf/dispatch-sync [:initialize])
  (routes/app-routes)
  (mount-root))

(init!)
