(ns protosfrontend.core
  (:require [reagent.core :as reagent]
            [reagent.session :as session]
            [re-frame.core :as rf]
            [ajax.core :as ajax]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [secretary.core :as secretary :include-macros true]
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

;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
  (rf/dispatch [:set-active-page :dashboard-page]))

(secretary/defroute "/apps" []
  (rf/dispatch [:set-active-page :apps-page]))

(secretary/defroute "/apps/:id" {:as params}
  (let [id (:id params)]
   (rf/dispatch [:set-active-page :app-page id])))

(secretary/defroute "/store" []
  (rf/dispatch [:set-active-page :store-page]))

(secretary/defroute "/store/:id" {:as params}
  (let [id (:id params)]
   (rf/dispatch [:set-active-page :store-installer-page id])))

(secretary/defroute "/tasks" []
  (rf/dispatch [:set-active-page :tasks-page]))

(secretary/defroute "/tasks/:id" {:as params}
  (let [id (:id params)]
   (rf/dispatch [:set-active-page :task-page id])))

(secretary/defroute "/resources" []
  (rf/dispatch [:set-active-page :resources-page]))

(secretary/defroute "/resources/:id" {:as params}
  (let [id (:id params)]
   (rf/dispatch [:set-active-page :resource-page id])))

(secretary/defroute "/init" []
  (rf/dispatch [:set-active-page :init-page]))

  (secretary/defroute "/login" []
    (rf/dispatch [:set-active-page :login-page]))

;; -------------------------
;; History
;; must be called after routes have been defined
(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
      EventType/NAVIGATE
      (fn [event]
        (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

;; -------------------------
;; Initialize app

(defn mount-root []
  (rf/dispatch-sync [:initialize [:dashboard-page]])
  (reagent/render [v/current-page] (.getElementById js/document "protos")))

(defn init! []
  (hook-browser-navigation!)
  (mount-root))

(init!)
