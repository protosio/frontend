(ns protosfrontend.core
  (:require [reagent.core :as reagent]
            [reagent.session :as session]
            [re-frame.core :as rf]
            [ajax.core :as ajax]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [secretary.core :as secretary :include-macros true]
            [protosfrontend.events :as e]
            [protosfrontend.subs :as s]
            [protosfrontend.views :as v])
  (:import goog.History))

(enable-console-print!)

;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
  (rf/dispatch [:set-active-page [:apps-page] [:get-apps]]))

(secretary/defroute "/apps/:id" {:as params}
  (let [id (:id params)]
   (rf/dispatch [:set-active-page [:app-page id] [:get-app id]])))

(secretary/defroute "/installers" []
  (rf/dispatch [:set-active-page [:installers-page] [:get-installers]]))

(secretary/defroute "/installers/:id" {:as params}
  (let [id (:id params)]
   (rf/dispatch [:set-active-page [:installer-page id] [:get-installer id]])))

(secretary/defroute "/resources" []
  (rf/dispatch [:set-active-page [:resources-page] [:get-resources]]))

(secretary/defroute "/about" []
  (rf/dispatch [:set-active-page [:about-page]]))

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
  (rf/dispatch-sync [:initialize [:apps-page]])
  (reagent/render [v/current-page] (.getElementById js/document "app")))

(defn init! []
  (hook-browser-navigation!)
  (mount-root))

(init!)
